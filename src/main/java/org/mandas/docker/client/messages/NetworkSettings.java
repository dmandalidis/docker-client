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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.mandas.docker.AllowNulls;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = NetworkSettings.Builder.class)
public record NetworkSettings(
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("IPAddress")
    String ipAddress,
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("IPPrefixLen")
    Integer ipPrefixLen,
    @Nullable
    @JsonProperty("Gateway")
    String gateway,
    @Nullable
    @JsonProperty("Bridge")
    String bridge,
    @Nullable
    @JsonProperty("PortMapping")
    Map<String, Map<String, String>> portMapping,
    /**
     * @return Only used for deserialization and clients should not call that method
     */
    @Nullable @AllowNulls
    @JsonProperty("Ports")
    Map<String, List<PortBinding>> nullValuedPorts,
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("MacAddress")
    String macAddress,
    @Nullable
    @JsonProperty("Networks")
    Map<String, AttachedNetwork> networks,
    @Nullable
    @JsonProperty("EndpointID")
    String endpointId,
    @Nullable
    @JsonProperty("SandboxID")
    String sandboxId,
    @Nullable
    @JsonProperty("SandboxKey")
    String sandboxKey,
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("HairpinMode")
    Boolean hairpinMode,
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("LinkLocalIPv6Address")
    String linkLocalIPv6Address,
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("LinkLocalIPv6PrefixLen")
    Integer linkLocalIPv6PrefixLen,
    @Nullable
    @JsonProperty("GlobalIPv6Address")
    String globalIPv6Address,
    @Nullable
    @JsonProperty("GlobalIPv6PrefixLen")
    Integer globalIPv6PrefixLen,
    @Nullable
    @JsonProperty("IPv6Gateway")
    String ipv6Gateway) {

  @JsonIgnore
  @Nullable
  public Map<String, List<PortBinding>> ports() {
    if (nullValuedPorts() == null) {
      return null;
    }
    return nullValuedPorts().entrySet()
        .stream()
        .collect(toMap(k -> k.getKey(), k -> k.getValue() == null? emptyList(): k.getValue()));
  }

  static Builder builder() {
    return new Builder();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String ipAddress;
    private Integer ipPrefixLen;
    private String gateway;
    private String bridge;
    private Map<String, Map<String, String>> portMapping;
    private Map<String, List<PortBinding>> nullValuedPorts;
    private String macAddress;
    private Map<String, AttachedNetwork> networks;
    private String endpointId;
    private String sandboxId;
    private String sandboxKey;
    private Boolean hairpinMode;
    private String linkLocalIPv6Address;
    private Integer linkLocalIPv6PrefixLen;
    private String globalIPv6Address;
    private Integer globalIPv6PrefixLen;
    private String ipv6Gateway;

    public Builder ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public Builder ipPrefixLen(Integer ipPrefixLen) {
      this.ipPrefixLen = ipPrefixLen;
      return this;
    }

    public Builder gateway(String gateway) {
      this.gateway = gateway;
      return this;
    }

    public Builder bridge(String bridge) {
      this.bridge = bridge;
      return this;
    }

    public Builder portMapping(Map<String, Map<String, String>> portMapping) {
      this.portMapping = portMapping;
      return this;
    }

    public Builder nullValuedPorts(Map<String, List<PortBinding>> ports) {
      this.nullValuedPorts = ports;
      return this;
    }

    public Builder macAddress(String macAddress) {
      this.macAddress = macAddress;
      return this;
    }

    public Builder networks(Map<String, AttachedNetwork> networks) {
      this.networks = networks;
      return this;
    }

    public Builder endpointId(final String endpointId) {
      this.endpointId = endpointId;
      return this;
    }

    public Builder sandboxId(final String sandboxId) {
      this.sandboxId = sandboxId;
      return this;
    }

    public Builder sandboxKey(final String sandboxKey) {
      this.sandboxKey = sandboxKey;
      return this;
    }

    public Builder hairpinMode(final Boolean hairpinMode) {
      this.hairpinMode = hairpinMode;
      return this;
    }

    public Builder linkLocalIPv6Address(final String linkLocalIPv6Address) {
      this.linkLocalIPv6Address = linkLocalIPv6Address;
      return this;
    }

    public Builder linkLocalIPv6PrefixLen(final Integer linkLocalIPv6PrefixLen) {
      this.linkLocalIPv6PrefixLen = linkLocalIPv6PrefixLen;
      return this;
    }

    public Builder globalIPv6Address(final String globalIPv6Address) {
      this.globalIPv6Address = globalIPv6Address;
      return this;
    }

    public Builder globalIPv6PrefixLen(final Integer globalIPv6PrefixLen) {
      this.globalIPv6PrefixLen = globalIPv6PrefixLen;
      return this;
    }

    public Builder ipv6Gateway(final String ipv6Gateway) {
      this.ipv6Gateway = ipv6Gateway;
      return this;
    }

    public NetworkSettings build() {
      return new NetworkSettings(ipAddress, ipPrefixLen, gateway, bridge, portMapping, 
          nullValuedPorts, macAddress, networks, endpointId, sandboxId, sandboxKey, 
          hairpinMode, linkLocalIPv6Address, linkLocalIPv6PrefixLen, globalIPv6Address, 
          globalIPv6PrefixLen, ipv6Gateway);
    }
  }
}
