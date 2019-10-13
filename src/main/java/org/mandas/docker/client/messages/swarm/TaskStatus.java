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

@JsonDeserialize(builder = ImmutableTaskStatus.Builder.class)
@Immutable
public interface TaskStatus {

  public static final String TASK_STATE_NEW = "new";
  public static final String TASK_STATE_ALLOCATED = "allocated";
  public static final String TASK_STATE_PENDING = "pending";
  public static final String TASK_STATE_ASSIGNED = "assigned";
  public static final String TASK_STATE_ACCEPTED = "accepted";
  public static final String TASK_STATE_PREPARING = "preparing";
  public static final String TASK_STATE_READY = "ready";
  public static final String TASK_STATE_STARTING = "starting";
  public static final String TASK_STATE_RUNNING = "running";
  public static final String TASK_STATE_COMPLETE = "complete";
  public static final String TASK_STATE_SHUTDOWN = "shutdown";
  public static final String TASK_STATE_FAILED = "failed";
  public static final String TASK_STATE_REJECTED = "rejected";

  @JsonProperty("Timestamp")
  Date timestamp();

  @JsonProperty("State")
  String state();

  @JsonProperty("Message")
  String message();

  @Nullable
  @JsonProperty("Err")
  String err();

  @Nullable
  @JsonProperty("ContainerStatus")
  ContainerStatus containerStatus();
}
