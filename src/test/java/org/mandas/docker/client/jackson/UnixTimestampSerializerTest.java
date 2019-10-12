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

package org.mandas.docker.client.jackson;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.mandas.docker.client.ObjectMapperProvider;

import java.util.Date;

import org.junit.Test;

public class UnixTimestampSerializerTest {

  private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.objectMapper();

  private static class TestClass {

    @JsonProperty("date")
    @JsonSerialize(using = UnixTimestampSerializer.class)
    private Date date;

    TestClass(final Date date) {
      this.date = date;
    }
  }

  @Test
  public void testToString() throws Exception {
    final long timestamp = 1487357474682L;
    final String expectedJson = "{\"date\":1487357474}";
    final TestClass testClass = new TestClass(new Date(timestamp));

    final String json = OBJECT_MAPPER.writeValueAsString(testClass);
    assertThat(json, equalTo(expectedJson));
  }
}
