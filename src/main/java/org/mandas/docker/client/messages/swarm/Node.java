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

import java.util.Date;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableNode.Builder.class)
@Immutable
@Enclosing
public interface Node {

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

  @JsonDeserialize(builder = ImmutableNode.Criteria.Builder.class)
  @Immutable
  public interface Criteria {
    /**
     * @return Filter by node id.
     */
    @Nullable
    String nodeId();

    /**
     * @return Filter by label.
     */
    @Nullable
    String label();

    /**
     * @return Filter by membership {accepted | pending}.
     */
    @Nullable
    String membership();

    /**
     * @return Filter by node name.
     */
    @Nullable
    String nodeName();

    /**
     * @return Filter by node role {manager | worker}.
     */
    @Nullable
    String nodeRole();

    public static Criteria.Builder builder() {
      return ImmutableNode.Criteria.builder();
    }
    
    interface Builder {
      Builder nodeId(String nodeId);

      Builder label(String label);

      Builder nodeName(String nodeName);

      Builder membership(String membership);

      Builder nodeRole(String nodeRole);

      Criteria build();
    }
  }

  public static Node.Criteria.Builder find() {
    return ImmutableNode.Criteria.builder();
  }
}
