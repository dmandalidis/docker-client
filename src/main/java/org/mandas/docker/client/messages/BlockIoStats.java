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

import java.util.List;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@JsonDeserialize(builder = ImmutableBlockIoStats.Builder.class)
public interface BlockIoStats {

  @JsonProperty("io_service_bytes_recursive")
  List<Object> ioServiceBytesRecursive();

  @JsonProperty("io_serviced_recursive")
  List<Object> ioServicedRecursive();

  @JsonProperty("io_queue_recursive")
  List<Object> ioQueueRecursive();

  @JsonProperty("io_service_time_recursive")
  List<Object> ioServiceTimeRecursive();

  @JsonProperty("io_wait_time_recursive")
  List<Object> ioWaitTimeRecursive();

  @JsonProperty("io_merged_recursive")
  List<Object> ioMergedRecursive();

  @JsonProperty("io_time_recursive")
  List<Object> ioTimeRecursive();

  @JsonProperty("sectors_recursive")
  List<Object> sectorsRecursive();
}
