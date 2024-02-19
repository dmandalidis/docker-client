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

@JsonDeserialize(builder = ImmutableSwarmInfo.Builder.class)
@Immutable
public interface SwarmInfo {

  @Nullable
  @JsonProperty("Cluster")
  SwarmCluster cluster();
  
  @JsonProperty("ControlAvailable")
  boolean controlAvailable();

  @JsonProperty("Error")
  String error();

  @JsonProperty("LocalNodeState")
  String localNodeState();

  @JsonProperty("NodeAddr")
  String nodeAddr();

  @JsonProperty("NodeID")
  String nodeId();

  @Nullable
  @JsonProperty("Nodes")
  Integer nodes();

  @Nullable
  @JsonProperty("Managers")
  Integer managers();
  
  @Nullable
  @JsonProperty("RemoteManagers")
  List<RemoteManager> remoteManagers();

}
