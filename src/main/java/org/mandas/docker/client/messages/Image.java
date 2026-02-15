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
import java.util.Map;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Image(
  @JsonProperty("Created")
  String created,

  @JsonProperty("Id")
  String id,

  @JsonProperty("ParentId")
  String parentId,

  @Nullable
  @JsonProperty("RepoTags")
  List<String> repoTags,

  @Nullable
  @JsonProperty("RepoDigests")
  List<String> repoDigests,

  @JsonProperty("Size")
  Long size,

  @Deprecated
  @JsonProperty("VirtualSize")
  Long virtualSize,

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels
) {
  public Image {
    if (virtualSize == null) {
      virtualSize = size;
    }
  }
}
