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

package org.mandas.docker.client;

import static org.assertj.core.api.Assertions.*;
import static org.mandas.docker.client.ImageRef.parseRegistryUrl;

import org.junit.jupiter.api.Test;

class ImageRefTest {

  @Test
  void testImageWithoutTag() {
    final ImageRef sut = new ImageRef("foobar");
    assertThat(sut.getImage()).isEqualTo("foobar");
    assertThat(sut.getTag()).isNull();
  }

  @Test
  void testImageWithTag() {
    final ImageRef sut = new ImageRef("foobar:12345");
    assertThat(sut.getImage()).isEqualTo("foobar");
    assertThat(sut.getTag()).isEqualTo("12345");
  }

  @Test
  void testImageWithTagAndRegistry() {
    final ImageRef sut = new ImageRef("registry:4711/foo/bar:12345");
    assertThat(sut.getImage()).isEqualTo("registry:4711/foo/bar");
    assertThat(sut.getTag()).isEqualTo("12345");
  }

  @Test
  void testImageWithDigest() {
    final ImageRef sut = new ImageRef("bar@sha256:12345");
    assertThat(sut.getImage()).isEqualTo("bar@sha256:12345");
  }

  @Test
  void testImageWithDigestAndRegistry() {
    final ImageRef sut = new ImageRef("registry:4711/foo/bar@sha256:12345");
    assertThat(sut.getImage()).isEqualTo("registry:4711/foo/bar@sha256:12345");
  }

  @Test
  void testRegistry() {
    final String defaultRegistry = "docker.io";
    assertThat(new ImageRef("ubuntu").getRegistryName()).isEqualTo(defaultRegistry);
    assertThat(new ImageRef("library/ubuntu").getRegistryName()).isEqualTo(defaultRegistry);
    assertThat(new ImageRef("docker.io/library/ubuntu").getRegistryName()).isEqualTo(defaultRegistry);
    assertThat(new ImageRef("index.docker.io/library/ubuntu").getRegistryName()).isEqualTo("index.docker.io");
    assertThat(new ImageRef("quay.io/library/ubuntu").getRegistryName()).isEqualTo("quay.io");
    assertThat(new ImageRef("gcr.io/library/ubuntu").getRegistryName()).isEqualTo("gcr.io");
    assertThat(new ImageRef("us.gcr.io/library/ubuntu").getRegistryName()).isEqualTo("us.gcr.io");
    assertThat(new ImageRef("gcr.kubernetes.io/library/ubuntu").getRegistryName()).isEqualTo("gcr.kubernetes.io");

    assertThat(new ImageRef("registry.example.net/foo/bar").getRegistryName())
        .isEqualTo("registry.example.net");

    assertThat(new ImageRef("registry.example.net/foo/bar:1.2.3").getRegistryName())
        .isEqualTo("registry.example.net");

    assertThat(new ImageRef("registry.example.net/foo/bar:latest").getRegistryName())
        .isEqualTo("registry.example.net");

    assertThat(new ImageRef("registry.example.net:5555/foo/bar:latest").getRegistryName())
        .isEqualTo("registry.example.net:5555");
  }

  @Test
  void testRegistryUrl() {
    final String defaultRegistry = "https://index.docker.io/v1/";
    assertThat(new ImageRef("ubuntu").getRegistryUrl()).isEqualTo(defaultRegistry);
    assertThat(new ImageRef("library/ubuntu").getRegistryUrl()).isEqualTo(defaultRegistry);
    assertThat(new ImageRef("docker.io/library/ubuntu").getRegistryUrl()).isEqualTo(defaultRegistry);
    assertThat(new ImageRef("index.docker.io/library/ubuntu").getRegistryUrl()).isEqualTo(defaultRegistry);
    assertThat(new ImageRef("quay.io/library/ubuntu").getRegistryUrl()).isEqualTo("quay.io");
    assertThat(new ImageRef("gcr.io/library/ubuntu").getRegistryUrl()).isEqualTo("https://gcr.io");
    assertThat(new ImageRef("us.gcr.io/library/ubuntu").getRegistryUrl()).isEqualTo("https://us.gcr.io");
    assertThat(new ImageRef("gcr.kubernetes.io/library/ubuntu").getRegistryUrl())
        .isEqualTo("https://gcr.kubernetes.io");

    assertThat(new ImageRef("registry.example.net/foo/bar").getRegistryUrl())
        .isEqualTo("https://registry.example.net");

    assertThat(new ImageRef("registry.example.net/foo/bar:1.2.3").getRegistryUrl())
        .isEqualTo("https://registry.example.net");

    assertThat(new ImageRef("registry.example.net/foo/bar:latest").getRegistryUrl())
        .isEqualTo("https://registry.example.net");

    assertThat(new ImageRef("registry.example.net:5555/foo/bar:latest").getRegistryUrl())
        .isEqualTo("https://registry.example.net:5555");
  }

  @Test
  void testParseUrl() {
    assertThat(parseRegistryUrl("docker.io")).isEqualTo("https://index.docker.io/v1/");
    assertThat(parseRegistryUrl("index.docker.io")).isEqualTo("https://index.docker.io/v1/");
    assertThat(parseRegistryUrl("registry.net")).isEqualTo("https://registry.net");
    assertThat(parseRegistryUrl("registry.net:80")).isEqualTo("https://registry.net:80");
  }
}
