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

package org.mandas.docker.client.messages;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Immutable
public interface RemovedImage {

  Type type();

  @Nullable
  String imageId();

  @JsonCreator
  public static RemovedImage create(
      @JsonProperty("Untagged") final String untagged,
      @JsonProperty("Deleted") final String deleted) {
    if (untagged != null) {
      return ImmutableRemovedImage.builder().type(Type.UNTAGGED).imageId(untagged).build();
    } else if (deleted != null) {
      return ImmutableRemovedImage.builder().type(Type.DELETED).imageId(deleted).build();
    } else {
      return ImmutableRemovedImage.builder().type(Type.UNKNOWN).build();
    }
  }

  public static RemovedImage create(final Type type, final String imageId) {
    return ImmutableRemovedImage.builder().type(type).imageId(imageId).build();
  }

  public enum Type {
    UNTAGGED,
    DELETED,
    UNKNOWN
  }
}

