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

import org.mandas.docker.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Object representing a host's proxy configuration
 * @author Dimitris Mandalidis
 * @see DockerClientBuilder#proxyFromEnv()
 * @param host the host running the proxy (FQDN)
 * @param port the port to which the proxy listens (network port)
 * @param username an optional username in case of authenticated access
 * @param password an optional password in case of authenticated access
 */
public record ProxyConfiguration(
    String host,
    String port,
    @Nullable
    String username,
    @Nullable
    String password) {

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String host;
    private String port;
    private String username;
    private String password;

    /**
     * Set the host running the proxy 
     * @param host the FQDN
     * @return this
     */
    public Builder host(String host) {
      this.host = host;
      return this;
    }

    /**
     * Set the port to which the proxy listens 
     * @param port a network port
     * @return this
     */
    public Builder port(String port) {
      this.port = port;
      return this;
    }

    /**
     * Set the user in case of authenticated access 
     * @param username the username
     * @return this
     */
    public Builder username(String username) {
      this.username = username;
      return this;
    }

    /**
     * Set the password of the user in case of authenticated access 
     * @param password the password of the user
     * @return this
     */
    public Builder password(String password) {
      this.password = password;
      return this;
    }

    /**
     * Build this configuration
     * @return a new {@link ProxyConfiguration}
     */
    public ProxyConfiguration build() {
      return new ProxyConfiguration(host, port, username, password);
    }
  }

  /**
   * Get a {@link ProxyConfiguration} builder
   * @return a new builder
   */
  static Builder builder() {
    return new Builder();
  }
}
