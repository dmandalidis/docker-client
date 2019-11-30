/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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

package org.mandas.docker.client.messages.swarm;

import java.util.Map;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableSwarmSpec.Builder.class)
@Immutable
public interface SwarmSpec {

  @Nullable
  @JsonProperty("Name")
  String name();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  @Nullable
  @JsonProperty("Orchestration")
  OrchestrationConfig orchestration();

  @Nullable
  @JsonProperty("Raft")
  RaftConfig raft();

  @Nullable
  @JsonProperty("Dispatcher")
  DispatcherConfig dispatcher();

  @Nullable
  @JsonProperty("CAConfig")
  CaConfig caConfig();

  @Nullable
  @JsonProperty("EncryptionConfig")
  EncryptionConfig encryptionConfig();

  @Nullable
  @JsonProperty("TaskDefaults")
  TaskDefaults taskDefaults();

  interface Builder {
    Builder name(String name);

    Builder labels(Map<String, ? extends String> labels);

    Builder orchestration(OrchestrationConfig orchestration);

    Builder raft(RaftConfig raft);

    Builder dispatcher(DispatcherConfig dispatcher);

    Builder caConfig(CaConfig caConfig);

    Builder encryptionConfig(EncryptionConfig encryptionConfig);

    Builder taskDefaults(TaskDefaults taskDefaults);

    SwarmSpec build();
  }

  public static Builder builder() {
    return ImmutableSwarmSpec.builder();
  }
}
