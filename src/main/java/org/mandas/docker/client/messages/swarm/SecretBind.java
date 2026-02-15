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

import com.fasterxml.jackson.annotation.JsonProperty;

public record SecretBind(
  @JsonProperty("File")
  SecretFile file,

  @JsonProperty("SecretID")
  String secretId,

  @JsonProperty("SecretName")
  String secretName
) {

  public static Builder builder() {
    return new Builder();
  }

  

  public static class Builder {
    private SecretFile file;
    private String secretId;
    private String secretName;

    public Builder file(SecretFile file) {
      this.file = file;
      return this;
    }

    public Builder secretId(String secretId) {
      this.secretId = secretId;
      return this;
    }

    public Builder secretName(String secretName) {
      this.secretName = secretName;
      return this;
    }

    public SecretBind build() {
      return new SecretBind(file, secretId, secretName);
    }
  }
}
