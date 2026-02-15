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

import java.util.Arrays;
import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DnsConfig(
  @Nullable
  @JsonProperty("Nameservers")
  List<String> nameServers,

  @Nullable
  @JsonProperty("Search")
  List<String> search,

  @Nullable
  @JsonProperty("Options")
  List<String> options
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<String> nameServers;
    private List<String> search;
    private List<String> options;

    public Builder nameServers(String... nameServers) {
      return nameServers(Arrays.asList(nameServers));
    }

    public Builder nameServers(List<String> nameServers) {
      this.nameServers = nameServers;
      return this;
    }

    public Builder search(String... search) {
      return search(Arrays.asList(search));
    }

    public Builder search(List<String> search) {
      this.search = search;
      return this;
    }

    public Builder options(String... options) {
      return options(Arrays.asList(options));
    }

    public Builder options(List<String> options) {
      this.options = options;
      return this;
    }

    public DnsConfig build() {
      return new DnsConfig(nameServers, search, options);
    }
  }
}
