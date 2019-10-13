/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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

import java.util.List;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableEndpointSpec.Builder.class)
@Immutable
public interface EndpointSpec {

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

  @Nullable
  @JsonProperty("Mode")
  Mode mode();

  @JsonProperty("Ports")
  List<PortConfig> ports();

  @JsonIgnore
  @Derived
  @Auxiliary
  default Builder toBuilder() {
	return ImmutableEndpointSpec.builder().from(this);
  }
  
  default EndpointSpec withVipMode() {
    return toBuilder().mode(Mode.RESOLUTION_MODE_VIP).build();
  }

  default EndpointSpec withDnsrrMode() {
    return toBuilder().mode(Mode.RESOLUTION_MODE_DNSRR).build();
  }

  interface Builder {

    Builder mode(Mode mode);

    default Builder addPort(final PortConfig portConfig) {
    	ports(portConfig);
    	return this;
    }

    Builder ports(PortConfig... ports);

    Builder ports(Iterable<? extends PortConfig> ports);

    EndpointSpec build();
  }

  public static Builder builder() {
    return ImmutableEndpointSpec.builder();
  }

}
