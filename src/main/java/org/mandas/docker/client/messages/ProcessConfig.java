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

import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An object that represents the JSON returned by the Docker API for an exec command's process
 * configuration.
 * 
 * @param privileged whether the process runs in privileged mode
 * @param user the user to run the process as
 * @param tty whether to allocate a pseudo-TTY
 * @param entrypoint the entrypoint for the process
 * @param arguments the arguments for the process
 */
public record ProcessConfig(
  @JsonProperty("privileged")
  Boolean privileged,

  @Nullable
  @JsonProperty("user")
  String user,

  @JsonProperty("tty")
  Boolean tty,

  @JsonProperty("entrypoint")
  String entrypoint,

  @JsonProperty("arguments")
  List<String> arguments
) {}
