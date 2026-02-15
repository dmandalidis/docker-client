/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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

public record SwarmJoin(
  @JsonProperty("ListenAddr")
  String listenAddr,

  @Nullable
  @JsonProperty("AdvertiseAddr")
  String advertiseAddr,

  @JsonProperty("RemoteAddrs")
  List<String> remoteAddrs,

  @JsonProperty("JoinToken")
  String joinToken
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String listenAddr;
    private String advertiseAddr;
    private List<String> remoteAddrs;
    private String joinToken;

    public Builder listenAddr(String listenAddr) {
      this.listenAddr = listenAddr;
      return this;
    }

    public Builder advertiseAddr(String advertiseAddr) {
      this.advertiseAddr = advertiseAddr;
      return this;
    }

    public Builder remoteAddrs(List<String> remoteAddrs) {
      this.remoteAddrs = remoteAddrs;
      return this;
    }

    public Builder joinToken(String joinToken) {
      this.joinToken = joinToken;
      return this;
    }

    public SwarmJoin build() {
      return new SwarmJoin(listenAddr, advertiseAddr, remoteAddrs, joinToken);
    }
  }
}
