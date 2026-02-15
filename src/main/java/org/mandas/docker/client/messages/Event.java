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
import java.util.Map;

import org.mandas.docker.Nullable;
import org.mandas.docker.client.jackson.UnixTimestampDeserializer;
import org.mandas.docker.client.jackson.UnixTimestampSerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record Event(
  @Nullable
  @JsonProperty("Type")
  Type type,

  @Nullable
  @JsonProperty("Action")
  String action,

  @Nullable
  @JsonProperty("Actor")
  Actor actor,

  @JsonProperty("time")
  @JsonDeserialize(using = UnixTimestampDeserializer.class)
  @JsonSerialize(using = UnixTimestampSerializer.class)
  Date time,

  @Nullable
  @JsonProperty("timeNano")
  Long timeNano
) {

  
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Type type;
    private String action;
    private Actor actor;
    private Date time;
    private Long timeNano;

    public Builder type(Type type) {
      this.type = type;
      return this;
    }

    public Builder action(String action) {
      this.action = action;
      return this;
    }

    public Builder actor(Actor actor) {
      this.actor = actor;
      return this;
    }

    public Builder time(Date time) {
      this.time = time;
      return this;
    }

    public Builder timeNano(Long timeNano) {
      this.timeNano = timeNano;
      return this;
    }

    public Event build() {
      return new Event(type, action, actor, time, timeNano);
    }
  }
  
  public record Actor(
    @JsonProperty("ID")
    String id,

    @Nullable
    @JsonProperty("Attributes")
    Map<String, String> attributes
  ) {

    public static Actor create(
        final String id,
        final Map<String, String> attributes) {
      return new Actor(id, attributes);
    }
  }

  public enum Type {
    CONTAINER("container"),
    IMAGE("image"),
    VOLUME("volume"),
    NETWORK("network"),
    DAEMON("daemon"),
    PLUGIN("plugin"),
    NODE("node"),
    SERVICE("service"),
    SECRET("secret"),
    CONFIG("config");

    private final String name;

    @JsonCreator
    Type(final String name) {
      this.name = name;
    }

    @JsonValue
    public String getName() {
      return name;
    }
  }
}
