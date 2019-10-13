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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mandas.docker.FixtureUtil.fixture;

import org.junit.Test;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HostConfigTest {

  private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void testJsonAlways() throws Exception {
    final HostConfig hostConfig = objectMapper
        .readValue(fixture("fixtures/hostConfig/restartPolicyAlways.json"),
                   HostConfig.class);
    assertThat(hostConfig.restartPolicy(), is(HostConfig.RestartPolicy.always()));
  }

  @Test
  public void testJsonUnlessStopped() throws Exception {
    final HostConfig hostConfig = objectMapper
        .readValue(fixture("fixtures/hostConfig/restartPolicyUnlessStopped.json"),
                   HostConfig.class);
    assertThat(hostConfig.restartPolicy(), is(HostConfig.RestartPolicy.unlessStopped()));
  }

  @Test
  public void testJsonOnFailure() throws Exception {
    final HostConfig hostConfig = objectMapper
        .readValue(fixture("fixtures/hostConfig/restartPolicyOnFailure.json"),
                   HostConfig.class);
    assertThat(hostConfig.restartPolicy(), is(HostConfig.RestartPolicy.onFailure(5)));
  }

  @Test
  public void testKernelMemory() throws Exception {
    final HostConfig hostConfig = objectMapper
        .readValue(fixture("fixtures/1.21/hostConfigKernelMemory.json"),
            HostConfig.class);
    assertThat(hostConfig.kernelMemory(), is(0L));
  }
}
