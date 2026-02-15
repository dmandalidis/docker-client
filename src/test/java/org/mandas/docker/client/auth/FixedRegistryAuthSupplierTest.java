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

package org.mandas.docker.client.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.mandas.docker.client.exceptions.DockerException;
import org.mandas.docker.client.messages.RegistryAuth;
import org.junit.jupiter.api.Test;

public class FixedRegistryAuthSupplierTest {

  @Test
  public void authForReturnsWrappedAuthRegistry() throws DockerException {
    final RegistryAuth registryAuth = mock(RegistryAuth.class);
    FixedRegistryAuthSupplier fixedRegistryAuthSupplier =
        new FixedRegistryAuthSupplier(registryAuth, null);
    assertEquals(registryAuth, fixedRegistryAuthSupplier.authFor("doesn't matter"));
  }

  @Test
  public void authForReturnsNullForEmptyConstructor() throws DockerException {
    final FixedRegistryAuthSupplier fixedRegistryAuthSupplier = new FixedRegistryAuthSupplier();
    assertNull(fixedRegistryAuthSupplier.authFor("any"));
    assertNull(fixedRegistryAuthSupplier.authForBuild());
  }
}
