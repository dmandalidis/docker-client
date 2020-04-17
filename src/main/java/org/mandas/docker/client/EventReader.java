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

package org.mandas.docker.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.mandas.docker.client.messages.Event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventReader implements Closeable {

  private final ObjectMapper objectMapper;
  private JsonParser parser;
  private final InputStream stream;

  public EventReader(final InputStream stream, final ObjectMapper objectMapper) {
    this.stream = stream;
    this.objectMapper = objectMapper;
  }

  public Event nextMessage() throws IOException {
    if (this.parser == null) {
      this.parser = objectMapper.getFactory().createParser(stream);
    }

    // If the parser is closed, there's no new event
    if (this.parser.isClosed()) {
      return null;
    }

    // Read tokens until we get a start object
    if (parser.nextToken() == null) {
      return null;
    }

    return parser.readValueAs(Event.class);
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }

}
