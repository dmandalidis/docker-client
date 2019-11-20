/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
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

import java.io.IOException;
import java.util.Map;

import org.mandas.docker.client.messages.DockerCredentialHelperAuth;

/**
 * This class interacts with a docker credential helper.
 * See https://github.com/docker/docker-credential-helpers.
 *
 * <p>The credential helpers are platform-specific ways of storing and retrieving
 * registry auth information. Docker ships with OS-specific implementations,
 * such as osxkeychain and wincred, as well as others. But they also allow
 * third parties to implement their own credential helpers; for instance,
 * Google (https://github.com/GoogleCloudPlatform/docker-credential-gcr) and
 * Amazon (https://github.com/awslabs/amazon-ecr-credential-helper) have
 * implementations for their cloud registries.</p>
 *
 * <p>The main interface to this class is in four static methods, which perform the four
 * operations of a credential helper: {@link #get(String, String)}, {@link #list(String)},
 * {@link #store(String, DockerCredentialHelperAuth)}, and {@link #erase(String, String)}.
 * They all take the name of the credential helper as an argument; this value is usually read
 * as the credsStore or a credsHelper from a docker config file (see {@link DockerConfig}).</p>
 *
 * <p>The static methods all pass their operations down to a {@link CredentialHelperDelegate}
 * instance. By default this instance executes a command on the system. However, the delegate
 * is modifiable with {@link #setCredentialHelperDelegate(CredentialHelperDelegate)} and
 * {@link #restoreSystemCredentialHelperDelegate()} to facilitate testing.</p>
 */
public class DockerCredentialHelper {

  /**
   * An interface to be mocked during testing.
   */
  interface CredentialHelperDelegate {

    int store(String credsStore, DockerCredentialHelperAuth auth)
        throws IOException, InterruptedException;

    int erase(String credsStore, String registry) throws IOException, InterruptedException;

    DockerCredentialHelperAuth get(String credsStore, String registry) throws IOException;

    Map<String, String> list(String credsStore) throws IOException;
  }

  private static CredentialHelperDelegate credentialHelperDelegate =
      new SystemCredentialHelperDelegate();

  static void setCredentialHelperDelegate(final CredentialHelperDelegate delegate) {
    credentialHelperDelegate = delegate;
  }

  static void restoreSystemCredentialHelperDelegate() {
    credentialHelperDelegate = new SystemCredentialHelperDelegate();
  }

  /**
   * Store an auth value in the credsStore.
   * @param credsStore Name of the docker credential helper
   * @param auth Auth object to store
   * @return Exit code of the process
   * @throws IOException When we cannot read from the credential helper
   * @throws InterruptedException When writing to the credential helper
   *                              is interrupted
   */
  public static int store(final String credsStore, final DockerCredentialHelperAuth auth)
      throws IOException, InterruptedException {
    return credentialHelperDelegate.store(credsStore, auth);
  }

  /**
   * Erase an auth value from a credsStore matching a registry.
   * @param credsStore Name of the docker credential helper
   * @param registry The registry for which you want to erase the auth
   * @return Exit code of the process
   * @throws IOException When we cannot read from the credential helper
   * @throws InterruptedException When writing to the credential helper
   *                              is interrupted
   */
  public static int erase(final String credsStore, final String registry)
      throws IOException, InterruptedException {
    return credentialHelperDelegate.erase(credsStore, registry);
  }

  /**
   * Get an auth value from a credsStore for a registry.
   * @param credsStore Name of the docker credential helper
   * @param registry The registry for which you want to auth
   * @return A {@link DockerCredentialHelperAuth} auth object
   * @throws IOException When we cannot read from the credential helper
   */
  public static DockerCredentialHelperAuth get(final String credsStore, final String registry)
      throws IOException {
    return credentialHelperDelegate.get(credsStore, registry);
  }

  /**
   * Lists credentials stored in the credsStore.
   * @param credsStore Name of the docker credential helper
   * @return Map of registries to auth identifiers.
   *         (For instance, usernames for which you have signed in.)
   * @throws IOException When we cannot read from the credential helper
   */
  public static Map<String, String> list(final String credsStore) throws IOException {
    return credentialHelperDelegate.list(credsStore);
  }
}
