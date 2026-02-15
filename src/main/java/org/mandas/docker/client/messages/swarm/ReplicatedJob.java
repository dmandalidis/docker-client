/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2021 Dimitris Mandalidis
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

public record ReplicatedJob(
  @Nullable
  @JsonProperty("MaxConcurrent")
  Long maxConcurrent,
  
  @Nullable
  @JsonProperty("TotalCompletions")
  Long totalCompletions
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long maxConcurrent;
    private Long totalCompletions;

    public Builder maxConcurrent(Long maxConcurrent) {
      this.maxConcurrent = maxConcurrent;
      return this;
    }

    public Builder totalCompletions(Long totalCompletions) {
      this.totalCompletions = totalCompletions;
      return this;
    }

    public ReplicatedJob build() {
      return new ReplicatedJob(maxConcurrent, totalCompletions);
    }
  }
}
