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

package org.mandas.docker.client.messages;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mandas.docker.FixtureUtil.fixture;

import java.util.Date;

import org.junit.Test;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContainerStateTest {

  private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void testLoadFromRandomFixture() throws Exception {
    final ContainerState containerState = objectMapper
        .readValue(fixture("fixtures/container-state-random.json"), ContainerState.class);
    assertThat(containerState.paused(), is(false));
    assertThat(containerState.restarting(), is(false));
    assertThat(containerState.running(), is(true));
    assertThat(containerState.exitCode(), is(0L));
    assertThat(containerState.pid(), is(27629));
    assertThat(containerState.startedAt(), is(new Date(1412236798929L)));
    assertThat(containerState.finishedAt(), is(new Date(-62135769600000L)));
    assertThat(containerState.error(), is("this is an error"));
    assertThat(containerState.oomKilled(), is(false));
    assertThat(containerState.status(), is("running"));
    
    ContainerState.Health health = containerState.health();
    assertThat(health.failingStreak(), is(1));
    assertThat(health.status(), is("starting"));
    assertThat(health.log().size(), is(1));
    
    ContainerState.HealthLog log = health.log().get(0);
    assertThat(log.start(), is(new Date(1412236801547L)));
    assertThat(log.end(), is(new Date(1412236802697L)));
    assertThat(log.exitCode(), is(1L));
    assertThat(log.output(), is("output"));
  }

  @Test
  public void testLoadFromRandomFixtureMissingProperty() throws Exception {
    objectMapper.readValue(fixture("fixtures/container-state-missing-property.json"),
                           ContainerState.class);
  }

  @Test(expected = JsonMappingException.class)
  public void testLoadInvalidConatainerStateJson() throws Exception {
    objectMapper.readValue(fixture("fixtures/container-state-invalid.json"), ContainerState.class);

  }

  @Test(expected = JsonParseException.class)
  public void testLoadInvalidJson() throws Exception {
    objectMapper.readValue(fixture("fixtures/invalid.json"), ContainerState.class);
  }
}
