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

public record ServiceMode(
  @Nullable
  @JsonProperty("Replicated")
  ReplicatedService replicated,

  @Nullable
  @JsonProperty("ReplicatedJob")
  ReplicatedJob replicatedJob,
  
  @Nullable
  @JsonProperty("Global")
  GlobalService global,

  @Nullable
  @JsonProperty("GlobalJob")
  GlobalJob globalJob
) {
  
  public static ServiceMode withReplicas(final long replicas) {
    return new ServiceMode(
        ReplicatedService.builder().replicas(replicas).build(),
        null,
        null,
        null
    );
  }
  
  public static ServiceMode withReplicas(final long replicas, final long maxConcurrent) {
    return new ServiceMode(
        ReplicatedService.builder().replicas(replicas).maxConcurrent(maxConcurrent).build(),
        null,
        null,
        null
    );
  }
  
  public static ServiceMode withJobReplicas(final long totalCompletions) {
    return new ServiceMode(
        null,
        ReplicatedJob.builder().totalCompletions(totalCompletions).build(),
        null,
        null
    );
  }
  
  public static ServiceMode withJobReplicas(final long totalCompletions, final long maxConcurrent) {
    return new ServiceMode(
        null,
        ReplicatedJob.builder().totalCompletions(totalCompletions).maxConcurrent(maxConcurrent).build(),
        null,
        null
    );
  }

  public static ServiceMode withGlobal() {
    return new ServiceMode(null, null, GlobalService.builder().build(), null);
  }
  
  public static ServiceMode withGlobalJob() {
    return new ServiceMode(null, null, null, GlobalJob.builder().build());
  }

  public static ServiceMode.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private ReplicatedService replicated;
    private ReplicatedJob replicatedJob;
    private GlobalService global;
    private GlobalJob globalJob;

    public Builder replicated(ReplicatedService replicated) {
      this.replicated = replicated;
      return this;
    }
    
    public Builder replicatedJob(ReplicatedJob replicatedJob) {
      this.replicatedJob = replicatedJob;
      return this;
    }

    public Builder global(GlobalService global) {
      this.global = global;
      return this;
    }

    public Builder globalJob(GlobalJob globalJob) {
      this.globalJob = globalJob;
      return this;
    }
    
    public ServiceMode build() {
      return new ServiceMode(replicated, replicatedJob, global, globalJob);
    }
  }
}
