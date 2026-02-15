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

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonCreator;

public record RegistryConfigs(
  Map<String, RegistryAuth> configs
) {

  public static RegistryConfigs empty() {
    return builder().build();
  }

  @JsonCreator
  public static RegistryConfigs create(final Map<String, RegistryAuth> configs) {
    if (configs == null) {
      return empty();
    }

    final Map<String, RegistryAuth> transformedMap = configs.entrySet().stream()
      .collect(toMap(Entry::getKey, entry -> {
        RegistryAuth value = entry.getValue();
        if (value == null) {
          return null;
        }
        if (value.serverAddress() == null) {
          return value.toBuilder().serverAddress(entry.getKey()).build();
        }
        return value;
      }));
    
    return builder().configs(transformedMap).build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Map<String, RegistryAuth> configs = new HashMap<>();

    public Builder configs(Map<String, ? extends RegistryAuth> configs) {
      this.configs = new HashMap<>(configs);
      return this;
    }

    public Builder addConfig(final String server, final RegistryAuth registryAuth) {
      this.configs.put(server, registryAuth);
      return this;
    }

    public RegistryConfigs build() {
      return new RegistryConfigs(configs);
    }
  }
}
