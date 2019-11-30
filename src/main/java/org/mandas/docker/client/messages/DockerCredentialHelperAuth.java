/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
 * Copyright (C) 9/2019 - 2020 Dimitris Mandalidis
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

import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents the auth response received from a docker credential helper
 * on a "get" operation, or sent to a credential helper on a "store".
 *
 * <p>See {@link org.mandas.docker.client.DockerCredentialHelper}.</p>
 */
@JsonDeserialize(builder = ImmutableDockerCredentialHelperAuth.Builder.class)
@Immutable
public interface DockerCredentialHelperAuth {
  @JsonProperty("Username")
  String username();

  @JsonProperty("Secret")
  String secret();

  @Nullable
  @JsonProperty("ServerURL")
  String serverUrl();

  interface Builder {
	  Builder username(String username);
	  
	  Builder secret(String secret);
	  
	  Builder serverUrl(String serverUrl);
	  
	  DockerCredentialHelperAuth build();
  }
  
  static Builder builder() {
	  return ImmutableDockerCredentialHelperAuth.builder();
  }
  
  @JsonIgnore
  @Derived
  default RegistryAuth toRegistryAuth() {
    return RegistryAuth.builder()
        .username(username())
        .password(secret())
        .serverAddress(serverUrl())
        .build();
  }
}
