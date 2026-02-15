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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Healthcheck(
  @Nullable
  @JsonProperty("Test")
  List<String> test,

  @Nullable
  @JsonProperty("Interval")
  Long interval,

  @Nullable
  @JsonProperty("Timeout")
  Long timeout,

  @Nullable
  @JsonProperty("Retries")
  Integer retries,

  @Nullable
  @JsonProperty("StartPeriod")
  Long startPeriod
) {

  public static Healthcheck create(
          final List<String> test,
          final Long interval,
          final Long timeout,
          final Integer retries) {
    return create(test, interval, timeout, retries, null);
  }

  public static Healthcheck create(
          final List<String> test,
          final Long interval,
          final Long timeout,
          final Integer retries,
          final Long startPeriod) {
    return new Healthcheck(test, interval, timeout, retries, startPeriod);
  }
  
  public static Healthcheck.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<String> test;
    private Long interval;
    private Long timeout;
    private Integer retries;
    private Long startPeriod;

    public Healthcheck.Builder test(final List<String> test) {
      this.test = test;
      return this;
    }

    public Healthcheck.Builder interval(final Long interval) {
      this.interval = interval;
      return this;
    }

    public Healthcheck.Builder timeout(final Long timeout) {
      this.timeout = timeout;
      return this;
    }

    public Healthcheck.Builder retries(final Integer retries) {
      this.retries = retries;
      return this;
    }

    public Healthcheck.Builder startPeriod(final Long startPeriod) {
      this.startPeriod = startPeriod;
      return this;
    }

    public Healthcheck build() {
      return new Healthcheck(test, interval, timeout, retries, startPeriod);
    }
  }
}
