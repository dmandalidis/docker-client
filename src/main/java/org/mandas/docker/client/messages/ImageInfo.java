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

import java.util.Date;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = ImageInfo.Builder.class)
public record ImageInfo(
    @JsonProperty("Id")
    String id,
    @Deprecated // as of v1.48
    @Nullable
    @JsonProperty("Parent")
    String parent,
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("Comment")
    String comment,
    @JsonProperty("Created")
    Date created,
    @Deprecated // as of v1.44
    @JsonProperty("Container")
    @Nullable
    String container,
    @Deprecated // as of v1.44
    @JsonProperty("ContainerConfig")
    @Nullable
    ContainerConfig containerConfig,
    @Deprecated // as of v1.48
    @Nullable
    @JsonProperty("DockerVersion")
    String dockerVersion,
    @Deprecated // as of v1.44
    @Nullable
    @JsonProperty("Author")
    String author,
    @JsonProperty("Config")
    ImageConfig config,
    @JsonProperty("Architecture")
    String architecture,
    @JsonProperty("Os")
    String os,
    @JsonProperty("Size")
    Long size,
    @Nullable
    @JsonProperty("RootFS")
    RootFs rootFs) {

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String id;
    private String parent;
    private String comment;
    private Date created;
    private String container;
    private ContainerConfig containerConfig;
    private String dockerVersion;
    private String author;
    private ImageConfig config;
    private String architecture;
    private String os;
    private Long size;
    private RootFs rootFs;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder parent(String parent) {
      this.parent = parent;
      return this;
    }

    public Builder comment(String comment) {
      this.comment = comment;
      return this;
    }

    public Builder created(Date created) {
      this.created = created;
      return this;
    }

    public Builder container(String container) {
      this.container = container;
      return this;
    }

    public Builder containerConfig(ContainerConfig containerConfig) {
      this.containerConfig = containerConfig;
      return this;
    }

    public Builder dockerVersion(String dockerVersion) {
      this.dockerVersion = dockerVersion;
      return this;
    }

    public Builder author(String author) {
      this.author = author;
      return this;
    }

    public Builder config(ImageConfig config) {
      this.config = config;
      return this;
    }

    public Builder architecture(String architecture) {
      this.architecture = architecture;
      return this;
    }

    public Builder os(String os) {
      this.os = os;
      return this;
    }

    public Builder size(Long size) {
      this.size = size;
      return this;
    }

    public Builder rootFs(RootFs rootFs) {
      this.rootFs = rootFs;
      return this;
    }

    public ImageInfo build() {
      return new ImageInfo(id, parent, comment, created, container, containerConfig, 
          dockerVersion, author, config, architecture, os, size, rootFs);
    }
  }
}
