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

import java.util.List;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableEndpointConfig.Builder.class)
@Immutable
@Enclosing
public interface EndpointConfig {

  @Nullable
  @JsonProperty("IPAMConfig")
  EndpointIpamConfig ipamConfig();

  @Nullable
  @JsonProperty("Links")
  List<String> links();

  @Nullable
  @JsonProperty("Aliases")
  List<String> aliases();

  @Nullable
  @JsonProperty("Gateway")
  String gateway();

  @Nullable
  @JsonProperty("IPAddress")
  String ipAddress();

  @Nullable
  @JsonProperty("IPPrefixLen")
  Integer ipPrefixLen();

  @Nullable
  @JsonProperty("IPv6Gateway")
  String ipv6Gateway();

  @Nullable
  @JsonProperty("GlobalIPv6Address")
  String globalIPv6Address();

  @Nullable
  @JsonProperty("GlobalIPv6PrefixLen")
  Integer globalIPv6PrefixLen();

  @Nullable
  @JsonProperty("MacAddress")
  String macAddress();

  public static Builder builder() {
    return ImmutableEndpointConfig.builder();
  }

  interface Builder {

    Builder ipamConfig(EndpointIpamConfig ipamConfig);

    Builder links(Iterable<String> links);

    Builder aliases(Iterable<String> aliases);

    Builder gateway(String gateway);

    Builder ipAddress(String ipAddress);

    Builder ipPrefixLen(Integer ipPrefixLen);

    Builder ipv6Gateway(String ipv6Gateway);

    Builder globalIPv6Address(String globalIPv6Address);

    Builder globalIPv6PrefixLen(Integer globalIPv6PrefixLen);

    Builder macAddress(String macAddress);

    EndpointConfig build();
  }

  @JsonDeserialize(builder = ImmutableEndpointConfig.EndpointIpamConfig.Builder.class)
  @Immutable
  public interface EndpointIpamConfig {

    @Nullable
    @JsonProperty("IPv4Address")
    String ipv4Address();

    @Nullable
    @JsonProperty("IPv6Address")
    String ipv6Address();

    @Nullable
    @JsonProperty("LinkLocalIPs")
    List<String> linkLocalIPs();

    public static Builder builder() {
      return ImmutableEndpointConfig.EndpointIpamConfig.builder();
    }

    interface Builder {

      Builder ipv4Address(String ipv4Address);

      Builder ipv6Address(String ipv6Address);

      Builder linkLocalIPs(Iterable<String> linkLocalIPs);

      EndpointIpamConfig build();
    }
  }
}
