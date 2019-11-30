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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(builder = ImmutableUpdateConfig.Builder.class)
@Immutable
public interface UpdateConfig {

  @Nullable
  @JsonProperty("Parallelism")
  Long parallelism();

  @Nullable
  @JsonProperty("Delay")
  Long delay();

  @Nullable
  @JsonProperty("FailureAction")
  String failureAction();

  @Nullable
  @JsonProperty("Order")
  String order();

  public static UpdateConfig create(
      final Long parallelism,
      final Long delay,
      final String failureAction,
      final String order) {
	  
    return ImmutableUpdateConfig.builder()
    		.parallelism(parallelism)
    		.delay(delay)
    		.failureAction(failureAction)
    		.order(order).build();
  }
}
