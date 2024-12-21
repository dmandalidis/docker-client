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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

/**
 * Object representing a host's proxy configuration
 * @author Dimitris Mandalidis
 * @see DockerClientBuilder#proxyFromEnv()
 */
@Immutable
public interface ProxyConfiguration {

  /**
   * @return the host running the proxy
   */
  String host();
  
  /**
   * @return the port to which the proxy listens
   */
  String port();
  
  /**
   * @return an optional username in case of authenticated access
   */
  @Nullable
  String username();
  
  /**
   * @return an optional password in case of authenticated access
   */
  @Nullable
  String password();
  
  interface Builder {
    /**
     * Set the host running the proxy 
     * @param host the FQDN
     * @return this
     */
    Builder host(String host);
    /**
     * Set the port to which the proxy listens 
     * @param port a network port
     * @return this
     */
    Builder port(String port);
    /**
     * Set the user in case of authenticated access 
     * @param username the username
     * @return this
     */
    Builder username(String username);
    /**
     * Set the password of the user in case of authenticated access 
     * @param password the password of the user
     * @return this
     */
    Builder password(String password);
    /**
     * Build this configuration
     * @return a new {@link ProxyConfiguration}
     */
    ProxyConfiguration build();
  }
  
  /**
   * Get a {@link ProxyConfiguration} builder
   * @return a new builder
   */
  static Builder builder() {
    return ImmutableProxyConfiguration.builder();
  }
}
