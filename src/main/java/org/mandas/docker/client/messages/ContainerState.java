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

package org.mandas.docker.client.messages;

import java.util.Date;
import java.util.List;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableContainerState.Builder.class)
@Immutable
@Enclosing
public interface ContainerState {

  @Nullable
  @JsonProperty("Status")
  String status();

  @JsonProperty("Running")
  boolean running();

  @JsonProperty("Paused")
  boolean paused();

  @Nullable
  @JsonProperty("Restarting")
  Boolean restarting();

  @JsonProperty("Pid")
  Integer pid();

  @JsonProperty("ExitCode")
  Long exitCode();

  @JsonProperty("StartedAt")
  Date startedAt();

  @JsonProperty("FinishedAt")
  Date finishedAt();

  @Nullable
  @JsonProperty("Error")
  String error();

  @Nullable
  @JsonProperty("OOMKilled")
  Boolean oomKilled();

  @Nullable
  @JsonProperty("Health")
  Health health();

  @JsonDeserialize(builder = ImmutableContainerState.HealthLog.Builder.class)
  @Immutable
  public interface HealthLog {

    @JsonProperty("Start")
    Date start();

    @JsonProperty("End")
    Date end();

    @JsonProperty("ExitCode")
    Long exitCode();

    @JsonProperty("Output")
    String output();
  }

  @JsonDeserialize(builder = ImmutableContainerState.Health.Builder.class)
  @Immutable
  public interface Health {

    @JsonProperty("Status")
    String status();

    @JsonProperty("FailingStreak")
    Integer failingStreak();

    @JsonProperty("Log")
    List<HealthLog> log();
  }
}
