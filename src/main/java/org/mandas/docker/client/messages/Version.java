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

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Version.Builder.class)
public record Version(
    @JsonProperty("ApiVersion")
    String apiVersion,
    @JsonProperty("Arch")
    String arch,
    @Nullable
    @JsonProperty("BuildTime")
    String buildTime,
    @JsonProperty("GitCommit")
    String gitCommit,
    @JsonProperty("GoVersion")
    String goVersion,
    @JsonProperty("KernelVersion")
    String kernelVersion,
    @JsonProperty("Os")
    String os,
    @JsonProperty("Version")
    String version) {

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String apiVersion;
    private String arch;
    private String buildTime;
    private String gitCommit;
    private String goVersion;
    private String kernelVersion;
    private String os;
    private String version;

    public Builder apiVersion(String apiVersion) {
      this.apiVersion = apiVersion;
      return this;
    }

    public Builder arch(String arch) {
      this.arch = arch;
      return this;
    }

    public Builder buildTime(String buildTime) {
      this.buildTime = buildTime;
      return this;
    }

    public Builder gitCommit(String gitCommit) {
      this.gitCommit = gitCommit;
      return this;
    }

    public Builder goVersion(String goVersion) {
      this.goVersion = goVersion;
      return this;
    }

    public Builder kernelVersion(String kernelVersion) {
      this.kernelVersion = kernelVersion;
      return this;
    }

    public Builder os(String os) {
      this.os = os;
      return this;
    }

    public Builder version(String version) {
      this.version = version;
      return this;
    }

    public Version build() {
      return new Version(apiVersion, arch, buildTime, gitCommit, goVersion, 
          kernelVersion, os, version);
    }
  }
}
