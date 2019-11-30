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

package org.mandas.docker.client.messages.swarm;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutablePortConfig.Builder.class)
@Immutable
public interface PortConfig {

  public static final String PROTOCOL_TCP = "tcp";
  public static final String PROTOCOL_UDP = "udp";

  @Nullable
  @JsonProperty("Name")
  String name();

  @Nullable
  @JsonProperty("Protocol")
  String protocol();

  @Nullable
  @JsonProperty("TargetPort")
  Integer targetPort();

  @Nullable
  @JsonProperty("PublishedPort")
  Integer publishedPort();

  @Nullable
  @JsonProperty("PublishMode")
  PortConfigPublishMode publishMode();

  interface Builder {

    Builder name(String name);

    Builder protocol(String protocol);

    Builder targetPort(Integer targetPort);

    Builder publishedPort(Integer publishedPort);

    Builder publishMode(PortConfigPublishMode publishMode);

    PortConfig build();
  }

  public static Builder builder() {
    return ImmutablePortConfig.builder();
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
