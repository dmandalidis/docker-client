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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableNetworkConfig.Builder.class)
@Immutable
public interface NetworkConfig {

  @JsonProperty("Name")
  String name();

  @Nullable
  @JsonProperty("Driver")
  String driver();

  @Nullable
  @JsonProperty("IPAM")
  Ipam ipam();

  @JsonProperty("Options")
  Map<String, String> options();

  @Nullable
  @JsonProperty("CheckDuplicate")
  Boolean checkDuplicate();
  
  @Nullable
  @JsonProperty("Internal")
  Boolean internal();
  
  @Nullable
  @JsonProperty("EnableIPv6")
  Boolean enableIPv6();

  @Nullable
  @JsonProperty("Attachable")
  Boolean attachable();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  public static Builder builder() {
    return ImmutableNetworkConfig.builder();
  }

  interface Builder {

    Builder name(final String name);

    Builder addOption(final String key, final String value);
    
    Builder options(Map<String, ? extends String> options);

    Builder ipam(final Ipam ipam);

    Builder driver(final String driver);

    Builder checkDuplicate(Boolean check);
    
    Builder internal(Boolean internal);
    
    Builder enableIPv6(Boolean ipv6);

    Builder attachable(Boolean attachable);

    Builder labels(Map<String, ? extends String> labels);
    
    NetworkConfig build();
  }

}
