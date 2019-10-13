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

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableTmpfsOptions.Builder.class)
@Immutable
public interface TmpfsOptions {

  @Nullable
  @JsonProperty("SizeBytes")
  Long sizeBytes();

  /**
   * The mode and permission bits.
   */
  @Nullable
  @JsonProperty("Mode")
  Integer mode();

  interface Builder {

    Builder sizeBytes(Long sizeBytes);

    Builder mode(Integer mode);

    TmpfsOptions build();
  }

  public static TmpfsOptions.Builder builder() {
    return ImmutableTmpfsOptions.builder();
  }
}
