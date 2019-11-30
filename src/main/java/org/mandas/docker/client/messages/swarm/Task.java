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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableTask.Builder.class)
@Immutable
@Enclosing
public interface Task {

  @JsonProperty("ID")
  String id();

  @JsonProperty("Version")
  Version version();

  @JsonProperty("CreatedAt")
  Date createdAt();

  @JsonProperty("UpdatedAt")
  Date updatedAt();

  @Nullable
  @JsonProperty("Name")
  String name();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  @JsonProperty("Spec")
  TaskSpec spec();

  @JsonProperty("ServiceID")
  String serviceId();

  @Nullable
  @JsonProperty("Slot")
  Integer slot();

  @Nullable
  @JsonProperty("NodeID")
  String nodeId();

  @JsonProperty("Status")
  TaskStatus status();

  @JsonProperty("DesiredState")
  String desiredState();

  @Nullable
  @JsonProperty("NetworksAttachments")
  List<NetworkAttachment> networkAttachments();

  @JsonDeserialize(builder = ImmutableTask.Criteria.Builder.class)
  @Immutable
  public interface Criteria {

    /**
     * Filter by task id.
     */
    @Nullable
    String taskId();

    /**
     * Filter by task name.
     */
    @Nullable
    String taskName();

    /**
     * Filter by service name.
     */
    @Nullable
    String serviceName();

    /**
     * Filter by node id.
     */
    @Nullable
    String nodeId();

    /**
     * Filter by label.
     */
    @Nullable
    String label();

    /**
     * Filter by desired state.
     */
    @Nullable
    String desiredState();

    public static Criteria.Builder builder() {
      return ImmutableTask.Criteria.builder();
    }

    interface Builder {

      Builder taskId(final String taskId);

      Builder taskName(final String taskName);

      Builder serviceName(final String serviceName);

      Builder nodeId(final String nodeId);

      Builder label(final String label);

      Builder desiredState(final String desiredState);

      Criteria build();
    }
  }

  public static Criteria.Builder find() {
    return ImmutableTask.Criteria.builder();
  }
}
