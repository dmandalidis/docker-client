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

/**
 * An object that represents the JSON returned by the Docker API for an exec command's process
 * configuration.
 * @param privileged whether the process runs with elevated privileges
 * @param user optional user for the process
 * @param tty whether a pseudo-terminal is allocated
 * @param entrypoint the entrypoint command
 * @param arguments the command arguments
 */
@JsonDeserialize(builder = ProcessConfig.Builder.class)
public record ProcessConfig(
    @JsonProperty("privileged")
    Boolean privileged,
    @Nullable
    @JsonProperty("user")
    String user,
    @JsonProperty("tty")
    Boolean tty,
    @JsonProperty("entrypoint")
    String entrypoint,
    @JsonProperty("arguments")
    List<String> arguments) {

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private Boolean privileged;
    private String user;
    private Boolean tty;
    private String entrypoint;
    private List<String> arguments;

    public Builder privileged(Boolean privileged) {
      this.privileged = privileged;
      return this;
    }

    public Builder user(String user) {
      this.user = user;
      return this;
    }

    public Builder tty(Boolean tty) {
      this.tty = tty;
      return this;
    }

    public Builder entrypoint(String entrypoint) {
      this.entrypoint = entrypoint;
      return this;
    }

    public Builder arguments(List<String> arguments) {
      this.arguments = arguments;
      return this;
    }

    public ProcessConfig build() {
      return new ProcessConfig(privileged, user, tty, entrypoint, arguments);
    }
  }
}
