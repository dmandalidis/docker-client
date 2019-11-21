/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mandas.docker.FixtureUtil.fixture;

import org.junit.Test;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DnsConfigTest {

  private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

  @Test
  public void test1_32() throws Exception {
    final DnsConfig config = objectMapper.readValue(fixture(
        "fixtures/1.32/dnsConfig.json"), DnsConfig.class);
    assertThat(config.nameServers(), contains("8.8.8.8"));
    assertThat(config.search(), contains("example.org"));
    assertThat(config.options(), contains("timeout:3"));
  }

  @Test
  public void test1_32_WithoutNullables() throws Exception {
    final DnsConfig config = objectMapper.readValue("{}", DnsConfig.class);
    assertThat(config.nameServers(), equalTo(emptyList()));
    assertThat(config.search(), equalTo(emptyList()));
    assertThat(config.options(), equalTo(emptyList()));
  }
}