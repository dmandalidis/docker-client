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

import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableProgressMessage.Builder.class)
@Immutable
public interface ProgressMessage {

  // Prefix that appears before the actual image digest in a 1.6 status message. E.g.:
  // {"status":"Digest: sha256:ebd39c3e3962f804787f6b0520f8f1e35fbd5a01ab778ac14c8d6c37978e8445"}
  static final String STATUS_DIGEST_PREFIX_16 = "Digest: ";

  // In 1.8, the message instead looks like
  // {"status":"<some-tag>: digest: <digest> size: <size>"}
  static final String STATUS_DIGEST_PREFIX_18 = "digest: ";
  static final String STATUS_SIZE_PREFIX_18 = "size: ";

  @Nullable
  @JsonProperty("id")
  String id();

  @Nullable
  @JsonProperty("status")
  String status();

  @Nullable
  @JsonProperty("stream")
  String stream();

  @Nullable
  @JsonProperty("error")
  String error();

  @Nullable
  @JsonProperty("progress")
  String progress();

  @Nullable
  @JsonProperty("progressDetail")
  ProgressDetail progressDetail();

  public static Builder builder() {
    return ImmutableProgressMessage.builder();
  }

  interface Builder {

    Builder id(String id);

    Builder status(String status);

    Builder stream(String stream);

    Builder error(String error);

    Builder progress(String progress);

    Builder progressDetail(ProgressDetail progressDetail);

    ProgressMessage build();
  }

  /**
   * Checks if the stream field contains a string a like "Successfully built 2d6e00052167", and if
   * so, returns the image id. Otherwise null is returned. This string is expected when an image is
   * built successfully.
   *
   * @return The image id if this is a build success message, otherwise null.
   */
  @JsonIgnore
  @Derived
  @Nullable
  default String buildImageId() {
    // stream messages end with new line, so call trim to remove it
    final String stream = stream();
    return stream != null && stream.startsWith("Successfully built")
           ? stream.substring(stream.lastIndexOf(' ') + 1).trim()
           : null;
  }

  @JsonIgnore
  @Derived
  @Nullable
  default String digest() {
    final String status = status();
    if (status == null) {
      return null;
    }

    // the 1.6 format:
    // Digest : <digest ... >
    if (status.startsWith(STATUS_DIGEST_PREFIX_16)) {
      return status.substring(STATUS_DIGEST_PREFIX_16.length()).trim();
    }

    // the 1.8 format:
    // <image-tag>: digest: <digest...> size: <some count of bytes>
    final int digestIndex = status.indexOf(STATUS_DIGEST_PREFIX_18);
    final int sizeIndex = status.indexOf(STATUS_SIZE_PREFIX_18);
    // make sure both substrings exist and that size comes after digest
    if (digestIndex > -1 && sizeIndex > digestIndex) {
      final int start = digestIndex + STATUS_DIGEST_PREFIX_18.length();
      return status.substring(start, sizeIndex - 1);
    }

    return null;
  }
}
