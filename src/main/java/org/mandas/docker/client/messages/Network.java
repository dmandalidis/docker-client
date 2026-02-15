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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Network.Builder.class)
public record Network(
    @JsonProperty("Name")
    String name,
    @JsonProperty("Id")
    String id,
    @JsonProperty("Scope")
    String scope,
    @JsonProperty("Driver")
    String driver,
    @JsonProperty("IPAM")
    Ipam ipam,
    @Nullable
    @JsonProperty("Containers")
    Map<String, Container> containers,
    @Nullable
    @JsonProperty("Options")
    Map<String, String> options,
    @Nullable
    @JsonProperty("Internal")
    Boolean internal,
    @Nullable
    @JsonProperty("EnableIPv6")
    Boolean enableIPv6,
    @Nullable
    @JsonProperty("Labels")
    Map<String, String> labels,
    @Nullable
    @JsonProperty("Attachable")
    Boolean attachable) {

  /**
   * Container attached to the network
   * @param name optional container name
   * @param endpointId endpoint ID
   * @param macAddress MAC address
   * @param ipv4Address IPv4 address
   * @param ipv6Address IPv6 address
   */
  @JsonDeserialize(builder = Container.Builder.class)
  public record Container(
      @Nullable
      @JsonProperty("Name")
      String name,
      @JsonProperty("EndpointID")
      String endpointId,
      @JsonProperty("MacAddress")
      String macAddress,
      @JsonProperty("IPv4Address")
      String ipv4Address,
      @JsonProperty("IPv6Address")
      String ipv6Address) {

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
      private String name;
      private String endpointId;
      private String macAddress;
      private String ipv4Address;
      private String ipv6Address;

      public Builder name(String name) {
        this.name = name;
        return this;
      }

      public Builder endpointId(String endpointId) {
        this.endpointId = endpointId;
        return this;
      }

      public Builder macAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
      }

      public Builder ipv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
        return this;
      }

      public Builder ipv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
        return this;
      }

      public Container build() {
        return new Container(name, endpointId, macAddress, ipv4Address, ipv6Address);
      }
    }
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

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String name;
    private String id;
    private String scope;
    private String driver;
    private Ipam ipam;
    private Map<String, Container> containers;
    private Map<String, String> options;
    private Boolean internal;
    private Boolean enableIPv6;
    private Map<String, String> labels;
    private Boolean attachable;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder scope(String scope) {
      this.scope = scope;
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

    public Builder containers(Map<String, Container> containers) {
      this.containers = containers;
      return this;
    }

    public Builder options(Map<String, String> options) {
      this.options = options;
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

    public Builder labels(Map<String, String> labels) {
      this.labels = labels;
      return this;
    }

    public Builder attachable(Boolean attachable) {
      this.attachable = attachable;
      return this;
    }

    public Network build() {
      return new Network(name, id, scope, driver, ipam, containers, options, internal, 
          enableIPv6, labels, attachable);
    }
  }
}
