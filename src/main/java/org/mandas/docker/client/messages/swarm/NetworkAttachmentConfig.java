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

import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NetworkAttachmentConfig(
  @Nullable
  @JsonProperty("Target")
  String target,

  @Nullable
  @JsonProperty("Aliases")
  List<String> aliases
) {

  public static Builder builder() {
    return new Builder();
  }

  

  public static class Builder {
    private String target;
    private List<String> aliases;

    public Builder target(String target) {
      this.target = target;
      return this;
    }

    public Builder aliases(String... aliases) {
      this.aliases = aliases == null ? null : List.of(aliases);
      return this;
    }

    public Builder aliases(List<String> aliases) {
      this.aliases = aliases;
      return this;
    }

    public NetworkAttachmentConfig build() {
      return new NetworkAttachmentConfig(target, 
          aliases == null ? null : List.copyOf(aliases));
    }
  }
}
