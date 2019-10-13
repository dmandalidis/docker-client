/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.mandas.docker.Nullable;

@AutoValue
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public abstract class TmpfsOptions {

  @Nullable
  @JsonProperty("SizeBytes")
  public abstract Long sizeBytes();

  /**
   * The mode and permission bits.
   */
  @Nullable
  @JsonProperty("Mode")
  public abstract Integer mode();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder sizeBytes(Long sizeBytes);

    public abstract Builder mode(Integer mode);

    public abstract TmpfsOptions build();
  }

  public static TmpfsOptions.Builder builder() {
    return new AutoValue_TmpfsOptions.Builder();
  }

  @JsonCreator
  static TmpfsOptions create(
      @JsonProperty("SizeBytes") final Long sizeBytes,
      @JsonProperty("Labels") final Integer mode) {
    return builder()
        .sizeBytes(sizeBytes)
        .mode(mode)
        .build();
  }
}
