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

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;

import org.mandas.docker.Nullable;

@AutoValue
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public abstract class PortConfig {

  public static final String PROTOCOL_TCP = "tcp";
  public static final String PROTOCOL_UDP = "udp";

  @Nullable
  @JsonProperty("Name")
  public abstract String name();

  @Nullable
  @JsonProperty("Protocol")
  public abstract String protocol();

  @Nullable
  @JsonProperty("TargetPort")
  public abstract Integer targetPort();

  @Nullable
  @JsonProperty("PublishedPort")
  public abstract Integer publishedPort();

  @Nullable
  @JsonProperty("PublishMode")
  public abstract PortConfigPublishMode publishMode();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder name(String name);

    public abstract Builder protocol(String protocol);

    public abstract Builder targetPort(Integer targetPort);

    public abstract Builder publishedPort(Integer publishedPort);

    public abstract Builder publishMode(PortConfigPublishMode publishMode);

    public abstract PortConfig build();
  }

  public static PortConfig.Builder builder() {
    return new AutoValue_PortConfig.Builder();
  }

  @JsonCreator
  static PortConfig create(
      @JsonProperty("Name") final String name,
      @JsonProperty("Protocol") final String protocol,
      @JsonProperty("TargetPort") final Integer targetPort,
      @JsonProperty("PublishedPort") final Integer publishedPort,
      @JsonProperty("PublishMode") final PortConfigPublishMode publishMode) {
    return builder()
        .name(name)
        .protocol(protocol)
        .targetPort(targetPort)
        .publishedPort(publishedPort)
        .publishMode(publishMode)
        .build();
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
