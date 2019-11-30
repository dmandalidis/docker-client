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

package org.mandas.docker.client.messages.mount;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableMount.Builder.class)
@Immutable
public interface Mount {

  @Nullable
  @JsonProperty("Type")
  String type();

  @Nullable
  @JsonProperty("Source")
  String source();

  @Nullable
  @JsonProperty("Target")
  String target();

  @Nullable
  @JsonProperty("ReadOnly")
  Boolean readOnly();

  @Nullable
  @JsonProperty("BindOptions")
  BindOptions bindOptions();

  @Nullable
  @JsonProperty("VolumeOptions")
  VolumeOptions volumeOptions();

  @Nullable
  @JsonProperty("TmpfsOptions")
  TmpfsOptions tmpfsOptions();

  interface Builder {

    Builder type(String type);

    Builder source(String source);

    Builder target(String target);

    Builder readOnly(Boolean readOnly);

    Builder bindOptions(BindOptions bindOptions);

    Builder volumeOptions(VolumeOptions volumeOptions);

    Builder tmpfsOptions(TmpfsOptions tmpfsOptions);

    Mount build();
  }

  public static Builder builder() {
    return ImmutableMount.builder();
  }
}
