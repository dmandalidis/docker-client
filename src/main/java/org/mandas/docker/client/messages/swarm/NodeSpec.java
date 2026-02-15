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

import java.util.HashMap;
import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NodeSpec(
  @Nullable
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels,

  @JsonProperty("Role")
  String role,

  @JsonProperty("Availability")
  String availability
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(final NodeSpec source) {
    return new Builder(source);
  }

  public static class Builder {
    private String name;
    private Map<String, String> labels;
    private String role;
    private String availability;

    Builder() {}

    Builder(NodeSpec source) {
      this.name = source.name;
      this.labels = source.labels;
      this.role = source.role;
      this.availability = source.availability;
    }

    public Builder name(String name) {
      this.name = name;
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

    public Builder labels(Map<String, String> labels) {
      this.labels = labels == null ? null : Map.copyOf(labels);
      return this;
    }

    public Builder role(String role) {
      this.role = role;
      return this;
    }

    public Builder availability(String availability) {
      this.availability = availability;
      return this;
    }

    public NodeSpec build() {
      return new NodeSpec(name, labels == null ? null : Map.copyOf(labels), role, availability);
    }
  }
}
