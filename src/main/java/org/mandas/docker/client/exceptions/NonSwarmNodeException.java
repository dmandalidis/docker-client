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

package org.mandas.docker.client.exceptions;

public class NonSwarmNodeException extends DockerException {

  public NonSwarmNodeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public NonSwarmNodeException(final Throwable cause) {
    super(null, cause);
  }

  public NonSwarmNodeException(final String message) {
    this(message, null);
  }
}
