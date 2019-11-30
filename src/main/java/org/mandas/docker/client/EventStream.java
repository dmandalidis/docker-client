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
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.mandas.docker.client.messages.Event;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventStream implements Iterator<Event>, Closeable {

  private final EventReader reader;

  private Event next;
  
  EventStream(final CloseableHttpResponse response, final ObjectMapper objectMapper) {
    this.reader = new EventReader(response, objectMapper);
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public boolean hasNext() {
	if (next != null) {
	  return true;
	}
	try {
	  next = reader.nextMessage();
	} catch (IOException e) {
	  throw new RuntimeException(e);
	}
	return next != null;
  }

  @Override
  public Event next() {
	if (!hasNext()) {
	  throw new NoSuchElementException();
	}
	Event value = next;
	next = null;
	return value;
  }
}
