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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Device(
  @Nullable
  @JsonProperty("PathOnHost")
  String pathOnHost,

  @Nullable
  @JsonProperty("PathInContainer")
  String pathInContainer,

  @Nullable
  @JsonProperty("CgroupPermissions")
  String cgroupPermissions
) {

  public static Device.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String pathOnHost;
    private String pathInContainer;
    private String cgroupPermissions;

    public Builder pathOnHost(String pathOnHost) {
      this.pathOnHost = pathOnHost;
      return this;
    }

    public Builder pathInContainer(String pathInContainer) {
      this.pathInContainer = pathInContainer;
      return this;
    }

    public Builder cgroupPermissions(String cgroupPermissions) {
      this.cgroupPermissions = cgroupPermissions;
      return this;
    }

    public Device build() {
      return new Device(pathOnHost, pathInContainer, cgroupPermissions);
    }
  }
}
