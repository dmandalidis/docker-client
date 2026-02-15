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
 * @param httpHeaders HTTP headers to include in requests
 * @param credsStore the credentials store to use
 * @param detachKeys the detach keys configuration
 * @param stackOrchestrator the stack orchestrator configuration
 * @param psFormat the format string for ps command output
 * @param imagesFormat the format string for images command output
 */
public record DockerConfig(
  @JsonProperty("credHelpers")
  Map<String, String> credHelpers,

  @JsonProperty("auths")
  Map<String, RegistryAuth> auths,

  @JsonProperty("HttpHeaders")
  Map<String, String> httpHeaders,

  @Nullable
  @JsonProperty("credsStore")
  String credsStore,

  @Nullable
  @JsonProperty("detachKeys")
  String detachKeys,

  @Nullable
  @JsonProperty("stackOrchestrator")
  String stackOrchestrator,

  @Nullable
  @JsonProperty("psFormat")
  String psFormat,

  @Nullable
  @JsonProperty("imagesFormat")
  String imagesFormat
) {}
