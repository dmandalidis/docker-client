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

package org.mandas.docker.client.messages;

import java.util.Date;
import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = ContainerState.Builder.class)
public record ContainerState(
    @Nullable
    @JsonProperty("Status")
    String status,
    @JsonProperty("Running")
    boolean running,
    @JsonProperty("Paused")
    boolean paused,
    @Nullable
    @JsonProperty("Restarting")
    Boolean restarting,
    @JsonProperty("Pid")
    Integer pid,
    @JsonProperty("ExitCode")
    Long exitCode,
    @JsonProperty("StartedAt")
    Date startedAt,
    @JsonProperty("FinishedAt")
    Date finishedAt,
    @Nullable
    @JsonProperty("Error")
    String error,
    @Nullable
    @JsonProperty("OOMKilled")
    Boolean oomKilled,
    @Nullable
    @JsonProperty("Health")
    Health health) {

  /**
   * Health log entry
   * @param start start time
   * @param end end time
   * @param exitCode exit code
   * @param output health check output
   */
  @JsonDeserialize(builder = HealthLog.Builder.class)
  public record HealthLog(
      @JsonProperty("Start")
      Date start,
      @JsonProperty("End")
      Date end,
      @JsonProperty("ExitCode")
      Long exitCode,
      @JsonProperty("Output")
      String output) {

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
      private Date start;
      private Date end;
      private Long exitCode;
      private String output;

      public Builder start(Date start) {
        this.start = start;
        return this;
      }

      public Builder end(Date end) {
        this.end = end;
        return this;
      }

      public Builder exitCode(Long exitCode) {
        this.exitCode = exitCode;
        return this;
      }

      public Builder output(String output) {
        this.output = output;
        return this;
      }

      public HealthLog build() {
        return new HealthLog(start, end, exitCode, output);
      }
    }
  }

  /**
   * Health status
   * @param status health status string
   * @param failingStreak number of consecutive failures
   * @param log list of health logs
   */
  @JsonDeserialize(builder = Health.Builder.class)
  public record Health(
      @JsonProperty("Status")
      String status,
      @JsonProperty("FailingStreak")
      Integer failingStreak,
      @JsonProperty("Log")
      List<HealthLog> log) {

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
      private String status;
      private Integer failingStreak;
      private List<HealthLog> log;

      public Builder status(String status) {
        this.status = status;
        return this;
      }

      public Builder failingStreak(Integer failingStreak) {
        this.failingStreak = failingStreak;
        return this;
      }

      public Builder log(List<HealthLog> log) {
        this.log = log;
        return this;
      }

      public Health build() {
        return new Health(status, failingStreak, log);
      }
    }
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String status;
    private boolean running;
    private boolean paused;
    private Boolean restarting;
    private Integer pid;
    private Long exitCode;
    private Date startedAt;
    private Date finishedAt;
    private String error;
    private Boolean oomKilled;
    private Health health;

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Builder running(boolean running) {
      this.running = running;
      return this;
    }

    public Builder paused(boolean paused) {
      this.paused = paused;
      return this;
    }

    public Builder restarting(Boolean restarting) {
      this.restarting = restarting;
      return this;
    }

    public Builder pid(Integer pid) {
      this.pid = pid;
      return this;
    }

    public Builder exitCode(Long exitCode) {
      this.exitCode = exitCode;
      return this;
    }

    public Builder startedAt(Date startedAt) {
      this.startedAt = startedAt;
      return this;
    }

    public Builder finishedAt(Date finishedAt) {
      this.finishedAt = finishedAt;
      return this;
    }

    public Builder error(String error) {
      this.error = error;
      return this;
    }

    public Builder oomKilled(Boolean oomKilled) {
      this.oomKilled = oomKilled;
      return this;
    }

    public Builder health(Health health) {
      this.health = health;
      return this;
    }

    public ContainerState build() {
      return new ContainerState(status, running, paused, restarting, pid, exitCode, 
          startedAt, finishedAt, error, oomKilled, health);
    }
  }
}
