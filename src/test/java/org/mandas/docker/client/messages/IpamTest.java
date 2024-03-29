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

package org.mandas.docker.client.messages;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mandas.docker.FixtureUtil.fixture;

import org.junit.Test;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IpamTest {

  private static final ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void testDeserialize() throws Exception {
    final Ipam ipam =
        objectMapper.readValue(fixture("fixtures/1.29/ipam.json"), Ipam.class);
    assertThat(ipam.driver(), equalTo("default"));
    assertThat(ipam.config(), contains(IpamConfig.builder().subnet("172.17.0.0/16").build()));
    assertThat(ipam.options(), equalTo(singletonMap("foo", "bar")));
  }

  @Test
  public void testDeserialize_nullConfig() throws Exception {
    final Ipam ipam =
        objectMapper.readValue(fixture("fixtures/1.29/ipam-null-config.json"), Ipam.class);
    assertThat(ipam.driver(), equalTo("default"));
    assertThat(ipam.config(), is(nullValue()));
  }
}