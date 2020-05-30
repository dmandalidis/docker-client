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

import java.net.URI;

import org.mandas.docker.client.DefaultDockerClient;
import org.mandas.docker.client.DockerCertificatesStore;
import org.mandas.docker.client.auth.RegistryAuthSupplier;
import org.mandas.docker.client.exceptions.DockerCertificateException;

/**
 * <code>DockerClientBuilder</code> is an interface which has to be implemented from clients
 * when they need to use a JAXRS client implementation other than the provided Jersey
 *   
 * @author Dimitris Mandalidis
 * @param <B> the type of the builder
 * @see BaseDockerClientBuilder
 */
public interface DockerClientBuilder<B extends DockerClientBuilder<B>> {

  /**
   * @return the URI of the Docker engine
   */
  URI uri();

  enum EntityProcessing {
    CHUNKED,
    BUFFERED;
  }

  /**
   * Sets or overwrites {@link #uri()} and {@link #dockerCertificates(DockerCertificatesStore)} according to the values
   * present in DOCKER_HOST and DOCKER_CERT_PATH environment variables.
   *
   * @return Modifies a builder that can be used to further customize and then build the client.
   * @throws DockerCertificateException if we could not build a DockerCertificates object
   */
  B fromEnv() throws DockerCertificateException;

  B uri(URI uri);

  B uri(String uri);
  
  DefaultDockerClient build();

  /**
   * Adds additional headers to be sent in all requests to the Docker Remote API.
   * @param name the header name
   * @param value the header value
   * @return this
   */
  B header(String name, Object value);

  B registryAuthSupplier(RegistryAuthSupplier registryAuthSupplier);

  /**
   * Set the size of the connection pool for connections to Docker. Note that due to a known
   * issue, DefaultDockerClient maintains two separate connection pools, each of which is capped
   * at this size. Therefore, the maximum number of concurrent connections to Docker may be up to
   * 2 * connectionPoolSize.
   *
   * @param connectionPoolSize connection pool size
   * @return Builder
   */
  B connectionPoolSize(int connectionPoolSize);

  /**
   * Allows connecting to Docker Daemon using HTTP proxy.
   *
   * @param useProxy tells if Docker Client has to connect to docker daemon using HTTP Proxy
   * @return Builder
   */
  B useProxy(boolean useProxy);

  /**
   * Set the Docker API version that will be used in the HTTP requests to Docker daemon.
   *
   * @param apiVersion String for Docker API version
   * @return Builder
   */
  B apiVersion(String apiVersion);

  /**
   * Set the timeout in milliseconds until a connection to Docker is established. A timeout value
   * of zero is interpreted as an infinite timeout.
   *
   * @param connectTimeoutMillis connection timeout to Docker daemon in milliseconds
   * @return Builder
   */
  B connectTimeoutMillis(long connectTimeoutMillis);

  /**
   * Set the SO_TIMEOUT in milliseconds. This is the maximum period of inactivity between
   * receiving two consecutive data packets from Docker.
   *
   * @param readTimeoutMillis read timeout to Docker daemon in milliseconds
   * @return Builder
   */
  B readTimeoutMillis(long readTimeoutMillis);

  /**
   * Provide certificates to secure the connection to Docker.
   *
   * @param dockerCertificatesStore DockerCertificatesStore object
   * @return Builder
   */
  B dockerCertificates(DockerCertificatesStore dockerCertificatesStore);

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
  B entityProcessing(EntityProcessing entityProcessing);
}
