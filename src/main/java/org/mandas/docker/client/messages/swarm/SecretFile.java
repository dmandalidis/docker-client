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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SecretFile(
  @JsonProperty("Name")
  String name,

  @Nullable
  @JsonProperty("UID")
  String uid,

  @Nullable
  @JsonProperty("GID")
  String gid,

  @Nullable
  @JsonProperty("Mode")
  Long mode
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private String uid;
    private String gid;
    private Long mode;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder uid(String uid) {
      this.uid = uid;
      return this;
    }

    public Builder gid(String gid) {
      this.gid = gid;
      return this;
    }

    public Builder mode(Long mode) {
      this.mode = mode;
      return this;
    }

    public SecretFile build() {
      return new SecretFile(name, uid, gid, mode);
    }
  }
}
