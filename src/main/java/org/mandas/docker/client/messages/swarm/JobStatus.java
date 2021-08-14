/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2021 Dimitris Mandalidis
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

import java.util.Date;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableJobStatus.Builder.class)
@Immutable
public interface JobStatus {

  @JsonProperty("JobIteration")
  Version jobIteration();
  
  @JsonProperty("LastExecution")
  Date lastExecution();
  
  interface Builder {
    Builder jobIteration(Version iteration);
    Builder lastExecution(Date lastExecution);
    JobStatus build();
  }
  
  static Builder builder() {
    return ImmutableJobStatus.builder();
  }
}
