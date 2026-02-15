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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Ipam(
  @JsonProperty("Driver")
  String driver,

  @Nullable
  @JsonProperty("Config")
  List<IpamConfig> config,

  @Nullable
  @JsonProperty("Options")
  Map<String, String> options
) {

  public static Builder builder() {
    return new Builder();
  }

  public static Ipam create(final String driver, final List<IpamConfig> config) {
    return new Ipam(driver, config, null);
  }

  public static class Builder {
    private String driver;
    private List<IpamConfig> config;
    private Map<String, String> options;

    public Builder driver(String driver) {
      this.driver = driver;
      return this;
    }

    public Builder options(Map<String, String> options) {
      this.options = new HashMap<>(options);
      return this;
    }

    public Builder config(List<IpamConfig> config) {
      this.config = new ArrayList<>();
      config.forEach(this.config::add);
      return this;
    }

    public Ipam build() {
      return new Ipam(driver, config, options);
    }
  }
}
