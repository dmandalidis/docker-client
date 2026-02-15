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

public record ProgressMessage(
  @Nullable
  @JsonProperty("id")
  String id,

  @Nullable
  @JsonProperty("status")
  String status,

  @Nullable
  @JsonProperty("stream")
  String stream,

  @Nullable
  @JsonProperty("error")
  String error,

  @Nullable
  @JsonProperty("progress")
  String progress,

  @Nullable
  @JsonProperty("progressDetail")
  ProgressDetail progressDetail
) {

  static final String STATUS_DIGEST_PREFIX_16 = "Digest: ";
  static final String STATUS_DIGEST_PREFIX_18 = "digest: ";
  static final String STATUS_SIZE_PREFIX_18 = "size: ";

  public static Builder builder() {
    return new Builder();
  }

  @Nullable
  public String buildImageId() {
    final String stream = stream();
    return stream != null && stream.startsWith("Successfully built")
           ? stream.substring(stream.lastIndexOf(' ') + 1).trim()
           : null;
  }

  @Nullable
  public String digest() {
    final String status = status();
    if (status == null) {
      return null;
    }

    if (status.startsWith(STATUS_DIGEST_PREFIX_16)) {
      return status.substring(STATUS_DIGEST_PREFIX_16.length()).trim();
    }

    final int digestIndex = status.indexOf(STATUS_DIGEST_PREFIX_18);
    final int sizeIndex = status.indexOf(STATUS_SIZE_PREFIX_18);
    if (digestIndex > -1 && sizeIndex > digestIndex) {
      final int start = digestIndex + STATUS_DIGEST_PREFIX_18.length();
      return status.substring(start, sizeIndex - 1);
    }

    return null;
  }

  public static class Builder {
    private String id;
    private String status;
    private String stream;
    private String error;
    private String progress;
    private ProgressDetail progressDetail;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Builder stream(String stream) {
      this.stream = stream;
      return this;
    }

    public Builder error(String error) {
      this.error = error;
      return this;
    }

    public Builder progress(String progress) {
      this.progress = progress;
      return this;
    }

    public Builder progressDetail(ProgressDetail progressDetail) {
      this.progressDetail = progressDetail;
      return this;
    }

    public ProgressMessage build() {
      return new ProgressMessage(id, status, stream, error, progress, progressDetail);
    }
  }
}
