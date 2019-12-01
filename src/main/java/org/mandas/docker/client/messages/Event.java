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

package org.mandas.docker.client.messages;

import java.util.Date;
import java.util.Map;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.jackson.UnixTimestampDeserializer;
import org.mandas.docker.client.jackson.UnixTimestampSerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(builder = ImmutableEvent.Builder.class)
@Immutable
@Enclosing
public interface Event {

  @Nullable
  @JsonProperty("Type")
  Type type();

  /**
   * Event action.
   * @return action
   * @since API 1.22
   */
  @Nullable
  @JsonProperty("Action")
  String action();

  /**
   * Event actor.
   * @return {@link Actor}
   * @since API 1.22
   */
  @Nullable
  @JsonProperty("Actor")
  Actor actor();

  @JsonProperty("time")
  @JsonDeserialize(using = UnixTimestampDeserializer.class)
  @JsonSerialize(using = UnixTimestampSerializer.class)
  Date time();

  @Nullable
  @JsonProperty("timeNano")
  Long timeNano();

  interface Builder {
	  Builder type(Type type);

	  Builder action(String action);
	  
	  Builder actor(Actor actor);
	  
	  Builder time(Date time);
	  
	  Builder timeNano(Long timeNano);
	  
	  Event build();
  }
  
  static Builder builder() {
	  return ImmutableEvent.builder();
  }
  
  @JsonDeserialize(builder = ImmutableEvent.Actor.Builder.class)
  @Immutable
  public interface Actor {

    @JsonProperty("ID")
    String id();

    @Nullable
    @JsonProperty("Attributes")
    Map<String, String> attributes();

    static Actor create(
        final String id,
        final Map<String, String> attributes) {

    	return ImmutableEvent.Actor.builder().id(id).attributes(attributes).build();
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
