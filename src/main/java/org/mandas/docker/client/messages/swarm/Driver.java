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

public record Driver(
  @Nullable
  @JsonProperty("Name")
  String name,

  @JsonProperty("Options")
  Map<String, String> options
) {

  // Compact constructor to ensure options is never null
  public Driver {
    if (options == null) {
      options = Map.of();
    }
  }

  public static Driver.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private Map<String, String> options;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder addOption(final String name, final String value) {
      if (this.options == null) {
        this.options = new HashMap<>();
      }
      this.options.put(name, value);
      return this;
    }

    public Builder options(Map<String, String> options) {
      this.options = new HashMap<>(options);
      return this;
    }

    public Driver build() {
      return new Driver(name, options);
    }
  }
}
