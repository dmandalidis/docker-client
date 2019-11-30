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

import java.util.Date;
import java.util.Map;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableService.Builder.class)
@Immutable
@Enclosing
public interface Service {

  @JsonProperty("ID")
  String id();

  @JsonProperty("Version")
  Version version();

  @JsonProperty("CreatedAt")
  Date createdAt();

  @JsonProperty("UpdatedAt")
  Date updatedAt();

  @JsonProperty("Spec")
  ServiceSpec spec();

  @JsonProperty("Endpoint")
  Endpoint endpoint();

  @Nullable
  @JsonProperty("UpdateStatus")
  UpdateStatus updateStatus();

  @JsonDeserialize(builder = ImmutableService.Criteria.Builder.class)
  @Immutable
  public interface Criteria {

    /**
     * Filter by service id.
     */
    @Nullable
    String serviceId();

    /**
     * Filter by service name.
     */
    @Nullable
    String serviceName();

    /**
     * Filter by label.
     */
    Map<String, String> labels();
    
    public static Criteria.Builder builder() {
      return ImmutableService.Criteria.builder();
    }

    interface Builder {

      Builder serviceId(final String serviceId);

      Builder serviceName(final String serviceName);

      Builder labels(final Map<String, ? extends String> labels);
      
      Builder addLabel(final String label, final String value);
      
      Criteria build();
    }
  }

  public static Criteria.Builder find() {
    return ImmutableService.Criteria.builder();
  }

}
