/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * Copyright (C) 9/2019 - 2020 Dimitris Mandalidis
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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.AllowNulls;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableNetworkSettings.Builder.class)
@Immutable
public interface NetworkSettings {

  @Nullable
  @JsonProperty("IPAddress")
  String ipAddress();

  @Nullable
  @JsonProperty("IPPrefixLen")
  Integer ipPrefixLen();

  @Nullable
  @JsonProperty("Gateway")
  String gateway();

  @Nullable
  @JsonProperty("Bridge")
  String bridge();

  @Nullable
  @JsonProperty("PortMapping")
  Map<String, Map<String, String>> portMapping();

  @JsonIgnore
  @Nullable
  @Derived
  default Map<String, List<PortBinding>> ports() {
	  if (nullValuedPorts() == null) {
		  return null;
	  }
	  return nullValuedPorts().entrySet()
			  .stream()
			  .collect(toMap(k -> k.getKey(), k -> k.getValue() == null? emptyList(): k.getValue()));
  }
  
  /**
   * @return Only used for deserialization and clients should not call that method
   */
  @Nullable @AllowNulls
  @JsonProperty("Ports")
  Map<String, List<PortBinding>> nullValuedPorts();

  @Nullable
  @JsonProperty("MacAddress")
  String macAddress();

  @Nullable
  @JsonProperty("Networks")
  Map<String, AttachedNetwork> networks();

  @Nullable
  @JsonProperty("EndpointID")
  String endpointId();

  @Nullable
  @JsonProperty("SandboxID")
  String sandboxId();

  @Nullable
  @JsonProperty("SandboxKey")
  String sandboxKey();

  @Nullable
  @JsonProperty("HairpinMode")
  Boolean hairpinMode();

  @Nullable
  @JsonProperty("LinkLocalIPv6Address")
  String linkLocalIPv6Address();

  @Nullable
  @JsonProperty("LinkLocalIPv6PrefixLen")
  Integer linkLocalIPv6PrefixLen();

  @Nullable
  @JsonProperty("GlobalIPv6Address")
  String globalIPv6Address();

  @Nullable
  @JsonProperty("GlobalIPv6PrefixLen")
  Integer globalIPv6PrefixLen();

  @Nullable
  @JsonProperty("IPv6Gateway")
  String ipv6Gateway();

  static Builder builder() {
    return ImmutableNetworkSettings.builder();
  }

  interface Builder {

    Builder ipAddress(String ipAddress);

    Builder ipPrefixLen(Integer ipPrefixLen);

    Builder gateway(String gateway);

    Builder bridge(String bridge);

    Builder portMapping(Map<String, ? extends Map<String, String>> portMapping);

    Builder nullValuedPorts(Map<String, ? extends List<PortBinding>> ports);

    Builder macAddress(String macAddress);

    Builder networks(Map<String, ? extends AttachedNetwork> networks);

    Builder endpointId(final String endpointId);

    Builder sandboxId(final String sandboxId);

    Builder sandboxKey(final String sandboxKey);

    Builder hairpinMode(final Boolean hairpinMode);

    Builder linkLocalIPv6Address(final String linkLocalIPv6Address);

    Builder linkLocalIPv6PrefixLen(final Integer linkLocalIPv6PrefixLen);

    Builder globalIPv6Address(final String globalIPv6Address);

    Builder globalIPv6PrefixLen(final Integer globalIPv6PrefixLen);

    Builder ipv6Gateway(final String ipv6Gateway);

    NetworkSettings build();
  }
}
