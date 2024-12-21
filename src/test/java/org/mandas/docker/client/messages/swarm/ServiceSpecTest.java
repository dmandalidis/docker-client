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

package org.mandas.docker.client.messages.swarm;

import static org.mandas.docker.FixtureUtil.fixture;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mandas.docker.client.ObjectMapperProvider;
import org.junit.Test;

public class ServiceSpecTest {

  private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void test1_32_WithoutNullables() throws Exception {
    final ServiceSpec spec = objectMapper.readValue(fixture(
        "fixtures/1.32/serviceSpecWithoutNullables.json"), ServiceSpec.class);
    assertThat(spec.name(), is(nullValue()));
    assertThat(spec.labels(), is(nullValue()));
    assertThat(spec.taskTemplate(), is(notNullValue()));
    assertThat(spec.mode(), is(nullValue()));
    assertThat(spec.updateConfig(), is(nullValue()));
    assertThat(spec.taskTemplate().networks(), is(nullValue()));
    assertThat(spec.endpointSpec(), is(nullValue()));
  }
}
