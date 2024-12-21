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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

import jnr.unixsocket.UnixSocket;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;


/**
 * Provides a ConnectionSocketFactory for connecting Apache HTTP clients to Unix sockets.
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class UnixConnectionSocketFactory implements ConnectionSocketFactory {

  private File socketFile;

  public UnixConnectionSocketFactory(final URI socketUri) {
    super();

    final String filename = socketUri.toString()
        .replaceAll("^unix:///", "unix://localhost/")
        .replaceAll("^unix://localhost", "");

    this.socketFile = new File(filename);
  }

  public static URI sanitizeUri(final URI uri) {
    if (uri.getScheme().equals("unix")) {
      return URI.create("unix://localhost:80");
    }
    return uri;
  }

  @Override
  public UnixSocket createSocket(final HttpContext context) throws IOException {
    return UnixSocketChannel.open().socket();
  }

  @Override
  public Socket connectSocket(final TimeValue connectTimeout,
                              final Socket socket,
                              final HttpHost host,
                              final InetSocketAddress remoteAddress,
                              final InetSocketAddress localAddress,
                              final HttpContext context) throws IOException {
    if (!(socket instanceof UnixSocket)) {
      throw new AssertionError("Unexpected socket: " + socket);
    }

    socket.setSoTimeout((int) connectTimeout.toDuration().toMillis());
    socket.getChannel().connect(new UnixSocketAddress(socketFile));
    return socket;
  }
}
