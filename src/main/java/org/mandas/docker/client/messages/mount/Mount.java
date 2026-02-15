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

package org.mandas.docker.client.messages.mount;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Mount(
  @Nullable
  @JsonProperty("Type")
  String type,

  @Nullable
  @JsonProperty("Source")
  String source,

  @Nullable
  @JsonProperty("Target")
  String target,

  @Nullable
  @JsonProperty("ReadOnly")
  Boolean readOnly,

  @Nullable
  @JsonProperty("BindOptions")
  BindOptions bindOptions,

  @Nullable
  @JsonProperty("VolumeOptions")
  VolumeOptions volumeOptions,

  @Nullable
  @JsonProperty("TmpfsOptions")
  TmpfsOptions tmpfsOptions
) {

  

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String type;
    private String source;
    private String target;
    private Boolean readOnly;
    private BindOptions bindOptions;
    private VolumeOptions volumeOptions;
    private TmpfsOptions tmpfsOptions;

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder source(String source) {
      this.source = source;
      return this;
    }

    public Builder target(String target) {
      this.target = target;
      return this;
    }

    public Builder readOnly(Boolean readOnly) {
      this.readOnly = readOnly;
      return this;
    }

    public Builder bindOptions(BindOptions bindOptions) {
      this.bindOptions = bindOptions;
      return this;
    }

    public Builder volumeOptions(VolumeOptions volumeOptions) {
      this.volumeOptions = volumeOptions;
      return this;
    }

    public Builder tmpfsOptions(TmpfsOptions tmpfsOptions) {
      this.tmpfsOptions = tmpfsOptions;
      return this;
    }

    public Mount build() {
      return new Mount(type, source, target, readOnly, bindOptions, volumeOptions, tmpfsOptions);
    }
  }
}
