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

import java.util.HashMap;
import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Volume(
  @Nullable
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("Driver")
  String driver,

  @Nullable
  @JsonProperty("DriverOpts")
  Map<String, String> driverOpts,

  @Nullable
  @JsonProperty("Options")
  Map<String, String> options,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels,

  @Nullable
  @JsonProperty("Mountpoint")
  String mountpoint,

  @Nullable
  @JsonProperty("Scope")
  String scope,

  @Nullable
  @JsonProperty("Status")
  Map<String, String> status
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private String driver;
    private Map<String, String> driverOpts;
    private Map<String, String> options;
    private Map<String, String> labels;
    private String mountpoint;
    private String scope;
    private Map<String, String> status;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder driver(String driver) {
      this.driver = driver;
      return this;
    }

    public Builder driverOpts(Map<String, String> driverOpts) {
      this.driverOpts = new HashMap<>(driverOpts);
      return this;
    }

    public Builder options(Map<String, String> options) {
      this.options = new HashMap<>(options);
      return this;
    }

    public Builder labels(Map<String, String> labels) {
      this.labels = new HashMap<>(labels);
      return this;
    }

    public Builder mountpoint(String mountpoint) {
      this.mountpoint = mountpoint;
      return this;
    }

    public Builder scope(String scope) {
      this.scope = scope;
      return this;
    }

    public Builder status(Map<String, String> status) {
      this.status = new HashMap<>(status);
      return this;
    }

    public Volume build() {
      return new Volume(name, driver, driverOpts, options, labels, mountpoint, scope, status);
    }
  }
}
