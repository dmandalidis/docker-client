/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
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

import static org.assertj.core.api.Assertions.*;
import static org.mandas.docker.client.SystemCredentialHelperDelegate.readServerAuthDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mandas.docker.client.messages.DockerCredentialHelperAuth;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemCredentialHelperDelegateTest {

  private ObjectMapper objectMapper;
  private SystemCredentialHelperDelegate delegate;

  @BeforeEach
  void setup() {
    objectMapper = ObjectMapperProvider.objectMapper();
    delegate = new SystemCredentialHelperDelegate();
  }

  @Test
  void testReadServerAuthDetails() throws Exception {
    final ObjectNode node = objectMapper.createObjectNode()
        .put("Username", "foo")
        .put("Secret", "bar")
        .put("ServerURL", "example.com");
    final StringReader input = new StringReader(objectMapper.writeValueAsString(node));
    final DockerCredentialHelperAuth auth = readServerAuthDetails(new BufferedReader(input));
    
    assertThat(auth.username()).isEqualTo("foo");
    assertThat(auth.secret()).isEqualTo("bar");
    assertThat(auth.serverUrl()).isEqualTo("example.com");
  }
  
  @Test
  void readServerAuthDetailsFromMultipleLines() throws Exception {
    final ObjectNode node = objectMapper.createObjectNode()
        .put("Username", "foo")
        .put("Secret", "bar")
        .put("ServerURL", "example.com");
    final StringReader input =
        new StringReader(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
    final DockerCredentialHelperAuth auth = readServerAuthDetails(new BufferedReader(input));
    
    assertThat(auth.username()).isEqualTo("foo");
    assertThat(auth.secret()).isEqualTo("bar");
    assertThat(auth.serverUrl()).isEqualTo("example.com");
  }
  
  @Test
  void readServerAuthDetailsNoServerUrl() throws Exception {
    final ObjectNode node = objectMapper.createObjectNode()
        .put("Username", "foo")
        .put("Secret", "bar");
    final StringReader input =
        new StringReader(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
    final DockerCredentialHelperAuth auth = readServerAuthDetails(new BufferedReader(input));
    
    assertThat(auth.username()).isEqualTo("foo");
    assertThat(auth.secret()).isEqualTo("bar");
    assertThat(auth.serverUrl()).isNull();
  }

  @Test
  void testGetRejectsPathTraversalInCredsStore() {
    assertThatThrownBy(() -> delegate.get("../../usr/bin/evil", "registry.example.com"))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("Invalid credential store name");
  }

  @Test
  void testStoreRejectsPathTraversalInCredsStore() {
    final DockerCredentialHelperAuth auth = DockerCredentialHelperAuth.builder()
        .username("user")
        .secret("pass")
        .serverUrl("registry.example.com")
        .build();
    assertThatThrownBy(() -> delegate.store("../../usr/bin/evil", auth))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("Invalid credential store name");
  }

  @Test
  void testEraseRejectsPathTraversalInCredsStore() {
    assertThatThrownBy(() -> delegate.erase("../../usr/bin/evil", "registry.example.com"))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("Invalid credential store name");
  }

  @Test
  void testListRejectsPathTraversalInCredsStore() {
    assertThatThrownBy(() -> delegate.list("../../usr/bin/evil"))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("Invalid credential store name");
  }

  @Test
  void testRejectsCredsStoreWithSpaces() {
    assertThatThrownBy(() -> delegate.get("valid-name; malicious-command", "registry.example.com"))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("Invalid credential store name");
  }

}
