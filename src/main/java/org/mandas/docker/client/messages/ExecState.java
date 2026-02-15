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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An object that represents the JSON returned by the Docker API for low-level information about
 * exec commands.
 * 
 * @param id the exec command ID
 * @param running whether the exec command is running
 * @param exitCode the exit code of the exec command
 * @param processConfig the process configuration
 * @param openStdin whether stdin is open
 * @param openStdout whether stdout is open
 * @param openStderr whether stderr is open
 * @param container the container information
 * @param containerId the container ID
 */
public record ExecState(
  @JsonProperty("ID")
  String id,

  @JsonProperty("Running")
  Boolean running,

  @Nullable
  @JsonProperty("ExitCode")
  Long exitCode,

  @JsonProperty("ProcessConfig")
  ProcessConfig processConfig,

  @JsonProperty("OpenStdin")
  Boolean openStdin,

  @JsonProperty("OpenStdout")
  Boolean openStdout,

  @JsonProperty("OpenStderr")
  Boolean openStderr,

  @Nullable
  @JsonProperty("Container")
  ContainerInfo container,

  @Nullable
  @JsonProperty("ContainerID")
  String containerId
) {}
