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

package org.mandas.docker.client.messages.swarm;

import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SwarmSpec(
  @Nullable
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels,

  @Nullable
  @JsonProperty("Orchestration")
  OrchestrationConfig orchestration,

  @Nullable
  @JsonProperty("Raft")
  RaftConfig raft,

  @Nullable
  @JsonProperty("Dispatcher")
  DispatcherConfig dispatcher,

  @Nullable
  @JsonProperty("CAConfig")
  CaConfig caConfig,

  @Nullable
  @JsonProperty("EncryptionConfig")
  EncryptionConfig encryptionConfig,

  @Nullable
  @JsonProperty("TaskDefaults")
  TaskDefaults taskDefaults
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private Map<String, String> labels;
    private OrchestrationConfig orchestration;
    private RaftConfig raft;
    private DispatcherConfig dispatcher;
    private CaConfig caConfig;
    private EncryptionConfig encryptionConfig;
    private TaskDefaults taskDefaults;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder labels(Map<String, String> labels) {
      this.labels = labels == null ? null : Map.copyOf(labels);
      return this;
    }

    public Builder orchestration(OrchestrationConfig orchestration) {
      this.orchestration = orchestration;
      return this;
    }

    public Builder raft(RaftConfig raft) {
      this.raft = raft;
      return this;
    }

    public Builder dispatcher(DispatcherConfig dispatcher) {
      this.dispatcher = dispatcher;
      return this;
    }

    public Builder caConfig(CaConfig caConfig) {
      this.caConfig = caConfig;
      return this;
    }

    public Builder encryptionConfig(EncryptionConfig encryptionConfig) {
      this.encryptionConfig = encryptionConfig;
      return this;
    }

    public Builder taskDefaults(TaskDefaults taskDefaults) {
      this.taskDefaults = taskDefaults;
      return this;
    }

    public SwarmSpec build() {
      return new SwarmSpec(name, labels == null ? null : Map.copyOf(labels), 
          orchestration, raft, dispatcher, caConfig, encryptionConfig, taskDefaults);
    }
  }
}
