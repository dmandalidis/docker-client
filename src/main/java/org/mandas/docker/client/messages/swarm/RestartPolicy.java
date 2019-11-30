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

@JsonDeserialize(builder = ImmutableRestartPolicy.Builder.class)
@Immutable
public interface RestartPolicy {

  public static final String RESTART_POLICY_NONE = "none";
  public static final String RESTART_POLICY_ON_FAILURE = "on-failure";
  public static final String RESTART_POLICY_ANY = "any";

  @Nullable
  @JsonProperty("Condition")
  String condition();

  @Nullable
  @JsonProperty("Delay")
  Long delay();

  @Nullable
  @JsonProperty("MaxAttempts")
  Integer maxAttempts();

  @Nullable
  @JsonProperty("Window")
  Long window();

  interface Builder {

    Builder condition(String condition);

    Builder delay(Long delay);

    Builder maxAttempts(Integer maxAttempts);

    Builder window(Long window);

    RestartPolicy build();
  }

  public static Builder builder() {
    return ImmutableRestartPolicy.builder();
  }

}
