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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IpamConfig(
  @Nullable
  @JsonProperty("Subnet")
  String subnet,

  @Nullable
  @JsonProperty("IPRange")
  String ipRange,

  @Nullable
  @JsonProperty("Gateway")
  String gateway
) {
  
  public static IpamConfig create(final String subnet, final String ipRange, final String gateway) {
    return new IpamConfig(subnet, ipRange, gateway);
  }
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String subnet;
    private String ipRange;
    private String gateway;

    public Builder subnet(String subnet) {
      this.subnet = subnet;
      return this;
    }

    public Builder ipRange(String ipRange) {
      this.ipRange = ipRange;
      return this;
    }

    public Builder gateway(String gateway) {
      this.gateway = gateway;
      return this;
    }

    public IpamConfig build() {
      return new IpamConfig(subnet, ipRange, gateway);
    }
  }
}
