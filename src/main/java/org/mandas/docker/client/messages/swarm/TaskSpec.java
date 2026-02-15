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

import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskSpec(
  @Nullable
  @JsonProperty("ContainerSpec")
  ContainerSpec containerSpec,

  @Nullable
  @JsonProperty("Resources")
  ResourceRequirements resources,

  @Nullable
  @JsonProperty("RestartPolicy")
  RestartPolicy restartPolicy,

  @Nullable
  @JsonProperty("Placement")
  Placement placement,

  @Nullable
  @JsonProperty("Networks")
  List<NetworkAttachmentConfig> networks,

  @Nullable
  @JsonProperty("LogDriver")
  Driver logDriver
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private ContainerSpec containerSpec;
    private ResourceRequirements resources;
    private RestartPolicy restartPolicy;
    private Placement placement;
    private List<NetworkAttachmentConfig> networks;
    private Driver logDriver;

    public Builder containerSpec(ContainerSpec containerSpec) {
      this.containerSpec = containerSpec;
      return this;
    }

    public Builder resources(ResourceRequirements resources) {
      this.resources = resources;
      return this;
    }

    public Builder restartPolicy(RestartPolicy restartPolicy) {
      this.restartPolicy = restartPolicy;
      return this;
    }

    public Builder placement(Placement placement) {
      this.placement = placement;
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

    public Builder logDriver(Driver logDriver) {
      this.logDriver = logDriver;
      return this;
    }

    public TaskSpec build() {
      return new TaskSpec(containerSpec, resources, restartPolicy, placement, 
          networks == null ? null : List.copyOf(networks), logDriver);
    }
  }
}
