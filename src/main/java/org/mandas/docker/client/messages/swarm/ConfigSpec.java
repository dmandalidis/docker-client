/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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

import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigSpec(
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels,

  @Nullable
  @JsonProperty("Data")
  String data
) {

  public static Builder builder() {
    return new Builder();
  }

  

  public static class Builder {
    private String name;
    private Map<String, String> labels;
    private String data;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder labels(Map<String, String> labels) {
      this.labels = labels == null ? null : Map.copyOf(labels);
      return this;
    }

    public Builder data(String data) {
      this.data = data;
      return this;
    }

    public ConfigSpec build() {
      return new ConfigSpec(name, labels == null ? null : Map.copyOf(labels), data);
    }
  }
}
