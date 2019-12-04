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

import java.util.Map;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableLogConfig.Builder.class)
@Immutable
public interface LogConfig {

  @JsonProperty("Type")
  String logType();

  @Nullable
  @JsonProperty("Config")
  Map<String, String> logOptions();

  static LogConfig create(final String logType) {
    return ImmutableLogConfig.builder().logType(logType).build();
  }
  
  static LogConfig create(final String logType, final Map<String, String> logOptions) {
    return ImmutableLogConfig.builder().logType(logType).logOptions(logOptions).build();  
  }
  
  interface Builder {
	  Builder logType(String logType);
	  Builder logOptions(Map<String, ? extends String> logOptions);
	  LogConfig build();
  }
  
  static Builder builder() {
	  return ImmutableLogConfig.builder();
  }
}
