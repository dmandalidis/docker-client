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

package org.mandas.docker.client.messages.mount;

import java.util.HashMap;
import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VolumeOptions(
  @Nullable
  @JsonProperty("NoCopy")
  Boolean noCopy,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels,

  @Nullable
  @JsonProperty("DriverConfig")
  Driver driverConfig
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Boolean noCopy;
    private Map<String, String> labels;
    private Driver driverConfig;

    public Builder noCopy(Boolean noCopy) {
      this.noCopy = noCopy;
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

    public Builder driverConfig(Driver driverConfig) {
      this.driverConfig = driverConfig;
      return this;
    }

    public VolumeOptions build() {
      return new VolumeOptions(noCopy, labels == null ? null : Map.copyOf(labels), driverConfig);
    }
  }
}
