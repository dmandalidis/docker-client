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

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.util.Date;
import javax.annotation.Nullable;


@AutoValue
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public abstract class NodeInfo {

  @JsonProperty("ID")
  public abstract String id();

  @JsonProperty("Version")
  public abstract Version version();

  @JsonProperty("CreatedAt")
  public abstract Date createdAt();

  @JsonProperty("UpdatedAt")
  public abstract Date updatedAt();

  @JsonProperty("Spec")
  public abstract NodeSpec spec();

  @JsonProperty("Description")
  public abstract NodeDescription description();

  @JsonProperty("Status")
  public abstract NodeStatus status();

  @Nullable
  @JsonProperty("ManagerStatus")
  public abstract ManagerStatus managerStatus();

  @JsonCreator
  public static AutoValue_NodeInfo create(@JsonProperty("ID") final String id,
                                          @JsonProperty("Version") final Version version,
                                          @JsonProperty("CreatedAt") final Date createdAt,
                                          @JsonProperty("UpdatedAt") final Date updatedAt,
                                          @JsonProperty("Spec") final NodeSpec nodeSpec,
                                          @JsonProperty("Description")
                                            final NodeDescription description,
                                          @JsonProperty("Status")
                                            final NodeStatus nodeStatus,
                                          @JsonProperty("ManagerStatus")
                                            final ManagerStatus managerStatus) {

    return new AutoValue_NodeInfo(id, version, createdAt, updatedAt,
        nodeSpec, description, nodeStatus, managerStatus);
  }
}
