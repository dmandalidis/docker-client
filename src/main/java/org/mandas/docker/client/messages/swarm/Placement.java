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

package org.mandas.docker.client.messages.swarm;

import java.util.List;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutablePlacement.Builder.class)
@Immutable
public interface Placement {

  @Nullable
  @JsonProperty("Constraints")
  List<String> constraints();

  @Nullable
  @JsonProperty("Preferences")
  List<Preference> preferences();

  public static Placement create(final List<String> constraints) {
    return create(constraints, null);
  }
  
  public static Placement create(final List<String> constraints, final List<Preference> preferences) {
    return ImmutablePlacement.builder().constraints(constraints).preferences(preferences).build();
  }
}
