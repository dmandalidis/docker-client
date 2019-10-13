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

import java.util.List;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * An object that represents the JSON returned by the Docker API for an exec command's process
 * configuration.
 */
@JsonDeserialize(builder = ImmutableProcessConfig.Builder.class)
@Immutable
public interface ProcessConfig {

  @JsonProperty("privileged")
  Boolean privileged();

  @Nullable
  @JsonProperty("user")
  String user();

  @JsonProperty("tty")
  Boolean tty();

  @JsonProperty("entrypoint")
  String entrypoint();

  @JsonProperty("arguments")
  List<String> arguments();
}
