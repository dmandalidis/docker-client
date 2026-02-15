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

import java.util.ArrayList;
import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public record EndpointSpec(
  @Nullable
  @JsonProperty("Mode")
  Mode mode,

  @JsonProperty("Ports")
  List<PortConfig> ports
) {

  public enum Mode {
    RESOLUTION_MODE_VIP("vip"),
    RESOLUTION_MODE_DNSRR("dnsrr");

    private final String value;

    Mode(final String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }
  }

  @JsonIgnore
  public Builder toBuilder() {
    Builder builder = builder();
    if (mode != null) {
      builder.mode(mode);
    }
    if (ports != null) {
      builder.ports(ports);
    }
    return builder;
  }
  
  public EndpointSpec withVipMode() {
    return toBuilder().mode(Mode.RESOLUTION_MODE_VIP).build();
  }

  public EndpointSpec withDnsrrMode() {
    return toBuilder().mode(Mode.RESOLUTION_MODE_DNSRR).build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Mode mode;
    private List<PortConfig> ports;

    public Builder mode(Mode mode) {
      this.mode = mode;
      return this;
    }

    public Builder addPort(final PortConfig portConfig) {
      if (this.ports == null) {
        this.ports = new ArrayList<>();
      } else {
        this.ports = new ArrayList<>(this.ports);
      }
      this.ports.add(portConfig);
      return this;
    }

    public Builder ports(PortConfig... ports) {
      this.ports = ports == null ? null : List.of(ports);
      return this;
    }

    public Builder ports(List<PortConfig> ports) {
      this.ports = ports;
      return this;
    }

    public EndpointSpec build() {
      return new EndpointSpec(mode, ports == null ? null : List.copyOf(ports));
    }
  }
}
