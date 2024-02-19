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

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableNetwork.Builder.class)
@Immutable
@Enclosing
public interface Network {

  @JsonProperty("Name")
  String name();

  @JsonProperty("Id")
  String id();

  @JsonProperty("Scope")
  String scope();

  @JsonProperty("Driver")
  String driver();

  @JsonProperty("IPAM")
  Ipam ipam();

  @Nullable
  @JsonProperty("Containers")
  Map<String, Container> containers();

  @Nullable
  @JsonProperty("Options")
  Map<String, String> options();
  
  @Nullable
  @JsonProperty("Internal")
  Boolean internal();
  
  @Nullable
  @JsonProperty("EnableIPv6")
  Boolean enableIPv6();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  @Nullable
  @JsonProperty("Attachable")
  Boolean attachable();

  @JsonDeserialize(builder = ImmutableNetwork.Container.Builder.class)
  @Immutable
  public interface Container {

    @Nullable
    @JsonProperty("Name")
    String name();

    @JsonProperty("EndpointID")
    String endpointId();

    @JsonProperty("MacAddress")
    String macAddress();

    @JsonProperty("IPv4Address")
    String ipv4Address();

    @JsonProperty("IPv6Address")
    String ipv6Address();
  }
  
  /**
   * Docker networks come in two kinds: built-in or custom. 
   */
  public enum Type {
    /** Predefined networks that are built-in into Docker. */
    BUILTIN("builtin"),
    /** Custom networks that were created by users. */
    CUSTOM("custom");
    
    private final String name;

    @JsonCreator
    Type(final String name) {
      this.name = name;
    }

    @JsonValue
    public String getName() {
      return name;
    }
  }
}
