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
package org.mandas.docker.client;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
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
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.mandas.docker.client.auth.ConfigFileRegistryAuthSupplier;
import org.mandas.docker.client.auth.RegistryAuthSupplier;
import org.mandas.docker.client.exceptions.DockerCertificateException;
import org.mandas.docker.client.npipe.NpipeConnectionSocketFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class DockerClientBuilder {
  enum RequestProcessingMode {
    CHUNKED,
    BUFFERED
  }
  
  static final String UNIX_SCHEME = "unix";
  static final String NPIPE_SCHEME = "npipe";
  static final long NO_TIMEOUT = 0;
  static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = SECONDS.toMillis(5);
  static final long DEFAULT_READ_TIMEOUT_MILLIS = SECONDS.toMillis(30);
  static final int DEFAULT_CONNECTION_POOL_SIZE = 100;
  
  public static final String ERROR_MESSAGE =
      "LOGIC ERROR: DefaultDockerClient does not support being built "
      + "with both `registryAuth` and `registryAuthSupplier`. "
      + "Please build with at most one of these options.";
  private URI uri;
  private String apiVersion;
  private long connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
  private long readTimeoutMillis = DEFAULT_READ_TIMEOUT_MILLIS;
  private int connectionPoolSize = DEFAULT_CONNECTION_POOL_SIZE;
  private DockerCertificatesStore dockerCertificatesStore;
  private boolean useProxy = true;
  private RegistryAuthSupplier registryAuthSupplier;
  private Map<String, Object> headers = new HashMap<>();
  private RequestProcessingMode requestEntityProcessing;

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
    if (this.registryAuthSupplier != null) {
      throw new IllegalStateException(ERROR_MESSAGE);
    }
    this.registryAuthSupplier = registryAuthSupplier;
    return this;
  }

  /**
   * Adds additional headers to be sent in all requests to the Docker Remote API.
   */
  public DockerClientBuilder header(String name, Object value) {
    headers.put(name, value);
    return this;
  }

  /**
   * Allows setting transfer encoding. CHUNKED does not send the content-length header 
   * while BUFFERED does.
   * 
   * <p>By default ApacheConnectorProvider uses CHUNKED mode. Some Docker API end-points 
   * seems to fail when no content-length is specified but a body is sent.
   * 
   * @param requestEntityProcessing is the requested entity processing to use when calling docker
   *     daemon (tcp protocol).
   * @return Builder
   */
  public DockerClientBuilder useRequestEntityProcessing(
      final RequestProcessingMode requestEntityProcessing) {
    this.requestEntityProcessing = requestEntityProcessing;
    return this;
  }
  
  private HttpClientConnectionManager getConnectionManager(Registry<ConnectionSocketFactory> schemeRegistry, DockerClientBuilder builder) {
    if (builder.uri.getScheme().equals(NPIPE_SCHEME)) {
      final BasicHttpClientConnectionManager bm = new BasicHttpClientConnectionManager(schemeRegistry);
      return bm;
    }
    final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(schemeRegistry);
    // Use all available connections instead of artificially limiting ourselves to 2 per server.
    cm.setMaxTotal(builder.connectionPoolSize);
    cm.setDefaultMaxPerRoute(cm.getMaxTotal());
    return cm;
  }

  private Registry<ConnectionSocketFactory> getSchemeRegistry(final DockerClientBuilder builder) {
    final SSLConnectionSocketFactory https;
    if (builder.dockerCertificatesStore == null) {
      https = SSLConnectionSocketFactory.getSocketFactory();
    } else {
      https = new SSLConnectionSocketFactory(builder.dockerCertificatesStore.sslContext(),
                                             builder.dockerCertificatesStore.hostnameVerifier());
    }

    final RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
        .<ConnectionSocketFactory>create()
        .register("https", https)
        .register("http", PlainConnectionSocketFactory.getSocketFactory());

    if (builder.uri.getScheme().equals(UNIX_SCHEME)) {
      registryBuilder.register(UNIX_SCHEME, new UnixConnectionSocketFactory(builder.uri));
    }
    
    if (builder.uri.getScheme().equals(NPIPE_SCHEME)) {
      registryBuilder.register(NPIPE_SCHEME, new NpipeConnectionSocketFactory(builder.uri));
    }

    return registryBuilder.build();
  }
  
  // TODO: missing https here
  private ProxyConfiguration getProxyConfigurationFor(String host) {
    String proxyHost = System.getProperty("http.proxyHost");
    if (proxyHost != null) {
      String nonProxyHosts = System.getProperty("http.nonProxyHosts");
      if (nonProxyHosts != null) {
          String[] nonProxy = nonProxyHosts
            .replaceAll("^\\s*\"", "")
            .replaceAll("\\s*\"$", "")
            .split("\\|");
          for (String h: nonProxy) {
            if (host.matches(toRegExp(h))) {
              return null;
            }
          }
      }
      String proxyPort = System.getProperty("http.proxyPort");
      return ImmutableProxyConfiguration.builder()
        .proxyHost(proxyHost.replaceAll("^http://", ""))
        .proxyPort(Integer.parseInt(proxyPort))
        .proxyUser(System.getProperty("http.proxyUser"))
        .proxyPassword(System.getProperty("http.proxyPassword"))
        .build();
    }
    return null;
  }
  
  private String toRegExp(String hostnameWithWildcards) {
    return hostnameWithWildcards.replace(".", "\\.").replace("*", ".*");
  }
  
  @SuppressWarnings("resource")
  ClientBuilder newBaseBuilderResteasy(HttpClientConnectionManager cm, ProxyConfiguration proxy, RequestConfig requestConfig, RequestProcessingMode requestEntityProcessing) {
    HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(cm);
    if (proxy != null) {
      Credentials credentials = new UsernamePasswordCredentials(proxy.proxyUser(), proxy.proxyPassword());
      CredentialsProvider credProvider = new BasicCredentialsProvider();
      credProvider.setCredentials(new AuthScope(proxy.proxyHost(), proxy.proxyPort()), credentials);
      clientBuilder
        .setProxy(new HttpHost(proxy.proxyHost(), proxy.proxyPort()))
        .setDefaultCredentialsProvider(credProvider)
        .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
    }
    
    CloseableHttpClient httpClient = clientBuilder
        .setDefaultRequestConfig(requestConfig)
        .build();
    
    ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient);
    if (proxy == null && requestEntityProcessing != null) {
      switch (requestEntityProcessing) {
        case BUFFERED:
          engine.setChunked(false);
          break;
        case CHUNKED:
          engine.setChunked(true);
          break;
        default:
          throw new IllegalStateException("Invalid processing mode " + requestEntityProcessing);
      }
    } else {
      engine.setChunked(true);
    }

    return new ResteasyClientBuilderImpl()
      .httpEngine(engine)
      .register(JacksonJsonProvider.class);
  }
  
