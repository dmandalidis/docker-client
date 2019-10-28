/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(builder = ImmutableSwarmInit.Builder.class)
@Immutable
public interface SwarmInit {

  @JsonProperty("ListenAddr")
  String listenAddr();

  @JsonProperty("AdvertiseAddr")
  String advertiseAddr();

  @Nullable
  @JsonProperty("ForceNewCluster")
  Boolean forceNewCluster();

  @Nullable
  @JsonProperty("Spec")
  SwarmSpec swarmSpec();

  interface Builder {
    Builder listenAddr(String listenAddr);

    Builder advertiseAddr(String advertiseAddr);

    Builder forceNewCluster(Boolean forceNewCluster);

    Builder swarmSpec(SwarmSpec swarmSpec);

    SwarmInit build();
  }

  public static SwarmInit.Builder builder() {
    return ImmutableSwarmInit.builder();
  }

}
