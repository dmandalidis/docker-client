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

import javax.ws.rs.client.Client;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.mandas.docker.client.DefaultDockerClient;
import org.mandas.docker.client.DockerCertificates;
import org.mandas.docker.client.DockerCertificatesStore;
import org.mandas.docker.client.DockerHost;
import org.mandas.docker.client.LogsResponseReader;
import org.mandas.docker.client.ObjectMapperProvider;
import org.mandas.docker.client.ProgressResponseReader;
import org.mandas.docker.client.UnixConnectionSocketFactory;
import org.mandas.docker.client.auth.ConfigFileRegistryAuthSupplier;
import org.mandas.docker.client.auth.RegistryAuthSupplier;
import org.mandas.docker.client.exceptions.DockerCertificateException;
import org.mandas.docker.client.npipe.NpipeConnectionSocketFactory;

/**
 * A convenience base class for implementing {@link DockerClientBuilder}s
 * @author Dimitris Mandalidis
 * @param <B> the type of the builder
 */
public abstract class BaseDockerClientBuilder<B extends BaseDockerClientBuilder<B>> implements DockerClientBuilder<B> {

  protected String UNIX_SCHEME = "unix";
  protected String NPIPE_SCHEME = "npipe";
  protected long DEFAULT_CONNECT_TIMEOUT_MILLIS = SECONDS.toMillis(5);
  protected long DEFAULT_READ_TIMEOUT_MILLIS = SECONDS.toMillis(30);
  protected int DEFAULT_CONNECTION_POOL_SIZE = 100;
  protected String ERROR_MESSAGE = "LOGIC ERROR: DefaultDockerClient does not support being built "
        + "with both `registryAuth` and `registryAuthSupplier`. "
        + "Please build with at most one of these options.";
  protected URI uri;
  protected String apiVersion;
  protected long connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
  protected long readTimeoutMillis = DEFAULT_READ_TIMEOUT_MILLIS;
  protected int connectionPoolSize = DEFAULT_CONNECTION_POOL_SIZE;
  protected DockerCertificatesStore dockerCertificatesStore;
  protected boolean useProxy = true;
  protected RegistryAuthSupplier registryAuthSupplier;
  protected Map<String, Object> headers = new HashMap<>();
  protected Client client;
  protected EntityProcessing entityProcessing;

  private B self() {
    return (B) this;
  }
  
  /**
   * Sets or overwrites {@link #uri()} and {@link #dockerCertificates(DockerCertificatesStore)} according to the values
   * present in DOCKER_HOST and DOCKER_CERT_PATH environment variables.
   *
   * @return Modifies a builder that can be used to further customize and then build the client.
   * @throws DockerCertificateException if we could not build a DockerCertificates object
   */
  @Override
  public B fromEnv() throws DockerCertificateException {
    final String endpoint = DockerHost.endpointFromEnv();
    final Path dockerCertPath = Paths.get(asList(certPathFromEnv(), configPathFromEnv(), defaultCertPath())
        .stream()
        .filter(cert -> cert != null)
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("Cannot find docker certificated path")));
  
    final Optional<DockerCertificatesStore> certs = DockerCertificates.builder().dockerCertPath(dockerCertPath).build();
  
    if (endpoint.startsWith(UNIX_SCHEME + "://")) {
      this.uri(endpoint);
    } else if (endpoint.startsWith(NPIPE_SCHEME + "://")) {
      this.uri(endpoint);
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
      this.uri(initialUri);
    }
  
    if (certs.isPresent()) {
      this.dockerCertificates(certs.get());
    }
  
    return self();
  }

  @Override
  public B uri(final URI uri) {
    this.uri = uri;
    return self();
  }

  /**
   * Set the URI for connections to Docker.
   *
   * @param uri URI String for connections to Docker
   * @return Builder
   */
  public B uri(final String uri) {
    return uri(URI.create(uri));
  }

  /**
   * Set the Docker API version that will be used in the HTTP requests to Docker daemon.
   *
   * @param apiVersion String for Docker API version
   * @return Builder
   */
  @Override
  public B apiVersion(final String apiVersion) {
    this.apiVersion = apiVersion;
    return self();
  }

  @Override
  public B connectTimeoutMillis(final long connectTimeoutMillis) {
    this.connectTimeoutMillis = connectTimeoutMillis;
    return self();
  }

  @Override
  public B readTimeoutMillis(final long readTimeoutMillis) {
    this.readTimeoutMillis = readTimeoutMillis;
    return self();
  }

  @Override
  public B dockerCertificates(final DockerCertificatesStore dockerCertificatesStore) {
    this.dockerCertificatesStore = dockerCertificatesStore;
    return self();
  }

  @Override
  public B connectionPoolSize(final int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
    return self();
  }

  @Override
  public B useProxy(final boolean useProxy) {
    this.useProxy = useProxy;
    return self();
  }

  @Override
  public B registryAuthSupplier(final RegistryAuthSupplier registryAuthSupplier) {
    if (this.registryAuthSupplier != null) {
      throw new IllegalStateException(ERROR_MESSAGE);
    }
    this.registryAuthSupplier = registryAuthSupplier;
    return self();
  }

  @Override
  public B header(String name, Object value) {
    headers.put(name, value);
    return self();
  }

  @Override
  public URI uri() {
    return uri;
  }

  @Override
  public B entityProcessing(final EntityProcessing entityProcessing) {
    this.entityProcessing = entityProcessing;
    return self();
  }
  
  private String toRegExp(String hostnameWithWildcards) {
    return hostnameWithWildcards.replace(".", "\\.").replace("*", ".*");
  }
  
  protected abstract Client createClient();
  
  protected ProxyConfiguration proxyFromEnv() {
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

  @Override
  public DefaultDockerClient build() {
    requireNonNull(uri, "uri");
    requireNonNull(uri.getScheme(), "url has null scheme");
    
    if ((dockerCertificatesStore != null) && !uri.getScheme().equals("https")) {
      throw new IllegalArgumentException(
          "An HTTPS URI for DOCKER_HOST must be provided to use Docker client certificates");
    }
    
    this.client = createClient()
        .register(ObjectMapperProvider.class)
        .register(LogsResponseReader.class)
        .register(ProgressResponseReader.class);
    
    if (uri.getScheme().equals(UNIX_SCHEME)) {
      this.uri = UnixConnectionSocketFactory.sanitizeUri(uri);
    } else if (uri.getScheme().equals(NPIPE_SCHEME)) {
      this.uri = NpipeConnectionSocketFactory.sanitizeUri(uri);
    }
    
    // read the docker config file for auth info if nothing else was specified
    if (registryAuthSupplier == null) {
      registryAuthSupplier(new ConfigFileRegistryAuthSupplier());
    }
    
    return new DefaultDockerClient(apiVersion, registryAuthSupplier, uri, client, headers);
  }

  protected HttpClientConnectionManager getConnectionManager(URI uri, Registry<ConnectionSocketFactory> schemeRegistry, int connectionPoolSize) {
    if (uri.getScheme().equals(NPIPE_SCHEME)) {
      final BasicHttpClientConnectionManager bm = 
          new BasicHttpClientConnectionManager(schemeRegistry);
      return bm;
    }
    final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(schemeRegistry);
    // Use all available connections instead of artificially limiting ourselves to 2 per server.
    cm.setMaxTotal(connectionPoolSize);
    cm.setDefaultMaxPerRoute(cm.getMaxTotal());
    return cm;
  }

  protected Registry<ConnectionSocketFactory> getSchemeRegistry(URI uri, DockerCertificatesStore certificateStore) {
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
