/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2018 Spotify AB
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

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;


@AutoValue
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = ANY)
public abstract class Descriptor {

  @JsonProperty("MediaType")
  public abstract String mediaType();

  @JsonProperty("Digest")
  public abstract String digest();

  @JsonProperty("Size")
  public abstract Long size();

  @JsonProperty("URLs")
  public abstract ImmutableList<String> urls();

  @JsonCreator
  static Descriptor create(
            @JsonProperty("MediaType") String mediaType,
            @JsonProperty("Digest") String digest,
            @JsonProperty("Size") Long size,
            @JsonProperty("URLs") ImmutableList<String> urls) {
    return new AutoValue_Descriptor(mediaType, digest, size, urls);
  }
}
