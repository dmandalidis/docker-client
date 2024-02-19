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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * An object that represents the JSON returned by the Docker API for low-level information about
 * exec commands.
 */
@JsonDeserialize(builder = ImmutableExecState.Builder.class)
@Immutable
public interface ExecState {

  @JsonProperty("ID")
  String id();

  @JsonProperty("Running")
  Boolean running();

  @Nullable
  @JsonProperty("ExitCode")
  Long exitCode();

  @JsonProperty("ProcessConfig")
  ProcessConfig processConfig();

  @JsonProperty("OpenStdin")
  Boolean openStdin();

  @JsonProperty("OpenStdout")
  Boolean openStdout();

  @JsonProperty("OpenStderr")
  Boolean openStderr();

  @Nullable
  @JsonProperty("Container")
  ContainerInfo container();

  @Nullable
  @JsonProperty("ContainerID")
  String containerId();
}
