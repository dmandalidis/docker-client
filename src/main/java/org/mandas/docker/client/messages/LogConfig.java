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

import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = LogConfig.Builder.class)
public record LogConfig(
    @JsonProperty("Type")
    String logType,
    @Nullable
    @JsonProperty("Config")
    Map<String, String> logOptions) {

  static LogConfig create(final String logType) {
    return new LogConfig(logType, null);
  }
  
  static LogConfig create(final String logType, final Map<String, String> logOptions) {
    return new LogConfig(logType, logOptions);  
  }
  
  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String logType;
    private Map<String, String> logOptions;

    public Builder logType(String logType) {
      this.logType = logType;
      return this;
    }

    public Builder logOptions(Map<String, String> logOptions) {
      this.logOptions = logOptions;
      return this;
    }

    public LogConfig build() {
      return new LogConfig(logType, logOptions);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
