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

import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = AttachedNetwork.Builder.class)
public record AttachedNetwork(
    @Nullable
    @JsonProperty("Aliases")
    List<String> aliases,
    @Nullable
    @JsonProperty("NetworkID")
    String networkId,
    @JsonProperty("EndpointID")
    String endpointId,
    @JsonProperty("Gateway")
    String gateway,
    @JsonProperty("IPAddress")
    String ipAddress,
    @JsonProperty("IPPrefixLen")
    Integer ipPrefixLen,
    @JsonProperty("IPv6Gateway")
    String ipv6Gateway,
    @JsonProperty("GlobalIPv6Address")
    String globalIPv6Address,
    @JsonProperty("GlobalIPv6PrefixLen")
    Integer globalIPv6PrefixLen,
    @JsonProperty("MacAddress")
    String macAddress) {

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private List<String> aliases;
    private String networkId;
    private String endpointId;
    private String gateway;
    private String ipAddress;
    private Integer ipPrefixLen;
    private String ipv6Gateway;
    private String globalIPv6Address;
    private Integer globalIPv6PrefixLen;
    private String macAddress;

    public Builder aliases(List<String> aliases) {
      this.aliases = aliases;
      return this;
    }

    public Builder networkId(String networkId) {
      this.networkId = networkId;
      return this;
    }

    public Builder endpointId(String endpointId) {
      this.endpointId = endpointId;
      return this;
    }

    public Builder gateway(String gateway) {
      this.gateway = gateway;
      return this;
    }

    public Builder ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public Builder ipPrefixLen(Integer ipPrefixLen) {
      this.ipPrefixLen = ipPrefixLen;
      return this;
    }

    public Builder ipv6Gateway(String ipv6Gateway) {
      this.ipv6Gateway = ipv6Gateway;
      return this;
    }

    public Builder globalIPv6Address(String globalIPv6Address) {
      this.globalIPv6Address = globalIPv6Address;
      return this;
    }

    public Builder globalIPv6PrefixLen(Integer globalIPv6PrefixLen) {
      this.globalIPv6PrefixLen = globalIPv6PrefixLen;
      return this;
    }

    public Builder macAddress(String macAddress) {
      this.macAddress = macAddress;
      return this;
    }

    public AttachedNetwork build() {
      return new AttachedNetwork(aliases, networkId, endpointId, gateway, ipAddress, 
          ipPrefixLen, ipv6Gateway, globalIPv6Address, globalIPv6PrefixLen, macAddress);
    }
  }
}
