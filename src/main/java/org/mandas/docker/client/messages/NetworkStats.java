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

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(builder = ImmutableNetworkStats.Builder.class)
@Immutable
public interface NetworkStats {

  @JsonProperty("rx_bytes")
  Long rxBytes();

  @JsonProperty("rx_packets")
  Long rxPackets();

  @JsonProperty("rx_dropped")
  Long rxDropped();

  @JsonProperty("rx_errors")
  Long rxErrors();

  @JsonProperty("tx_bytes")
  Long txBytes();

  @JsonProperty("tx_packets")
  Long txPackets();

  @JsonProperty("tx_dropped")
  Long txDropped();

  @JsonProperty("tx_errors")
  Long txErrors();
}
