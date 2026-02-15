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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ServiceSpec(
  @Nullable
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels,

  @JsonProperty("TaskTemplate")
  TaskSpec taskTemplate,

  @Nullable
  @JsonProperty("Mode")
  ServiceMode mode,

  @Nullable
  @JsonProperty("UpdateConfig")
  UpdateConfig updateConfig,

  @Deprecated
  @Nullable
  @JsonProperty("Networks")
  List<NetworkAttachmentConfig> networks,

  @Nullable
  @JsonProperty("EndpointSpec")
  EndpointSpec endpointSpec
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private Map<String, String> labels;
    private TaskSpec taskTemplate;
    private ServiceMode mode;
    private UpdateConfig updateConfig;
    private List<NetworkAttachmentConfig> networks;
    private EndpointSpec endpointSpec;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder addLabel(final String label, final String value) {
      if (this.labels == null) {
        this.labels = new HashMap<>();
      } else {
        this.labels = new HashMap<>(this.labels);
      }
      this.labels.put(label, value);
      return this;
    }

    public Builder labels(Map<String, String> labels) {
      this.labels = labels == null ? null : Map.copyOf(labels);
      return this;
    }

    public Builder taskTemplate(TaskSpec taskTemplate) {
      this.taskTemplate = taskTemplate;
      return this;
    }

    public Builder mode(ServiceMode mode) {
      this.mode = mode;
      return this;
    }

    public Builder updateConfig(UpdateConfig updateConfig) {
      this.updateConfig = updateConfig;
      return this;
    }

    public Builder networks(NetworkAttachmentConfig... networks) {
      this.networks = networks == null ? null : List.of(networks);
      return this;
    }

    public Builder networks(List<NetworkAttachmentConfig> networks) {
      this.networks = networks;
      return this;
    }

    public Builder endpointSpec(EndpointSpec endpointSpec) {
      this.endpointSpec = endpointSpec;
      return this;
    }

    public ServiceSpec build() {
      return new ServiceSpec(name, labels == null ? null : Map.copyOf(labels), 
          taskTemplate, mode, updateConfig, 
          networks == null ? null : List.copyOf(networks), endpointSpec);
    }
  }
}
