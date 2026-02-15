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

public record EndpointConfig(
  @Nullable @JsonProperty("IPAMConfig") EndpointIpamConfig ipamConfig,
  @Nullable @JsonProperty("Links") List<String> links,
  @Nullable @JsonProperty("Aliases") List<String> aliases,
  @Nullable @JsonProperty("Gateway") String gateway,
  @Nullable @JsonProperty("IPAddress") String ipAddress,
  @Nullable @JsonProperty("IPPrefixLen") Integer ipPrefixLen,
  @Nullable @JsonProperty("IPv6Gateway") String ipv6Gateway,
  @Nullable @JsonProperty("GlobalIPv6Address") String globalIPv6Address,
  @Nullable @JsonProperty("GlobalIPv6PrefixLen") Integer globalIPv6PrefixLen,
  @Nullable @JsonProperty("MacAddress") String macAddress
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private EndpointIpamConfig ipamConfig;
    private List<String> links;
    private List<String> aliases;
    private String gateway;
    private String ipAddress;
    private Integer ipPrefixLen;
    private String ipv6Gateway;
    private String globalIPv6Address;
    private Integer globalIPv6PrefixLen;
    private String macAddress;

    public Builder ipamConfig(EndpointIpamConfig ipamConfig) {
      this.ipamConfig = ipamConfig;
      return this;
    }

    public Builder links(List<String> links) {
      this.links = links;
      return this;
    }

    public Builder aliases(List<String> aliases) {
      this.aliases = aliases;
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

    public EndpointConfig build() {
      return new EndpointConfig(ipamConfig, links, aliases, gateway, ipAddress, ipPrefixLen, 
                               ipv6Gateway, globalIPv6Address, globalIPv6PrefixLen, macAddress);
    }
  }

  public record EndpointIpamConfig(
    @Nullable @JsonProperty("IPv4Address") String ipv4Address,
    @Nullable @JsonProperty("IPv6Address") String ipv6Address,
    @Nullable @JsonProperty("LinkLocalIPs") List<String> linkLocalIPs
  ) {

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private String ipv4Address;
      private String ipv6Address;
      private List<String> linkLocalIPs;

      public Builder ipv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
        return this;
      }

      public Builder ipv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
        return this;
      }

      public Builder linkLocalIPs(List<String> linkLocalIPs) {
        this.linkLocalIPs = linkLocalIPs;
        return this;
      }

      public EndpointIpamConfig build() {
        return new EndpointIpamConfig(ipv4Address, ipv6Address, linkLocalIPs);
      }
    }
  }
}
