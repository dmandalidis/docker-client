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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

class DefaultLogStream implements LogStream {

  private final LogReader reader;
  private LogMessage next;
  
  private DefaultLogStream(final InputStream stream) {
    this(new LogReader(stream));
  }

  DefaultLogStream(final LogReader reader) {
    this.reader = reader;
  }

  static DefaultLogStream create(final InputStream stream) {
    return new DefaultLogStream(stream);
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
  public LogMessage next() {
	if (!hasNext()) {
		throw new NoSuchElementException();
	}
	LogMessage value = next;
	next = null;
	return value;
  }
  
  @Override
  public String readFully() {
    final StringBuilder stringBuilder = new StringBuilder();
    while (hasNext()) {
      stringBuilder.append(UTF_8.decode(next().content()));
    }
    return stringBuilder.toString();
  }

  @Override
  public void attach(final OutputStream stdout, final OutputStream stderr) throws IOException {
	  while (this.hasNext()) {
	    final LogMessage message = this.next();
	    final ByteBuffer content = message.content();
	
	    switch (message.stream()) {
	      case STDOUT:
	        writeAndFlush(content, stdout);
	        break;
	      case STDERR:
	        writeAndFlush(content, stderr);
	        break;
	      case STDIN:
	      default:
	        break;
	    }
	  }
  }

  /** Write the contents of the given ByteBuffer to the OutputStream and flush the stream. */
  private static void writeAndFlush(
      final ByteBuffer buffer, final OutputStream outputStream) throws IOException {

    if (buffer.hasArray()) {
      outputStream.write(buffer.array(), buffer.position(), buffer.remaining());
    } else {
      // cannot access underlying byte array, need to copy into a temporary array
      while (buffer.hasRemaining()) {
        // figure out how much to read, but use an upper limit of 8kb. LogMessages should be rather
        // small so we don't expect this to get hit but avoid large temporary buffers, just in case.
        final int size = Math.min(buffer.remaining(), 8 * 1024);
        final byte[] chunk = new byte[size];
        buffer.get(chunk);
        outputStream.write(chunk);
      }
    }
    outputStream.flush();
  }
}
