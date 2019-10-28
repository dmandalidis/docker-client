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

package org.mandas.docker.client.messages;

import java.util.List;
import java.util.Map;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableIpam.Builder.class)
@Immutable
public interface Ipam {

  @JsonProperty("Driver")
  String driver();

  @Nullable
  @JsonProperty("Config")
  List<IpamConfig> config();

  @Nullable
  @JsonProperty("Options")
  Map<String, String> options();

  public static Builder builder() {
    return ImmutableIpam.builder();
  }

  interface Builder {

    Builder driver(String driver);

    Builder options(Map<String, ? extends String> options);

    Builder config(Iterable<? extends IpamConfig> config);

    public abstract Ipam build();
  }

  static Ipam create(final String driver, final List<IpamConfig> config) {
    return builder()
        .driver(driver)
        .config(config)
        .options(null)
        .build();
  }
}
