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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public record PortConfig(
  @Nullable
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("Protocol")
  String protocol,

  @Nullable
  @JsonProperty("TargetPort")
  Integer targetPort,

  @Nullable
  @JsonProperty("PublishedPort")
  Integer publishedPort,

  @Nullable
  @JsonProperty("PublishMode")
  PortConfigPublishMode publishMode
) {

  public static final String PROTOCOL_TCP = "tcp";
  public static final String PROTOCOL_UDP = "udp";

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private String protocol;
    private Integer targetPort;
    private Integer publishedPort;
    private PortConfigPublishMode publishMode;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder protocol(String protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder targetPort(Integer targetPort) {
      this.targetPort = targetPort;
      return this;
    }

    public Builder publishedPort(Integer publishedPort) {
      this.publishedPort = publishedPort;
      return this;
    }

    public Builder publishMode(PortConfigPublishMode publishMode) {
      this.publishMode = publishMode;
      return this;
    }

    public PortConfig build() {
      return new PortConfig(name, protocol, targetPort, publishedPort, publishMode);
    }
  }

  public enum PortConfigPublishMode {
    INGRESS("ingress"),
    HOST("host");

    private final String name;

    @JsonCreator
    PortConfigPublishMode(final String name) {
      this.name = name;
    }

    @JsonValue
    public String getName() {
      return name;
    }
  }
}
