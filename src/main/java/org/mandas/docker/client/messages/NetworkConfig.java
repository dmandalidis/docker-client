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

import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NetworkConfig(
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("Driver")
  String driver,

  @Nullable
  @JsonProperty("IPAM")
  Ipam ipam,

  @JsonProperty("Options")
  Map<String, String> options,

  @Nullable
  @JsonProperty("CheckDuplicate")
  Boolean checkDuplicate,
  
  @Nullable
  @JsonProperty("Internal")
  Boolean internal,
  
  @Nullable
  @JsonProperty("EnableIPv6")
  Boolean enableIPv6,

  @Nullable
  @JsonProperty("Attachable")
  Boolean attachable,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private String driver;
    private Ipam ipam;
    private Map<String, String> options;
    private Boolean checkDuplicate;
    private Boolean internal;
    private Boolean enableIPv6;
    private Boolean attachable;
    private Map<String, String> labels;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder driver(String driver) {
      this.driver = driver;
      return this;
    }

    public Builder ipam(Ipam ipam) {
      this.ipam = ipam;
      return this;
    }

    public Builder options(Map<String, String> options) {
      this.options = options != null ? Map.copyOf(options) : null;
      return this;
    }

    public Builder checkDuplicate(Boolean checkDuplicate) {
      this.checkDuplicate = checkDuplicate;
      return this;
    }

    public Builder internal(Boolean internal) {
      this.internal = internal;
      return this;
    }

    public Builder enableIPv6(Boolean enableIPv6) {
      this.enableIPv6 = enableIPv6;
      return this;
    }

    public Builder attachable(Boolean attachable) {
      this.attachable = attachable;
      return this;
    }

    public Builder labels(Map<String, String> labels) {
      this.labels = labels != null ? Map.copyOf(labels) : null;
      return this;
    }

    public NetworkConfig build() {
      return new NetworkConfig(name, driver, ipam, options, checkDuplicate, 
                              internal, enableIPv6, attachable, labels);
    }
  }
}
