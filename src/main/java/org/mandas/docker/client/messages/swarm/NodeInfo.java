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

package org.mandas.docker.client.messages.swarm;

import java.util.Date;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(builder = ImmutableNodeInfo.Builder.class)
@Immutable
public interface NodeInfo {

  @JsonProperty("ID")
  String id();

  @JsonProperty("Version")
  Version version();

  @JsonProperty("CreatedAt")
  Date createdAt();

  @JsonProperty("UpdatedAt")
  Date updatedAt();

  @JsonProperty("Spec")
  NodeSpec spec();

  @JsonProperty("Description")
  NodeDescription description();

  @JsonProperty("Status")
  NodeStatus status();

  @Nullable
  @JsonProperty("ManagerStatus")
  ManagerStatus managerStatus();
}
