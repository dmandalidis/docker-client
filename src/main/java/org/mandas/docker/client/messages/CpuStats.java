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

package org.mandas.docker.client.messages;

import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CpuStats.Builder.class)
public record CpuStats(
    @JsonProperty("cpu_usage")
    CpuUsage cpuUsage,
    @Nullable
    @JsonProperty("system_cpu_usage")
    Long systemCpuUsage,
    @JsonProperty("throttling_data")
    ThrottlingData throttlingData) {

  /**
   * CPU usage statistics
   * @param totalUsage total CPU usage
   * @param percpuUsage optional per-CPU usage
   * @param usageInKernelmode kernel mode usage
   * @param usageInUsermode user mode usage
   */
  @JsonDeserialize(builder = CpuUsage.Builder.class)
  public record CpuUsage(
      @JsonProperty("total_usage")
      Long totalUsage,
      @Nullable
      @JsonProperty("percpu_usage")
      List<Long> percpuUsage,
      @JsonProperty("usage_in_kernelmode")
      Long usageInKernelmode,
      @JsonProperty("usage_in_usermode")
      Long usageInUsermode) {

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
      private Long totalUsage;
      private List<Long> percpuUsage;
      private Long usageInKernelmode;
      private Long usageInUsermode;

      public Builder totalUsage(Long totalUsage) {
        this.totalUsage = totalUsage;
        return this;
      }

      public Builder percpuUsage(List<Long> percpuUsage) {
        this.percpuUsage = percpuUsage;
        return this;
      }

      public Builder usageInKernelmode(Long usageInKernelmode) {
        this.usageInKernelmode = usageInKernelmode;
        return this;
      }

      public Builder usageInUsermode(Long usageInUsermode) {
        this.usageInUsermode = usageInUsermode;
        return this;
      }

      public CpuUsage build() {
        return new CpuUsage(totalUsage, percpuUsage, usageInKernelmode, usageInUsermode);
      }
    }
  }

  /**
   * CPU throttling data
   * @param periods total periods
   * @param throttledPeriods throttled periods count
   * @param throttledTime throttled time
   */
  @JsonDeserialize(builder = ThrottlingData.Builder.class)
  public record ThrottlingData(
      @JsonProperty("periods")
      Long periods,
      @JsonProperty("throttled_periods")
      Long throttledPeriods,
      @JsonProperty("throttled_time")
      Long throttledTime) {

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
      private Long periods;
      private Long throttledPeriods;
      private Long throttledTime;

      public Builder periods(Long periods) {
        this.periods = periods;
        return this;
      }

      public Builder throttledPeriods(Long throttledPeriods) {
        this.throttledPeriods = throttledPeriods;
        return this;
      }

      public Builder throttledTime(Long throttledTime) {
        this.throttledTime = throttledTime;
        return this;
      }

      public ThrottlingData build() {
        return new ThrottlingData(periods, throttledPeriods, throttledTime);
      }
    }
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private CpuUsage cpuUsage;
    private Long systemCpuUsage;
    private ThrottlingData throttlingData;

    public Builder cpuUsage(CpuUsage cpuUsage) {
      this.cpuUsage = cpuUsage;
      return this;
    }

    public Builder systemCpuUsage(Long systemCpuUsage) {
      this.systemCpuUsage = systemCpuUsage;
      return this;
    }

    public Builder throttlingData(ThrottlingData throttlingData) {
      this.throttlingData = throttlingData;
      return this;
    }

    public CpuStats build() {
      return new CpuStats(cpuUsage, systemCpuUsage, throttlingData);
    }
  }
}
