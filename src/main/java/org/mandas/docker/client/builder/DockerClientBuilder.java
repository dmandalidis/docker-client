/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2019-2020 Dimitris Mandalidis
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
*/
package org.mandas.docker.client.builder;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mandas.docker.client.DockerHost.certPathFromEnv;
import static org.mandas.docker.client.DockerHost.configPathFromEnv;
import static org.mandas.docker.client.DockerHost.defaultAddress;
import static org.mandas.docker.client.DockerHost.defaultCertPath;
import static org.mandas.docker.client.DockerHost.defaultPort;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.mandas.docker.client.DefaultDockerClient;
import org.mandas.docker.client.DockerCertificates;
import org.mandas.docker.client.DockerCertificatesStore;
import org.mandas.docker.client.DockerHost;
import org.mandas.docker.client.ObjectMapperProvider;
import org.mandas.docker.client.UnixConnectionSocketFactory;
import org.mandas.docker.client.auth.ConfigFileRegistryAuthSupplier;
import org.mandas.docker.client.auth.RegistryAuthSupplier;
import org.mandas.docker.client.exceptions.DockerCertificateException;
import org.mandas.docker.client.npipe.NpipeConnectionSocketFactory;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * Docker client builder
 * @author Dimitris Mandalidis
 */
public class DockerClientBuilder {

  public enum EntityProcessing {
    CHUNKED,
    BUFFERED;
  }
  
  private static String UNIX_SCHEME = "unix";
  private static String NPIPE_SCHEME = "npipe";
  private long DEFAULT_CONNECT_TIMEOUT_MILLIS = SECONDS.toMillis(5);
  private long DEFAULT_READ_TIMEOUT_MILLIS = SECONDS.toMillis(30);
  private int DEFAULT_CONNECTION_POOL_SIZE = 100;
  private URI uri;
  private URI sanitizedUri;
  private String apiVersion;
  private long connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
  private long readTimeoutMillis = DEFAULT_READ_TIMEOUT_MILLIS;
  private int connectionPoolSize = DEFAULT_CONNECTION_POOL_SIZE;
  private DockerCertificatesStore dockerCertificatesStore;
  private boolean useProxy = true;
  private RegistryAuthSupplier registryAuthSupplier;
  private Map<String, Object> headers = new HashMap<>();
  private Client client;
  private EntityProcessing entityProcessing;

  private ClientConfig updateProxy(ClientConfig config) {
    ProxyConfiguration proxyConfiguration = proxyFromEnv();
    if (proxyConfiguration == null) {
      return config;
    }
    
    String proxyHost = proxyConfiguration.host();
    
    config.property(ClientProperties.PROXY_URI, (!proxyHost.startsWith("http") ? "http://" : "")
            + proxyHost + ":" + proxyConfiguration.port());
    
    if (proxyConfiguration.username() != null) {
      config.property(ClientProperties.PROXY_USERNAME, proxyConfiguration.username());
    }
    if (proxyConfiguration.password() != null) {
      config.property(ClientProperties.PROXY_PASSWORD, proxyConfiguration.password());
    }

    //ensure Content-Length is populated before sending request via proxy.
    config.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
    return config;
  }
  
  private Client createClient() {
    Registry<ConnectionSocketFactory> schemeRegistry = getSchemeRegistry(uri, dockerCertificatesStore);
    
    final HttpClientConnectionManager cm = getConnectionManager(uri, schemeRegistry, connectionPoolSize);

    final RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout((int) connectTimeoutMillis)
        .setConnectTimeout((int) connectTimeoutMillis)
        .setSocketTimeout((int) readTimeoutMillis)
        .build();

    ClientConfig config = new ClientConfig(JacksonFeature.class);
    
    if (useProxy) {
      config = updateProxy(config);
    }
    
    config
      .connectorProvider(new ApacheConnectorProvider())
      .property(ApacheClientProperties.CONNECTION_MANAGER, cm)
      .property(ApacheClientProperties.CONNECTION_MANAGER_SHARED, "true")
      .property(ApacheClientProperties.REQUEST_CONFIG, requestConfig);

    if (entityProcessing != null) {
      switch (entityProcessing) {
        case BUFFERED:
          config.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
          break;
        case CHUNKED:
          config.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.CHUNKED);
          break;
        default:
          throw new IllegalArgumentException("Invalid entity processing mode " + entityProcessing);
      }
    }

