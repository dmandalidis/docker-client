/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2024 Dimitris Mandalidis
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
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableHealthcheck.Builder.class)
@Immutable
public interface Healthcheck {
  @Nullable
  @JsonProperty("Test")
  List<String> test();

  /**
   * @return interval in nanoseconds.
   */
  @Nullable
  @JsonProperty("Interval")
  Long interval();

  /**
   * @return timeout in nanoseconds.
   */
  @Nullable
  @JsonProperty("Timeout")
  Long timeout();

  @Nullable
  @JsonProperty("Retries")
  Integer retries();

  /**
   * @return start period in nanoseconds.
   * @since API 1.29
   */
  @Nullable
  @JsonProperty("StartPeriod")
  Long startPeriod();

  static Healthcheck create(
          final List<String> test,
          final Long interval,
          final Long timeout,
          final Integer retries) {
    return create(test, interval, timeout, retries, null);
  }

  static Healthcheck create(
          final List<String> test,
          final Long interval,
          final Long timeout,
          final Integer retries,
          final Long startPeriod) {
    return builder()
        .test(test)
        .interval(interval)
        .timeout(timeout)
        .retries(retries)
        .startPeriod(startPeriod)
        .build();
  }
  
  public static Healthcheck.Builder builder() {
    return ImmutableHealthcheck.builder();
  }

  interface Builder {
    Healthcheck.Builder test(final Iterable<String> test);

    Healthcheck.Builder interval(final Long interval);

    Healthcheck.Builder timeout(final Long timeout);

    Healthcheck.Builder retries(final Integer retries);

    Healthcheck.Builder startPeriod(final Long startPeriod);

    Healthcheck build();
  }
}