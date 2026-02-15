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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Node(
  @JsonProperty("ID")
  String id,

  @JsonProperty("Version")
  Version version,

  @JsonProperty("CreatedAt")
  Date createdAt,

  @JsonProperty("UpdatedAt")
  Date updatedAt,

  @JsonProperty("Spec")
  NodeSpec spec,

  @JsonProperty("Description")
  NodeDescription description,

  @JsonProperty("Status")
  NodeStatus status,

  @Nullable
  @JsonProperty("ManagerStatus")
  ManagerStatus managerStatus
) {

  public record Criteria(
    @Nullable
    String nodeId,

    @Nullable
    String label,

    @Nullable
    String membership,

    @Nullable
    String nodeName,

    @Nullable
    String nodeRole
  ) {
    
    public static Builder builder() {
    return new Builder();
    }

    public static class Builder {
      private String nodeId;
      private String label;
      private String membership;
      private String nodeName;
      private String nodeRole;

      public Builder nodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
      }

      public Builder label(String label) {
        this.label = label;
        return this;
      }

      public Builder membership(String membership) {
        this.membership = membership;
        return this;
      }

      public Builder nodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
      }

      public Builder nodeRole(String nodeRole) {
        this.nodeRole = nodeRole;
        return this;
      }

      public Criteria build() {
        return new Criteria(nodeId, label, membership, nodeName, nodeRole);
      }
    }
  }

  public static Criteria.Builder find() {
    return Criteria.builder();
  }
}
