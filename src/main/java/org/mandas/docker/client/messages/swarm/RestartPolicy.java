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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RestartPolicy(
  @Nullable
  @JsonProperty("Condition")
  String condition,

  @Nullable
  @JsonProperty("Delay")
  Long delay,

  @Nullable
  @JsonProperty("MaxAttempts")
  Integer maxAttempts,

  @Nullable
  @JsonProperty("Window")
  Long window
) {

  public static final String RESTART_POLICY_NONE = "none";
  public static final String RESTART_POLICY_ON_FAILURE = "on-failure";
  public static final String RESTART_POLICY_ANY = "any";

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String condition;
    private Long delay;
    private Integer maxAttempts;
    private Long window;

    public Builder condition(String condition) {
      this.condition = condition;
      return this;
    }

    public Builder delay(Long delay) {
      this.delay = delay;
      return this;
    }

    public Builder maxAttempts(Integer maxAttempts) {
      this.maxAttempts = maxAttempts;
      return this;
    }

    public Builder window(Long window) {
      this.window = window;
      return this;
    }

    public RestartPolicy build() {
      return new RestartPolicy(condition, delay, maxAttempts, window);
    }
  }
}
