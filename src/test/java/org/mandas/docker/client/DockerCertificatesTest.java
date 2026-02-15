/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2017 Spotify AB
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
import static org.mockito.Mockito.*;

import com.google.common.io.Resources;
import org.mandas.docker.client.DockerCertificates.SslContextFactory;
import org.mandas.docker.client.exceptions.DockerCertificateException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DockerCertificatesTest {

  private SslContextFactory factory = mock(SslContextFactory.class);
  private ArgumentCaptor<KeyStore> keyStore = ArgumentCaptor.forClass(KeyStore.class);
  private ArgumentCaptor<KeyStore> trustStore = ArgumentCaptor.forClass(KeyStore.class);
  private ArgumentCaptor<char[]> password = ArgumentCaptor.forClass(char[].class);

  @Test
  void testBadDockerCertificates() {
    // try building a DockerCertificates with specifying a cert path to something that
    // isn't a cert
    assertThatThrownBy(() -> DockerCertificates.builder()
        .dockerCertPath(getResourceFile("dockerInvalidSslDirectory"))
        .build())
        .isInstanceOf(DockerCertificateException.class);
  }

  @Test
  void testNoDockerCertificatesInDir() throws Exception {
    final Path certDir = Paths.get(System.getProperty("java.io.tmpdir"));
    final Optional<DockerCertificatesStore> result = DockerCertificates.builder()
        .dockerCertPath(certDir)
        .build();
    assertThat(result.isPresent()).isFalse();
  }

  @Test
  void testDefaultDockerCertificates() throws Exception {
    DockerCertificates.builder()
        .dockerCertPath(getCertPath())
        .sslFactory(factory)
        .build();

    verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());

    final KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getValue()
        .getEntry("key", new KeyStore.PasswordProtection(password.getValue()));

    final KeyStore caKeyStore = trustStore.getValue();

    assertThat(pkEntry).isNotNull();
    assertThat(pkEntry.getCertificate()).isNotNull();
    assertThat(caKeyStore.getCertificate("o=boot2docker")).isNotNull();
  }


  @Test
  void testDockerCertificatesWithMultiCa() throws Exception {
    DockerCertificates.builder()
        .dockerCertPath(getCertPath())
        .caCertPath(getVariant("ca-multi.pem"))
        .sslFactory(factory)
        .build();

    verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());

    final KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getValue()
        .getEntry("key", new KeyStore.PasswordProtection(password.getValue()));

    assertThat(pkEntry).isNotNull();
    assertThat(pkEntry.getCertificate()).isNotNull();
    assertThat(trustStore.getValue().getCertificate(
        "cn=ca-test,o=internet widgits pty ltd,st=some-state,c=cr")).isNotNull();
    assertThat(trustStore.getValue().getCertificate(
        "cn=ca-test-2,o=internet widgits pty ltd,st=some-state,c=cr")).isNotNull();
  }

  @Test
  void testReadPrivateKeyPkcs1() throws Exception {
    DockerCertificates.builder()
        .dockerCertPath(getCertPath())
        .clientKeyPath(getVariant("key-pkcs1.pem"))
        .sslFactory(factory)
        .build();

    verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());

    final KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getValue()
        .getEntry("key", new KeyStore.PasswordProtection(password.getValue()));

    assertThat(pkEntry.getPrivateKey()).isNotNull();
  }

  @Test
  void testReadPrivateKeyPkcs8() throws Exception {
    DockerCertificates.builder()
        .dockerCertPath(getCertPath())
        .clientKeyPath(getVariant("key-pkcs8.pem"))
        .sslFactory(factory)
        .build();

    verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());

    final KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getValue()
        .getEntry("key", new KeyStore.PasswordProtection(password.getValue()));

    assertThat(pkEntry.getPrivateKey()).isNotNull();
  }

  @Test
  void testReadEllipticCurvePrivateKey() throws Exception {
        DockerCertificates.builder()
        .dockerCertPath(getResourceFile("dockerSslDirectoryWithEcKey"))
        .sslFactory(factory)
        .build();

    verify(factory).newSslContext(keyStore.capture(), password.capture(), trustStore.capture());

    final KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getValue()
            .getEntry("key", new KeyStore.PasswordProtection(password.getValue()));

    assertThat(pkEntry.getPrivateKey()).isNotNull();
  }

  private Path getResourceFile(final String path) throws URISyntaxException {
    return Paths.get(Resources.getResource(path).toURI());
  }

  private Path getCertPath() throws URISyntaxException {
    return getResourceFile("dockerSslDirectory");
  }

  private Path getVariant(final String filename) throws URISyntaxException {
    return getResourceFile("dockerSslVariants").resolve(filename);
  }
}