    return ClientBuilder.newBuilder()
        .withConfig(config)
        .build();
  }
  
  /**
   * Sets or overwrites {@link #uri()} and {@link #dockerCertificates(DockerCertificatesStore)} according to the values
   * present in DOCKER_HOST and DOCKER_CERT_PATH environment variables.
   *
   * @return Modifies a builder that can be used to further customize and then build the client.
   * @throws DockerCertificateException if we could not build a DockerCertificates object
   */
  public static DockerClientBuilder fromEnv() throws DockerCertificateException {
    final String endpoint = DockerHost.endpointFromEnv();
    final Path dockerCertPath = Paths.get(asList(certPathFromEnv(), configPathFromEnv(), defaultCertPath())
        .stream()
        .filter(cert -> cert != null)
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("Cannot find docker certificated path")));
  
    final Optional<DockerCertificatesStore> certs = DockerCertificates.builder().dockerCertPath(dockerCertPath).build();
  
    URI uri = null;
    if (endpoint.startsWith(UNIX_SCHEME + "://")) {
      uri = URI.create(endpoint);
    } else if (endpoint.startsWith(NPIPE_SCHEME + "://")) {
      uri = URI.create(endpoint);
    } else {
      final String stripped = endpoint.replaceAll(".*://", "");
      final String scheme = certs.isPresent() ? "https" : "http";
      URI initialUri = URI.create(scheme + "://" + stripped);
      if (initialUri.getPort() == -1 && initialUri.getHost() == null) {
          initialUri = URI.create(scheme + "://" + defaultAddress() + ":" + defaultPort());
      } else if (initialUri.getHost() == null) {
          initialUri = URI.create(scheme + "://" + defaultAddress()+ ":" + initialUri.getPort());
      } else if (initialUri.getPort() == -1) {
          initialUri = URI.create(scheme + "://" + initialUri.getHost() + ":" + defaultPort());
      }
      uri = initialUri;
    }
  
    if (certs.isPresent()) {
      return new DockerClientBuilder(uri, certs.get());
    }
  
    return new DockerClientBuilder(uri);
  }

  private DockerClientBuilder(final URI uri) {
    this(uri, null);
  }
  
  private DockerClientBuilder(final URI uri, final DockerCertificatesStore certs) {
    this.uri = uri;
    this.dockerCertificatesStore = certs;
  }
  
  public DockerClientBuilder uri(final URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Set the URI for connections to Docker.
   *
   * @param uri URI String for connections to Docker
   * @return Builder
   */
  public DockerClientBuilder uri(final String uri) {
    return uri(URI.create(uri));
  }

  /**
   * Set the Docker API version that will be used in the HTTP requests to Docker daemon.
   *
   * @param apiVersion String for Docker API version
   * @return Builder
   */
  public DockerClientBuilder apiVersion(final String apiVersion) {
    this.apiVersion = apiVersion;
    return this;
  }

  /**
   * Set the timeout in milliseconds until a connection to Docker is established. A timeout value
   * of zero is interpreted as an infinite timeout.
   *
   * @param connectTimeoutMillis connection timeout to Docker daemon in milliseconds
   * @return Builder
   */
  public DockerClientBuilder connectTimeoutMillis(final long connectTimeoutMillis) {
    this.connectTimeoutMillis = connectTimeoutMillis;
    return this;
  }

  /**
   * Set the SO_TIMEOUT in milliseconds. This is the maximum period of inactivity between
   * receiving two consecutive data packets from Docker.
   *
   * @param readTimeoutMillis read timeout to Docker daemon in milliseconds
   * @return Builder
   */
  public DockerClientBuilder readTimeoutMillis(final long readTimeoutMillis) {
    this.readTimeoutMillis = readTimeoutMillis;
    return this;
  }

  /**
   * Provide certificates to secure the connection to Docker.
   *
   * @param dockerCertificatesStore DockerCertificatesStore object
   * @return Builder
   */
  public DockerClientBuilder dockerCertificates(final DockerCertificatesStore dockerCertificatesStore) {
    this.dockerCertificatesStore = dockerCertificatesStore;
    return this;
  }

  /**
   * Set the size of the connection pool for connections to Docker. Note that due to a known
   * issue, DefaultDockerClient maintains two separate connection pools, each of which is capped
   * at this size. Therefore, the maximum number of concurrent connections to Docker may be up to
   * 2 * connectionPoolSize.
   *
   * @param connectionPoolSize connection pool size
   * @return Builder
   */
  public DockerClientBuilder connectionPoolSize(final int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
    return this;
  }

  /**
   * Allows connecting to Docker Daemon using HTTP proxy.
   *
   * @param useProxy tells if Docker Client has to connect to docker daemon using HTTP Proxy
   * @return Builder
   */
  public DockerClientBuilder useProxy(final boolean useProxy) {
    this.useProxy = useProxy;
    return this;
  }

  public DockerClientBuilder registryAuthSupplier(final RegistryAuthSupplier registryAuthSupplier) {
    this.registryAuthSupplier = registryAuthSupplier;
    return this;
  }

  /**
   * Adds additional headers to be sent in all requests to the Docker Remote API.
   * @param name the header name
   * @param value the header value
   * @return this
   */
  public DockerClientBuilder header(String name, Object value) {
    headers.put(name, value);
    return this;
  }

  /**
   * @return the URI of the Docker engine
   * @deprecated this will be removed
   */
  @Deprecated
  public URI uri() {
    return uri;
  }

  /**
   * Allows setting transfer encoding. CHUNKED does not send the content-length header 
   * while BUFFERED does.
   * 
   * <p>By default ApacheConnectorProvider uses CHUNKED mode. Some Docker API end-points 
   * seems to fail when no content-length is specified but a body is sent.
   * 
   * @param entityProcessing is the requested entity processing to use when calling docker
   *     daemon (tcp protocol).
   * @return Builder
   */
  public DockerClientBuilder entityProcessing(final EntityProcessing entityProcessing) {
    this.entityProcessing = entityProcessing;
    return this;
  }
  
  private String toRegExp(String hostnameWithWildcards) {
    return hostnameWithWildcards.replace(".", "\\.").replace("*", ".*");
  }
  
  private ProxyConfiguration proxyFromEnv() {
    final String proxyHost = System.getProperty("http.proxyHost");
    if (proxyHost == null) {
      return null;
    }
    
    String nonProxyHosts = System.getProperty("http.nonProxyHosts");
    if (nonProxyHosts != null) {
      // Remove quotes, if any. Refer to https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html
      String[] nonProxy = nonProxyHosts
          .replaceAll("^\\s*\"", "")
          .replaceAll("\\s*\"$", "")
          .split("\\|");
      String host = ofNullable(uri.getHost()).orElse("localhost");
      for (String h: nonProxy) {
        if (host.matches(toRegExp(h))) {
          return null;
        }
      }
    }
    
    return ProxyConfiguration.builder()
      .host(proxyHost)
      .port(System.getProperty("http.proxyPort"))
      .username(System.getProperty("http.proxyUser"))
      .password(System.getProperty("http.proxyPassword"))
      .build();
  }

  public DefaultDockerClient build() {
    requireNonNull(uri, "uri");
    requireNonNull(uri.getScheme(), "url has null scheme");
    
    if ((dockerCertificatesStore != null) && !uri.getScheme().equals("https")) {
      throw new IllegalArgumentException(
          "An HTTPS URI for DOCKER_HOST must be provided to use Docker client certificates");
    }
    
    if (uri.getScheme().startsWith(UNIX_SCHEME) || uri.getScheme().startsWith(NPIPE_SCHEME)) {
      this.useProxy = false;
    }
    
    this.client = createClient()
        .register(ObjectMapperProvider.class);
    
    if (uri.getScheme().equals(UNIX_SCHEME)) {
      this.sanitizedUri = UnixConnectionSocketFactory.sanitizeUri(uri);
    } else if (uri.getScheme().equals(NPIPE_SCHEME)) {
      this.sanitizedUri = NpipeConnectionSocketFactory.sanitizeUri(uri);
    } else {
      this.sanitizedUri = this.uri;
    }
    
    // read the docker config file for auth info if nothing else was specified
    if (registryAuthSupplier == null) {
      registryAuthSupplier(new ConfigFileRegistryAuthSupplier());
    }
    
    return new DefaultDockerClient(apiVersion, registryAuthSupplier, sanitizedUri, client, headers);
  }

  private HttpClientConnectionManager getConnectionManager(URI uri, Registry<ConnectionSocketFactory> schemeRegistry, int connectionPoolSize) {
    if (uri.getScheme().equals(NPIPE_SCHEME)) {
      return new BasicHttpClientConnectionManager(schemeRegistry);
    }
    final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(schemeRegistry);
    // Use all available connections instead of artificially limiting ourselves to 2 per server.
    cm.setMaxTotal(connectionPoolSize);
    cm.setDefaultMaxPerRoute(cm.getMaxTotal());
    return cm;
  }

  private Registry<ConnectionSocketFactory> getSchemeRegistry(URI uri, DockerCertificatesStore certificateStore) {
    final SSLConnectionSocketFactory https;
    if (dockerCertificatesStore == null) {
      https = SSLConnectionSocketFactory.getSocketFactory();
    } else {
      https = new SSLConnectionSocketFactory(dockerCertificatesStore.sslContext(),
                                             dockerCertificatesStore.hostnameVerifier());
    }
  
    final RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
        .<ConnectionSocketFactory>create()
        .register("https", https)
        .register("http", PlainConnectionSocketFactory.getSocketFactory());
  
    if (uri.getScheme().equals(UNIX_SCHEME)) {
      registryBuilder.register(UNIX_SCHEME, new UnixConnectionSocketFactory(uri));
    }
    
    if (uri.getScheme().equals(NPIPE_SCHEME)) {
      registryBuilder.register(NPIPE_SCHEME, new NpipeConnectionSocketFactory(uri));
    }
  
    return registryBuilder.build();
  }
}
