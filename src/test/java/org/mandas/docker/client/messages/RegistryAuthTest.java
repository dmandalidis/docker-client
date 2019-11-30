/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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

package org.mandas.docker.client.messages;

import static org.mandas.docker.FixtureUtil.fixture;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Base64;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mandas.docker.client.ObjectMapperProvider;

public class RegistryAuthTest {

  private static final ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void testDeserializingFromJson() throws Exception {
    final RegistryAuth registryAuth =
        objectMapper.readValue(fixture("fixtures/registryAuth.json"), RegistryAuth.class);
    assertThat(registryAuth.username(), equalTo("hannibal"));
    assertThat(registryAuth.password(), equalTo("xxxx"));
    assertThat(registryAuth.email(), equalTo("hannibal@a-team.com"));
    assertThat(registryAuth.serverAddress(), equalTo("https://index.docker.io/v1/"));
    assertThat(registryAuth.identityToken(), equalTo("foobar"));
  }

  @Test
  public void testForAuth() {
    final String username = "johndoe";
    final String password = "pass123";
    final String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

    final RegistryAuth registryAuth = RegistryAuth.forAuth(encoded).build();
    assertThat(registryAuth.username(), is(username));
    assertThat(registryAuth.password(), is(password));
  }

  @Test
  public void testForAuth_PasswordContainsColon() {
    final String username = "johndoe";
    final String password = "foo:bar";
    final String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

    final RegistryAuth registryAuth = RegistryAuth.forAuth(encoded).build();
    assertThat(registryAuth.username(), is(username));
    assertThat(registryAuth.password(), is(password));
  }
}
