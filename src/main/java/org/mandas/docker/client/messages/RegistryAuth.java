/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.Base64;

import org.mandas.docker.Nullable;
import org.mandas.docker.client.DockerConfigReader;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegistryAuth(
  @Nullable
  @JsonProperty("username")
  String username,

  @Nullable
  @JsonProperty("password")
  String password,

  @Nullable
  @JsonProperty("email")
  String email,

  @Nullable
  @JsonProperty("serveraddress")
  String serverAddress,

  @Nullable
  @JsonProperty("identitytoken")
  String identityToken
) {

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static Builder fromDockerConfig(final String serverAddress) throws IOException {
    DockerConfigReader dockerCfgReader = new DockerConfigReader();
    return dockerCfgReader
        .authForRegistry(dockerCfgReader.defaultConfigPath(), serverAddress).toBuilder();
  }

  @JsonCreator
  public static RegistryAuth create(@JsonProperty("username") final String username,
                                    @JsonProperty("password") final String password,
                                    @JsonProperty("email") final String email,
                                    @JsonProperty("serveraddress") final String serveraddress,
                                    @JsonProperty("identitytoken") final String identitytoken,
                                    @JsonProperty("auth") final String auth) {

    final Builder builder;
    if (auth != null) {
      builder = forAuth(auth);
    } else {
      builder = builder()
          .username(username)
          .password(password);
    }
    return builder
        .email(email)
        .serverAddress(serveraddress)
        .identityToken(identitytoken)
        .build();
  }

  public static Builder forAuth(final String auth) {
    byte[] authByteValue = Base64.getDecoder().decode(auth);
    final String[] authParams = new String(authByteValue, UTF_8).split(":", 2);

    if (authParams.length != 2) {
      return builder();
    }

    return builder()
        .username(authParams[0].trim())
        .password(authParams[1].trim());
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String username;
    private String password;
    private String email;
    private String serverAddress;
    private String identityToken;

    public Builder() {
    }

    public Builder(RegistryAuth auth) {
      this.username = auth.username;
      this.password = auth.password;
      this.email = auth.email;
      this.serverAddress = auth.serverAddress;
      this.identityToken = auth.identityToken;
    }

    public Builder username(final String username) {
      this.username = username;
      return this;
    }

    public Builder password(final String password) {
      this.password = password;
      return this;
    }

    public Builder email(final String email) {
      this.email = email;
      return this;
    }

    public Builder serverAddress(final String serverAddress) {
      this.serverAddress = serverAddress;
      return this;
    }

    public Builder identityToken(final String token) {
      this.identityToken = token;
      return this;
    }

    public RegistryAuth build() {
      return new RegistryAuth(username, password, email, serverAddress, identityToken);
    }
  }
}
