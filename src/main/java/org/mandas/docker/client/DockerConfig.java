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

package org.mandas.docker.client;

import java.util.Map;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.RegistryAuth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents the contents of the docker config.json file.
 */
@Immutable
@JsonDeserialize(builder = ImmutableDockerConfig.Builder.class)
public interface DockerConfig {

  @JsonProperty("credHelpers")
  Map<String, String> credHelpers();

  @JsonProperty("auths")
  Map<String, RegistryAuth> auths();

  @JsonProperty("HttpHeaders")
  Map<String, String> httpHeaders();

  @Nullable
  @JsonProperty("credsStore")
  String credsStore();

  @Nullable
  @JsonProperty("detachKeys")
  String detachKeys();

  @Nullable
  @JsonProperty("stackOrchestrator")
  String stackOrchestrator();

  @Nullable
  @JsonProperty("psFormat")
  String psFormat();

  @Nullable
  @JsonProperty("imagesFormat")
  String imagesFormat();
}
