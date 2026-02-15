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
import java.util.HashMap;
import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Service(
  @JsonProperty("ID")
  String id,

  @JsonProperty("Version")
  Version version,

  @JsonProperty("CreatedAt")
  Date createdAt,

  @JsonProperty("UpdatedAt")
  Date updatedAt,

  @JsonProperty("Spec")
  ServiceSpec spec,

  @JsonProperty("Endpoint")
  Endpoint endpoint,

  @Nullable
  @JsonProperty("UpdateStatus")
  UpdateStatus updateStatus,

  @Nullable
  @JsonProperty("JobStatus")
  JobStatus jobStatus
) {
  
  public record Criteria(
    @Nullable
    String serviceId,

    @Nullable
    String serviceName,

    Map<String, String> labels
  ) {
    
    // Compact constructor to ensure labels is never null
    public Criteria {
      if (labels == null) {
        labels = Map.of();
      }
    }
    
    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private String serviceId;
      private String serviceName;
      private Map<String, String> labels;

      public Builder serviceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
      }

      public Builder serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
      }

      public Builder labels(Map<String, String> labels) {
        this.labels = labels == null ? null : Map.copyOf(labels);
        return this;
      }

      public Builder addLabel(final String label, final String value) {
        if (this.labels == null) {
          this.labels = new HashMap<>();
        } else {
          this.labels = new HashMap<>(this.labels);
        }
        this.labels.put(label, value);
        return this;
      }

      public Criteria build() {
        return new Criteria(serviceId, serviceName, 
            labels == null ? null : Map.copyOf(labels));
      }
    }
  }

  public static Criteria.Builder find() {
    return Criteria.builder();
  }
}
