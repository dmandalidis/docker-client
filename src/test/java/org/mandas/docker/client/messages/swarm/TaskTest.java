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

package org.mandas.docker.client.messages.swarm;

import static org.mandas.docker.FixtureUtil.fixture;

import org.junit.Test;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskTest {

  private final ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void test1_30() throws Exception {
    objectMapper.readValue(fixture("fixtures/1.30/task.json"), Task.class);
  }

  @Test
  public void test1_30_noContainerStatus() throws Exception {
    objectMapper.readValue(fixture("fixtures/1.30/taskNoContainerStatus.json"), Task.class);
  }
}