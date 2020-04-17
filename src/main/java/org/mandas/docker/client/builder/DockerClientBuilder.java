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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.client.Client;

import org.mandas.docker.client.auth.RegistryAuthSupplier;

/**
 * <code>DockerClientBuilder</code> is an interface which has to be implemented from clients
 * when they need to use a JAXRS client implementation other than the provided Jersey
 *   
 * @author Dimitris Mandalidis
 * @see BaseDockerClientBuilder
 */
public interface DockerClientBuilder {

  /**
   * @return the URI of the Docker engine
   */
  URI uri();

  /**
   * The Docker API version which will be used.
   * <p>
   * When set it is appended in all client requests 
   * @return the Docker API version to be used
   */
  String apiVersion();

  /**
   * An optional set of custom HTTP headers added in every request
   * @return a set of custom headers
   */
  Map<String, Object> headers();

  /**
   * The supplier of registry authentication information
   * @return a non-nullable supplier
   */
  RegistryAuthSupplier registryAuthSupplier();

  /**
   * The JAX-RS 2.1 Client API implementation that will be used for the request 
   * @return a non-nullable JAX-RS 2.1 client
   */
  Client client();

  
  enum EntityProcessing {
    CHUNKED,
    BUFFERED;
  }
}
