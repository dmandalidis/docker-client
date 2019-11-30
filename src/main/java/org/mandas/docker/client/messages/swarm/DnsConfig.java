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

import java.util.List;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableDnsConfig.Builder.class)
@Immutable
public interface DnsConfig {

  @Nullable
  @JsonProperty("Nameservers")
  List<String> nameServers();

  @Nullable
  @JsonProperty("Search")
  List<String> search();

  @Nullable
  @JsonProperty("Options")
  List<String> options();


  interface Builder {

    Builder nameServers(String... nameServers);

    Builder nameServers(Iterable<String> nameServers);
    
    Builder search(String... search);
    
    Builder search(Iterable<String> search);
    
    Builder options(String... options);

    Builder options(Iterable<String> options);

    DnsConfig build();
  }

  public static Builder builder() {
    return ImmutableDnsConfig.builder();
  }
}
