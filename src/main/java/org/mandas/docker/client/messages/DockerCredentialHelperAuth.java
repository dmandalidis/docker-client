/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
 * Copyright (C) 9/2019 - now Dimitris Mandalidis
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

package org.mandas.docker.client.messages;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DockerCredentialHelperAuth(
  @JsonProperty("Username")
  String username,

  @JsonProperty("Secret")
  String secret,

  @Nullable
  @JsonProperty("ServerURL")
  String serverUrl
) {

  
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String username;
    private String secret;
    private String serverUrl;

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder secret(String secret) {
      this.secret = secret;
      return this;
    }

    public Builder serverUrl(String serverUrl) {
      this.serverUrl = serverUrl;
      return this;
    }

    public DockerCredentialHelperAuth build() {
      return new DockerCredentialHelperAuth(username, secret, serverUrl);
    }
  }
  
  @JsonIgnore
  public RegistryAuth toRegistryAuth() {
    return RegistryAuth.builder()
        .username(username)
        .password(secret)
        .serverAddress(serverUrl)
        .build();
  }
}
