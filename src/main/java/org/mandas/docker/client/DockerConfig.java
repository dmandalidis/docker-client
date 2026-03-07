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

package org.mandas.docker.client;

import java.util.Map;

import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.RegistryAuth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the contents of the docker config.json file.
 * 
 * @param credHelpers credential helpers configuration
 * @param auths authentication configurations
 * @param credsStore the credentials store to use
 */
public record DockerConfig(
  @JsonProperty("credHelpers")
  Map<String, String> credHelpers,

  @JsonProperty("auths")
  Map<String, RegistryAuth> auths,

  @Nullable
  @JsonProperty("credsStore")
  String credsStore
) {}
