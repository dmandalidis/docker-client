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

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContainerChange(
  @JsonProperty("Path")
  String path,

  @JsonProperty("Kind")
  Integer kind
) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private String path;
    private Integer kind;

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder kind(Integer kind) {
      this.kind = kind;
      return this;
    }

    public ContainerChange build() {
      return new ContainerChange(path, kind);
    }
  }
}
