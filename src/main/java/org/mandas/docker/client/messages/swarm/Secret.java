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

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableSecret.Builder.class)
@Immutable
@Enclosing
public interface Secret {

  @JsonProperty("ID")
  String id();
  
  @JsonProperty("Version")
  Version version();

  @JsonProperty("CreatedAt")
  Date createdAt();

  @JsonProperty("UpdatedAt")
  Date updatedAt();

  @JsonProperty("Spec")
  SecretSpec secretSpec();
  
  @Immutable
  public interface Criteria {
    /**
     * @return Filter by secret id.
     */
    @Nullable
    String id();

    /**
     * @return Filter by label.
     */
    @Nullable
    String label();

    /**
     * @return Filter by secret name.
     */
    @Nullable
    String name();
    
    public static Criteria.Builder builder() {
      return ImmutableSecret.Criteria.builder();
    }

    interface Builder {
      Builder id(String id);

      Builder label(String label);

      Builder name(String name);
      
      Criteria build();
    }
  }
}