//  ClientBuilder newBaseBuilderJersey(HttpClientConnectionManager cm, ProxyConfiguration proxy, RequestConfig requestConfig, RequestProcessingMode requestEntityProcessing) {
//    ClientConfig extraConfig = new ClientConfig()
//        .connectorProvider(new ApacheConnectorProvider())
//        .property(ApacheClientProperties.REQUEST_CONFIG, requestConfig)
//        ;
//    
//    ClientBuilder clientBuilder = ClientBuilder.newBuilder()
//        .withConfig(extraConfig)
//        .register(JacksonFeature.class)
//        .property(ApacheClientProperties.CONNECTION_MANAGER, cm)
//        .property(ApacheClientProperties.CONNECTION_MANAGER_SHARED, "true");
//    
//    if (proxy != null) {
//      clientBuilder.property(ClientProperties.PROXY_URI, "http://" + proxy.proxyHost() + ":" + proxy.proxyPort());
//      clientBuilder.property(ClientProperties.PROXY_USERNAME, proxy.proxyUser());
//      clientBuilder.property(ClientProperties.PROXY_PASSWORD, proxy.proxyPassword());
//      //ensure Content-Length is populated before sending request via proxy.
//      clientBuilder.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
//    }
//    
//    if (requestEntityProcessing != null) {
//      switch (requestEntityProcessing) {
//        case BUFFERED:
//          clientBuilder.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
//          break;
//        case CHUNKED:
//          clientBuilder.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.CHUNKED);
//          break;
//        default:
//          throw new IllegalStateException("Invalid processing mode " + requestEntityProcessing);
//      }
//    }
//    
//    return clientBuilder;
//  }
  
  Client client(URI dockerEngineUri) {
    Registry<ConnectionSocketFactory> schemeRegistry = getSchemeRegistry(this);
    final HttpClientConnectionManager cm = getConnectionManager(schemeRegistry, this);
    ProxyConfiguration proxyConfiguration = getProxyConfigurationFor(Optional.ofNullable(dockerEngineUri.getHost()).orElse("localhost"));
    RequestConfig requestConfig = RequestConfig.custom()
      .setConnectionRequestTimeout((int) connectTimeoutMillis)
      .setConnectTimeout((int) connectTimeoutMillis)
      .setSocketTimeout((int) readTimeoutMillis)
      .build();
    
    ClientBuilder clientBuilder = newBaseBuilderResteasy(cm, useProxy && proxyConfiguration != null? proxyConfiguration: null, requestConfig, requestEntityProcessing)
      .register(ObjectMapperProvider.class)
      .register(LogsResponseReader.class)
      .register(ProgressResponseReader.class)
      .connectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS)
      .readTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS);

    return clientBuilder.build();
  }
  
  public DefaultDockerClient build() {
    requireNonNull(uri, "uri");
    requireNonNull(uri.getScheme(), "url has null scheme");

    if ((dockerCertificatesStore != null) && !uri.getScheme().equals("https")) {
      throw new IllegalArgumentException("An HTTPS URI for DOCKER_HOST must be provided to use Docker client certificates");
    }

    URI dockerEngineUri = null;
    if (uri.getScheme().equals(UNIX_SCHEME)) {
      dockerEngineUri = UnixConnectionSocketFactory.sanitizeUri(uri);
    } else if (uri.getScheme().equals(NPIPE_SCHEME)) {
      dockerEngineUri = NpipeConnectionSocketFactory.sanitizeUri(uri);
    } else {
      dockerEngineUri = uri;
    }

    // read the docker config file for auth info if nothing else was specified
    RegistryAuthSupplier authSupplier = registryAuthSupplier;
    if (authSupplier == null) {
      authSupplier = new ConfigFileRegistryAuthSupplier();
    }
    
    Client client = client(dockerEngineUri);
    
    return new DefaultDockerClient(dockerEngineUri, apiVersion, client, authSupplier, new HashMap<>(headers));
  }
  
  /**
   * Create a new {@link DefaultDockerClient} builder.
   *
   * @return Returns a builder that can be used to further customize and then build the client.
   */
  public static DockerClientBuilder builder() {
    return new DockerClientBuilder();
  }

  /**
   * Create a new {@link DefaultDockerClient} builder prepopulated with values loaded from the
   * DOCKER_HOST and DOCKER_CERT_PATH environment variables.
   *
   * @return Returns a builder that can be used to further customize and then build the client.
   * @throws DockerCertificateException if we could not build a DockerCertificates object
   */
  public static DockerClientBuilder fromEnv() throws DockerCertificateException {
    final String endpoint = DockerHost.endpointFromEnv();
    final Path dockerCertPath = Paths.get(asList(certPathFromEnv(), configPathFromEnv(), defaultCertPath())
    	.stream()
    	.filter(cert -> cert != null)
    	.findFirst()
    	.orElseThrow(() -> new NoSuchElementException("Cannot find docker certificated path")));
  
    final DockerClientBuilder builder = new DockerClientBuilder();
  
    final Optional<DockerCertificatesStore> certs = DockerCertificates.builder()
        .dockerCertPath(dockerCertPath).build();
  
    if (endpoint.startsWith(UNIX_SCHEME + "://")) {
      builder.uri(endpoint);
    } else if (endpoint.startsWith(NPIPE_SCHEME + "://")) {
      builder.uri(endpoint);
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
      builder.uri(initialUri);
    }
  
    if (certs.isPresent()) {
      builder.dockerCertificates(certs.get());
    }
  
    return builder;
  }

  /**
   * Create a new client with default configuration.
   *
   * @param uri The docker rest api uri.
   */
  public static DockerClient newClient(final String uri) {
    return newClient(URI.create(uri.replaceAll("^unix:///", "unix://localhost/")));
  }

  /**
   * Create a new client with default configuration.
   *
   * @param uri The docker rest api uri.
   */
  public static DockerClient newClient(final URI uri) {
    return new DockerClientBuilder().uri(uri).build();
  }

  /**
   * Create a new client with default configuration.
   *
   * @param uri                The docker rest api uri.
   * @param dockerCertificatesStore The certificates to use for HTTPS.
   */
  public static DockerClient newClient(final URI uri, final DockerCertificatesStore dockerCertificatesStore) {
    return new DockerClientBuilder().uri(uri).dockerCertificates(dockerCertificatesStore).build();
  }
}