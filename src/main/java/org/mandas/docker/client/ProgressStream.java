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

package org.mandas.docker.client;

import static org.mandas.docker.client.ObjectMapperProvider.objectMapper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;

import org.mandas.docker.client.exceptions.DockerException;
import org.mandas.docker.client.exceptions.DockerTimeoutException;
import org.mandas.docker.client.messages.ProgressMessage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;


class ProgressStream implements Closeable {

  private final MappingIterator<ProgressMessage> iterator;
  private final JsonParser parser;

  ProgressStream(final InputStream stream) throws IOException {
    parser = objectMapper().getFactory().createParser(stream);
    iterator = objectMapper().readValues(parser, ProgressMessage.class);
  }

  public boolean hasNextMessage(final String method, final URI uri) throws DockerException {
    try {
      return iterator.hasNextValue();
    } catch (SocketTimeoutException e) {
      throw new DockerTimeoutException(method, uri, e);
    } catch (IOException e) {
      throw new DockerException(e);
    }
  }

  public ProgressMessage nextMessage(final String method, final URI uri) throws DockerException {
    try {
      return iterator.nextValue();
    } catch (SocketTimeoutException e) {
      throw new DockerTimeoutException(method, uri, e);
    } catch (IOException e) {
      throw new DockerException(e);
    }
  }

  public void tail(ProgressHandler handler, final String method, final URI uri)
      throws DockerException, InterruptedException {
    while (hasNextMessage(method, uri)) {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      handler.progress(nextMessage(method, uri));
    }
  }

  @Override
  public void close() throws IOException {
	parser.close();
  }
}
