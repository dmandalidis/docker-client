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

import java.util.Map;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@JsonDeserialize(builder = ImmutableVolume.Builder.class)
public interface Volume {

  @Nullable
  @JsonProperty("Name")
  String name();

  @Nullable
  @JsonProperty("Driver")
  String driver();

  @Nullable
  @JsonProperty("DriverOpts")
  Map<String, String> driverOpts();

  @Nullable
  @JsonProperty("Options")
  Map<String, String> options();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  @Nullable
  @JsonProperty("Mountpoint")
  String mountpoint();

  @Nullable
  @JsonProperty("Scope")
  String scope();

  @Nullable
  @JsonProperty("Status")
  Map<String, String> status();

  public static Builder builder() {
    return ImmutableVolume.builder();
  }

  public interface Builder {

    Builder name(String name);

    Builder driver(String driver);

    Builder driverOpts(Map<String, ? extends String> driverOpts);

    Builder options(Map<String, ? extends String> options);

    Builder labels(Map<String, ? extends String> labels);

    Builder mountpoint(String mountpoint);

    Builder scope(String scope);

    Builder status(Map<String, ? extends String> status);

    Volume build();
  }
}
