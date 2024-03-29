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

package org.mandas.docker.client.auth;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mandas.docker.client.messages.RegistryAuth;
import org.mandas.docker.client.messages.RegistryConfigs;

public class MultiRegistryAuthSupplierTest {

  private final RegistryAuthSupplier supplier1 = mock(RegistryAuthSupplier.class);
  private final RegistryAuthSupplier supplier2 = mock(RegistryAuthSupplier.class);

  private final RegistryAuthSupplier multiSupplier =
      new MultiRegistryAuthSupplier(unmodifiableList(asList(supplier1, supplier2)));

  @Test
  public void testAuthFor() throws Exception {
    final String image1 = "foobar:latest";
    final RegistryAuth auth1 = RegistryAuth.builder().build();
    when(supplier1.authFor(image1)).thenReturn(auth1);

    assertThat(multiSupplier.authFor(image1), is(auth1));
    verify(supplier2, never()).authFor(image1);

    // test fallback
    final String image2 = "bizbat:1.2.3";
    final RegistryAuth auth2 = RegistryAuth.builder()
        .email("foo@biz.com")
        .build();
    when(supplier1.authFor(image2)).thenReturn(null);
    when(supplier2.authFor(image2)).thenReturn(auth2);

    assertThat(multiSupplier.authFor(image2), is(auth2));
  }

  @Test
  public void testAuthForSwarm() throws Exception {
    final RegistryAuth auth1 = RegistryAuth.builder()
        .email("a@b.com")
        .build();
    when(supplier1.authForSwarm()).thenReturn(auth1);

    assertThat(multiSupplier.authForSwarm(), is(auth1));
    verify(supplier2, never()).authForSwarm();

    // test fallback
    final RegistryAuth auth2 = RegistryAuth.builder()
        .email("foo@biz.com")
        .build();
    when(supplier1.authForSwarm()).thenReturn(null);
    when(supplier2.authForSwarm()).thenReturn(auth2);

    assertThat(multiSupplier.authForSwarm(), is(auth2));
  }

  @Test
  public void testAuthForBuild() throws Exception {

    final RegistryAuth auth1 = RegistryAuth.builder()
        .username("1")
        .serverAddress("a")
        .build();

    final RegistryAuth auth2 = RegistryAuth.builder()
        .username("2")
        .serverAddress("b")
        .build();

    final RegistryAuth auth3 = RegistryAuth.builder()
        .username("3")
        .serverAddress("b")
        .build();

    final RegistryAuth auth4 = RegistryAuth.builder()
        .username("4")
        .serverAddress("c")
        .build();

    Map<String, RegistryAuth> expectedSup1Auths = new HashMap<>();
    expectedSup1Auths.put("a", auth1);
    expectedSup1Auths.put("b", auth2);
    when(supplier1.authForBuild()).thenReturn(RegistryConfigs.create(expectedSup1Auths));

    Map<String, RegistryAuth> expectedSup2Auths = new HashMap<>();
    expectedSup2Auths.put("b", auth3);
    expectedSup2Auths.put("c", auth4);
    when(supplier2.authForBuild()).thenReturn(RegistryConfigs.create(expectedSup2Auths));

    // ensure that supplier1 had priority for server b
    assertThat(multiSupplier.authForBuild().configs(), allOf(
        hasEntry("a", auth1),
        hasEntry("b", auth2),
        hasEntry("c", auth4)
    ));
  }

  @Test
  public void testAuthForBuild_ReturnsNull() throws Exception {

    when(supplier1.authForBuild()).thenReturn(null);

    Map<String, RegistryAuth> expectedAuths = new HashMap<>();
    expectedAuths.put("a", RegistryAuth.builder()
            .username("1")
            .serverAddress("a")
            .build());
    expectedAuths.put("b", RegistryAuth.builder()
            .username("2")
            .serverAddress("b")
            .build());
    
    final RegistryConfigs registryConfigs = RegistryConfigs.create(expectedAuths);
    when(supplier2.authForBuild()).thenReturn(registryConfigs);

    assertThat(multiSupplier.authForBuild(), is(registryConfigs));
  }
}
