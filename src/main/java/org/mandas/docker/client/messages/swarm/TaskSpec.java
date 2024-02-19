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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableTaskSpec.Builder.class)
@Immutable
public interface TaskSpec {

  @Nullable
  @JsonProperty("ContainerSpec")
  ContainerSpec containerSpec();

  @Nullable
  @JsonProperty("Resources")
  ResourceRequirements resources();

  @Nullable
  @JsonProperty("RestartPolicy")
  RestartPolicy restartPolicy();

  @Nullable
  @JsonProperty("Placement")
  Placement placement();

  @Nullable
  @JsonProperty("Networks")
  List<NetworkAttachmentConfig> networks();

  @Nullable
  @JsonProperty("LogDriver")
  Driver logDriver();

  interface Builder {

    Builder containerSpec(ContainerSpec containerSpec);

    Builder resources(ResourceRequirements resources);

    Builder restartPolicy(RestartPolicy restartPolicy);

    Builder placement(Placement placement);

    Builder networks(NetworkAttachmentConfig... networks);

    Builder networks(Iterable<? extends NetworkAttachmentConfig> networks);

    Builder logDriver(Driver logDriver);

    TaskSpec build();
  }

  public static Builder builder() {
    return ImmutableTaskSpec.builder();
  }
}
