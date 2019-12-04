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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutablePortBinding.Builder.class)
@Immutable
public interface PortBinding {

  @Nullable
  @JsonProperty("HostIp")
  String hostIp();

  @JsonProperty("HostPort")
  String hostPort();

  public static PortBinding of(final String ip, final String port) {
    return ImmutablePortBinding.builder().hostIp(ip).hostPort(port).build();
  }

  public static PortBinding of(final String ip, final int port) {
    return ImmutablePortBinding.builder().hostIp(ip).hostPort(String.valueOf(port)).build();
  }

  public static PortBinding randomPort(final String ip) {
    return ImmutablePortBinding.builder().hostIp(ip).hostPort("").build();
  }
  
  /**
   * @deprecated use {@link #of(String, String)} instead
   */
  @Deprecated
  public static PortBinding create(final String hostIp, final String hostPort) {
	return ImmutablePortBinding.builder().hostIp(hostIp).hostPort(hostPort).build();  
  }
}
