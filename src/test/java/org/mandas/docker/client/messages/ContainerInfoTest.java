/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mandas.docker.client.ObjectMapperProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ContainerInfoTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void test1_22() throws Exception {
    objectMapper.readValue(fixture("fixtures/1.22/containerInfo.json"), ContainerInfo.class);
  }

  @Test
  public void test1_24() throws Exception {
    objectMapper.readValue(fixture("fixtures/1.24/containerInfo.json"), ContainerInfo.class);
  }

}