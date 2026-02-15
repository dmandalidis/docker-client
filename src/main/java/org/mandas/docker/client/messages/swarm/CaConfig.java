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

package org.mandas.docker.client.messages.swarm;

import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CaConfig(
  @Nullable
  @JsonProperty("NodeCertExpiry")
  Long nodeCertExpiry,

  @Nullable
  @JsonProperty("ExternalCAs")
  List<ExternalCa> externalCas
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long nodeCertExpiry;
    private List<ExternalCa> externalCas;

    public Builder nodeCertExpiry(Long nodeCertExpiry) {
      this.nodeCertExpiry = nodeCertExpiry;
      return this;
    }

    public Builder externalCas(List<ExternalCa> externalCas) {
      this.externalCas = externalCas;
      return this;
    }

    public CaConfig build() {
      return new CaConfig(nodeCertExpiry, 
          externalCas == null ? null : List.copyOf(externalCas));
    }
  }
}
