/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventTest {

  @Test
  public void serializationRoundTripTest() throws Exception {
    // Test serializing and deserializing the same Event instance works and preserves data
	Map<String, String> attributes = new HashMap<>();
	attributes.put("image", "nginx");
	attributes.put("name", "docker-nginx");
    final Event event = Event.builder().type(Event.Type.CONTAINER).action("create")
        .actor(Event.Actor.create("bar", attributes))
        .time(new Date(1487356000)).timeNano(100L).build();

    final ObjectMapper mapper = ObjectMapperProvider.objectMapper();

    final String json = mapper.writeValueAsString(event);

    final Event event2 = mapper.readValue(json, Event.class);
    assertThat(event, equalTo(event2));
  }
}