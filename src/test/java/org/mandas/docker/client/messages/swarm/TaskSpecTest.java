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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mandas.docker.client.ObjectMapperProvider;
import org.junit.Test;

public class TaskSpecTest {

  private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void test1_32_WithoutNullables() throws Exception {
    final TaskSpec spec = objectMapper.readValue("{}", TaskSpec.class);
    assertThat(spec.containerSpec(), is(nullValue()));
    assertThat(spec.resources(), is(nullValue()));
    assertThat(spec.restartPolicy(), is(nullValue()));
    assertThat(spec.placement(), is(nullValue()));
    assertThat(spec.networks(), is(nullValue()));
    assertThat(spec.logDriver(), is(nullValue()));
  }
}