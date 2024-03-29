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

import static java.util.Collections.reverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mandas.docker.client.exceptions.DockerException;
import org.mandas.docker.client.messages.RegistryAuth;
import org.mandas.docker.client.messages.RegistryConfigs;

/**
 * A RegistryAuthSupplier that combines authentication info from multiple RegistryAuthSupplier
 * instances.
 *
 * <p>The order of the suppliers matters: RegistryAuthSuppliers earlier in the list are
 * checked first to see if they can handle authentication for the given operation before trying
 * later RegistryAuthSuppliers in the list (a RegistryAuthSupplier signals that it can't handle the
 * operation by returning {@code null}).</p>
 */
public class MultiRegistryAuthSupplier implements RegistryAuthSupplier {

  private final List<RegistryAuthSupplier> suppliers;

  public MultiRegistryAuthSupplier(final List<RegistryAuthSupplier> suppliers) {
    this.suppliers = suppliers;
  }

  @Override
  public RegistryAuth authFor(final String imageName) throws DockerException {
    for (RegistryAuthSupplier supplier : suppliers) {
      final RegistryAuth auth = supplier.authFor(imageName);
      if (auth != null) {
        return auth;
      }
    }
    return null;
  }

  @Override
  public RegistryAuth authForSwarm() throws DockerException {
    for (RegistryAuthSupplier supplier : suppliers) {
      final RegistryAuth auth = supplier.authForSwarm();
      if (auth != null) {
        return auth;
      }
    }
    return null;

  }

  @Override
  public RegistryConfigs authForBuild() throws DockerException {
    final Map<String, RegistryAuth> allConfigs = new HashMap<>();
    // iterate through suppliers in reverse so that the earlier suppliers in the list
    // have precedence
    List<RegistryAuthSupplier> reversedSuppliers = new ArrayList<>(suppliers);
    reverse(reversedSuppliers);
    for (RegistryAuthSupplier supplier : reversedSuppliers) {
      final RegistryConfigs configs = supplier.authForBuild();
      if (configs != null && configs.configs() != null) {
        allConfigs.putAll(configs.configs());
      }
    }
    return RegistryConfigs.create(allConfigs);
  }
}
