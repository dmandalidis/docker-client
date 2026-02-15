/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
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

package org.mandas.docker.client;

import java.util.Map;

import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.RegistryAuth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Represents the contents of the docker config.json file.
 * @param credHelpers credential helpers mapping
 * @param auths authentication details mapping
 * @param httpHeaders HTTP headers mapping
 * @param credsStore optional credentials store
 * @param detachKeys optional detach keys
 * @param stackOrchestrator optional stack orchestrator
 * @param psFormat optional ps format
 * @param imagesFormat optional images format
 */
@JsonDeserialize(builder = DockerConfig.Builder.class)
public record DockerConfig(
    @JsonProperty("credHelpers")
    Map<String, String> credHelpers,
    @JsonProperty("auths")
    Map<String, RegistryAuth> auths,
    @JsonProperty("HttpHeaders")
    Map<String, String> httpHeaders,
    @Nullable
    @JsonProperty("credsStore")
    String credsStore,
    @Nullable
    @JsonProperty("detachKeys")
    String detachKeys,
    @Nullable
    @JsonProperty("stackOrchestrator")
    String stackOrchestrator,
    @Nullable
    @JsonProperty("psFormat")
    String psFormat,
    @Nullable
    @JsonProperty("imagesFormat")
    String imagesFormat) {

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private Map<String, String> credHelpers;
    private Map<String, RegistryAuth> auths;
    private Map<String, String> httpHeaders;
    private String credsStore;
    private String detachKeys;
    private String stackOrchestrator;
    private String psFormat;
    private String imagesFormat;

    public Builder credHelpers(Map<String, String> credHelpers) {
      this.credHelpers = credHelpers;
      return this;
    }

    public Builder auths(Map<String, RegistryAuth> auths) {
      this.auths = auths;
      return this;
    }

    public Builder httpHeaders(Map<String, String> httpHeaders) {
      this.httpHeaders = httpHeaders;
      return this;
    }

    public Builder credsStore(String credsStore) {
      this.credsStore = credsStore;
      return this;
    }

    public Builder detachKeys(String detachKeys) {
      this.detachKeys = detachKeys;
      return this;
    }

    public Builder stackOrchestrator(String stackOrchestrator) {
      this.stackOrchestrator = stackOrchestrator;
      return this;
    }

    public Builder psFormat(String psFormat) {
      this.psFormat = psFormat;
      return this;
    }

    public Builder imagesFormat(String imagesFormat) {
      this.imagesFormat = imagesFormat;
      return this;
    }

    public DockerConfig build() {
      return new DockerConfig(credHelpers, auths, httpHeaders, credsStore, detachKeys, 
          stackOrchestrator, psFormat, imagesFormat);
    }
  }
}
