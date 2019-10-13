/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Redacted;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.DockerConfigReader;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents all the auth info for a particular registry.
 *
 * <p>These are sent to docker during authenticated registry operations
 * in the X-Registry-Config header (see {@link RegistryConfigs}).</p>
 *
 * <p>Typically these objects are built by requesting auth information from a
 * {@link org.mandas.docker.client.DockerCredentialHelper}. However, in older less-secure
 * docker versions, these can be written directly into the ~/.docker/config.json file,
 * with the username and password joined with a ":" and base-64 encoded.</p>
 */
@Immutable
public interface RegistryAuth {

  @Nullable
  @JsonProperty("username")
  String username();

  @Nullable
  @Redacted
  @JsonProperty("password")
  String password();

  /**
   * Unused but must be a well-formed email address (e.g. 1234@5678.com).
   */
  @Nullable
  @Redacted
  @JsonProperty("email")
  String email();

  @Nullable
  @JsonProperty("serveraddress")
  String serverAddress();

  @Nullable
  @JsonProperty("identitytoken")
  String identityToken();

  @JsonIgnore
  @Derived
  @Auxiliary
  default Builder toBuilder() {
	  return ImmutableRegistryAuth.builder().from(this);
  }

  /**
   * This function looks for and parses credentials for logging into the Docker registry specified
   * by serverAddress. We first look in ~/.docker/config.json and fallback to ~/.dockercfg. These
   * files are created from running `docker login`.
   *
   * @param serverAddress A string representing the server address
   * @return a {@link Builder}
   * @throws IOException when we can't parse the docker config file
   */
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

  /** Construct a Builder based upon the "auth" field of the docker client config file. */
  public static Builder forAuth(final String auth) {
    // split with limit=2 to catch case where password contains a colon
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
    return ImmutableRegistryAuth.builder();
  }

  interface Builder {

    Builder username(final String username);

    Builder password(final String password);

    Builder email(final String email);

    Builder serverAddress(final String serverAddress);

    Builder identityToken(final String token);

    RegistryAuth build();
  }
}
