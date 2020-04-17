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
public abstract class BaseDockerClientBuilder<B extends BaseDockerClientBuilder<B>> implements DockerClientBuilder {

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

  /**
   * Sets or overwrites {@link #uri()} and {@link #dockerCertificates()} according to the values
   * present in DOCKER_HOST and DOCKER_CERT_PATH environment variables.
   *
   * @return Modifies a builder that can be used to further customize and then build the client.
   * @throws DockerCertificateException if we could not build a DockerCertificates object
   */
  public BaseDockerClientBuilder<B> fromEnv() throws DockerCertificateException {
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
  
    return this;
  }

  public BaseDockerClientBuilder<B> uri(final URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Set the URI for connections to Docker.
   *
   * @param uri URI String for connections to Docker
   * @return Builder
   */
  public BaseDockerClientBuilder<B> uri(final String uri) {
    return uri(URI.create(uri));
  }

  /**
   * Set the Docker API version that will be used in the HTTP requests to Docker daemon.
   *
   * @param apiVersion String for Docker API version
   * @return Builder
   */
  public BaseDockerClientBuilder<B> apiVersion(final String apiVersion) {
    this.apiVersion = apiVersion;
    return this;
  }

  @Override
  public String apiVersion() {
    return apiVersion;
  }

  /**
   * Set the timeout in milliseconds until a connection to Docker is established. A timeout value
   * of zero is interpreted as an infinite timeout.
   *
   * @param connectTimeoutMillis connection timeout to Docker daemon in milliseconds
   * @return Builder
   */
  public BaseDockerClientBuilder<B> connectTimeoutMillis(final long connectTimeoutMillis) {
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
  public BaseDockerClientBuilder<B> readTimeoutMillis(final long readTimeoutMillis) {
    this.readTimeoutMillis = readTimeoutMillis;
    return this;
  }

  /**
   * Provide certificates to secure the connection to Docker.
   *
   * @param dockerCertificatesStore DockerCertificatesStore object
   * @return Builder
   */
  public BaseDockerClientBuilder<B> dockerCertificates(final DockerCertificatesStore dockerCertificatesStore) {
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
  public BaseDockerClientBuilder<B> connectionPoolSize(final int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
    return this;
  }

  /**
   * Allows connecting to Docker Daemon using HTTP proxy.
   *
   * @param useProxy tells if Docker Client has to connect to docker daemon using HTTP Proxy
   * @return Builder
   */
  public BaseDockerClientBuilder<B> useProxy(final boolean useProxy) {
    this.useProxy = useProxy;
    return this;
  }

  public BaseDockerClientBuilder<B> registryAuthSupplier(final RegistryAuthSupplier registryAuthSupplier) {
    if (this.registryAuthSupplier != null) {
      throw new IllegalStateException(ERROR_MESSAGE);
    }
    this.registryAuthSupplier = registryAuthSupplier;
    return this;
  }

  /**
   * Adds additional headers to be sent in all requests to the Docker Remote API.
   */
  public BaseDockerClientBuilder<B> header(String name, Object value) {
    headers.put(name, value);
    return this;
  }

  @Override
  public Map<String, Object> headers() {
    return headers;
  }

  @Override
  public URI uri() {
    return uri;
  }

  @Override
  public Client client() {
    return client;
  }

  @Override
  public RegistryAuthSupplier registryAuthSupplier() {
    return registryAuthSupplier;
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
  public BaseDockerClientBuilder<B> entityProcessing(final EntityProcessing entityProcessing) {
    this.entityProcessing = entityProcessing;
    return this;
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
    
    return new DefaultDockerClient(this);
  }
}
