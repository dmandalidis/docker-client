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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mandas.docker.client.DockerClient.Signal;
import org.mandas.docker.client.builder.DockerClientBuilder;
import org.mandas.docker.client.builder.DockerClientBuilder.EntityProcessing;
import org.mandas.docker.client.exceptions.ConflictException;
import org.mandas.docker.client.exceptions.DockerCertificateException;
import org.mandas.docker.client.exceptions.DockerException;
import org.mandas.docker.client.exceptions.NodeNotFoundException;
import org.mandas.docker.client.exceptions.NonSwarmNodeException;
import org.mandas.docker.client.exceptions.NotFoundException;
import org.mandas.docker.client.messages.ContainerConfig;
import org.mandas.docker.client.messages.HostConfig;
import org.mandas.docker.client.messages.HostConfig.Bind;
import org.mandas.docker.client.messages.swarm.ConfigSpec;
import org.mandas.docker.client.messages.swarm.NodeSpec;
import org.mandas.docker.client.messages.swarm.SwarmJoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

/**
 * Tests DefaultDockerClient against a {@link okhttp3.mockwebserver.MockWebServer} instance, so
 * we can assert what the HTTP requests look like that DefaultDockerClient sends and test how
 * DefaltDockerClient behaves given certain responses from the Docker Remote API.
 * <p>
 * This test may not be a true "unit test", but using a MockWebServer where we can control the HTTP
 * responses sent by the server and capture the HTTP requests sent by the class-under-test is far
 * simpler that attempting to mock the {@link jakarta.ws.rs.client.Client} instance used by
 * DefaultDockerClient, since the Client has such a rich/fluent interface and many methods/classes
 * that would need to be mocked. Ultimately for testing DefaultDockerClient all we care about is
 * the HTTP requests it sends, rather than what HTTP client library it uses.</p>
 * <p>
 * When adding new functionality to DefaultDockerClient, please consider and prioritize adding unit
 * tests to cover the new functionality in this file rather than integration tests that require a
 * real docker daemon in {@link DefaultDockerClientTest}. While integration tests are valuable,
 * they are more brittle and harder to run than a simple unit test that captures/asserts HTTP
 * requests and responses.</p>
 *
 * @see <a href="https://github.com/square/okhttp/tree/master/mockwebserver">
 * https://github.com/square/okhttp/tree/master/mockwebserver</a>
 */
class DefaultDockerClientUnitTest {

  private final MockWebServer server = new MockWebServer();

  private DockerClientBuilder builder;

  @BeforeEach
  void setup() throws Exception {
    server.start();

    builder = DockerClientBuilder.fromEnv();
    builder.uri(server.url("/").uri());
  }

  @AfterEach
  void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  void testHostForUnixSocket() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("unix:///var/run/docker.sock").build()) {
      assertThat(client.getHost()).isEqualTo("localhost");
    }
  }

  @Test
  void testHostForLocalHttps() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("https://localhost:2375").build()) {
      assertThat(client.getHost()).isEqualTo("localhost");
    }
  }

  @Test
  void testHostForFqdnHttps() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("https://perdu.com:2375").build()) {
      assertThat(client.getHost()).isEqualTo("perdu.com");
    }
  }

  @Test
  void testHostForIpHttps() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("https://192.168.53.103:2375").build()) {
      assertThat(client.getHost()).isEqualTo("192.168.53.103");
    }
  }

  private RecordedRequest takeRequestImmediately() throws InterruptedException {
    return server.takeRequest(1, TimeUnit.MILLISECONDS);
  }

  @Test
  void testCustomHeaders() throws Exception {
    builder.header("int", 1);
    builder.header("string", "2");
    builder.header("list", Arrays.asList("a", "b", "c"));

    server.enqueue(new MockResponse());

    final DefaultDockerClient dockerClient = builder.build();
    dockerClient.info();

    final RecordedRequest recordedRequest = takeRequestImmediately();
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    assertThat(recordedRequest.getPath()).isEqualTo("/info");

    assertThat(recordedRequest.getHeader("int")).isEqualTo("1");
    assertThat(recordedRequest.getHeader("string")).isEqualTo("2");
    // TODO (mbrown): this seems like incorrect behavior - the client should send 3 headers with
    // name "list", not one header with a value of "[a, b, c]"
    assertThat(recordedRequest.getHeaders().values("list")).containsExactly("[a, b, c]");
  }

  private static JsonNode toJson(Buffer buffer) throws IOException {
    return ObjectMapperProvider.objectMapper().readTree(buffer.inputStream());
  }

  private static ObjectNode createObjectNode() {
    return ObjectMapperProvider.objectMapper().createObjectNode();
  }
  
  @Test
  void testGroupAdd() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    final HostConfig hostConfig = HostConfig.builder()
        .groupAdd("63", "65")
        .build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostConfig(hostConfig)
        .build();

    server.enqueue(new MockResponse());

    dockerClient.createContainer(containerConfig);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    final JsonNode groupAdd = toJson(recordedRequest.getBody()).get("HostConfig").get("GroupAdd");
    assertThat(groupAdd.isArray()).isTrue();

    assertThat(childrenTextNodes((ArrayNode) groupAdd)).containsExactlyInAnyOrder("63", "65");
  }

  @Test
  void testCapAddAndDrop() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    final HostConfig hostConfig = HostConfig.builder()
        .capAdd(unmodifiableList(asList("foo", "bar")))
        .capAdd(unmodifiableList(asList("baz", "qux")))
        .build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostConfig(hostConfig)
        .build();

    server.enqueue(new MockResponse());

    dockerClient.createContainer(containerConfig);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    assertThat(recordedRequest.getMethod()).isEqualTo("POST");
    assertThat(recordedRequest.getPath()).isEqualTo("/containers/create");

    assertThat(recordedRequest.getHeader("Content-Type")).isEqualTo("application/json");

    final JsonNode requestJson = toJson(recordedRequest.getBody());
    final JsonNode capAdd = requestJson.get("HostConfig").get("CapAdd");
    assertThat(capAdd.isArray()).isTrue();
    Set<String> capAddValues = childrenTextNodes((ArrayNode) capAdd);
    assertThat(capAddValues).contains("baz", "qux");
    assertThat(capAddValues).hasSize(2);
  }

  private static Set<String> childrenTextNodes(ArrayNode arrayNode) {
    final Set<String> texts = new HashSet<>();
    for (JsonNode child : arrayNode) {
      Preconditions.checkState(child.isTextual(),
          "ArrayNode must only contain text nodes, but found %s in %s",
          child.getNodeType(),
          arrayNode);
      texts.add(child.textValue());
    }
    return texts;
  }

  @Test
  void testNanoCpus() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    final HostConfig hostConfig = HostConfig.builder()
        .nanoCpus(2_000_000_000L)
        .build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostConfig(hostConfig)
        .build();

    server.enqueue(new MockResponse());

    dockerClient.createContainer(containerConfig);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    final JsonNode requestJson = toJson(recordedRequest.getBody());
    final JsonNode nanoCpus = requestJson.get("HostConfig").get("NanoCpus");

    assertThat(hostConfig.nanoCpus()).isEqualTo(nanoCpus.longValue());
  }

  @Test
  void testInspectMissingNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    // build() calls /version to check what format of header to send
    enqueueServerApiVersion("1.28");
    enqueueServerApiEmptyResponse(404);

    assertThatThrownBy(() -> dockerClient.inspectNode("24ifsmvkjbyhk"))
        .isInstanceOf(NodeNotFoundException.class);
  }

  @Test
  void testInspectNonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    // build() calls /version to check what format of header to send
    enqueueServerApiVersion("1.28");
    enqueueServerApiEmptyResponse(503);

    assertThatThrownBy(() -> dockerClient.inspectNode("24ifsmvkjbyhk"))
        .isInstanceOf(NonSwarmNodeException.class);
  }

  @Test
  void testUpdateNodeWithInvalidVersion() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.28");

    final ObjectNode errorMessage = createObjectNode()
        .put("message", "invalid node version: '7'");

    enqueueServerApiResponse(500, errorMessage);

    final NodeSpec nodeSpec = NodeSpec.builder()
        .addLabel("foo", "baz")
        .name("foobar")
        .availability("active")
        .role("manager")
        .build();

    assertThatThrownBy(() -> dockerClient.updateNode("24ifsmvkjbyhk", 7L, nodeSpec))
        .isInstanceOf(DockerException.class);
  }

  @Test
  void testUpdateMissingNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.28");
    enqueueServerApiError(404, "Error updating node: '24ifsmvkjbyhk'");

    final NodeSpec nodeSpec = NodeSpec.builder()
        .addLabel("foo", "baz")
        .name("foobar")
        .availability("active")
        .role("manager")
        .build();

    assertThatThrownBy(() -> dockerClient.updateNode("24ifsmvkjbyhk", 8L, nodeSpec))
        .isInstanceOf(NodeNotFoundException.class);
  }

  @Test
  void testUpdateNonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.28");
    enqueueServerApiError(503, "Error updating node: '24ifsmvkjbyhk'");

    final NodeSpec nodeSpec = NodeSpec.builder()
        .name("foobar")
        .addLabel("foo", "baz")
        .availability("active")
        .role("manager")
        .build();

    assertThatThrownBy(() -> dockerClient.updateNode("24ifsmvkjbyhk", 8L, nodeSpec))
        .isInstanceOf(NonSwarmNodeException.class);
  }

  @Test
  void testJoinSwarm() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.24");
    enqueueServerApiEmptyResponse(200);

    SwarmJoin swarmJoin = SwarmJoin.builder()
            .joinToken("token_foo")
            .listenAddr("0.0.0.0:2377")
            .remoteAddrs(singletonList("10.0.0.10:2377"))
            .build();

    dockerClient.joinSwarm(swarmJoin);
  }

  private void enqueueServerApiError(final int statusCode, final String message) {
    final ObjectNode errorMessage = createObjectNode()
        .put("message", message);

    enqueueServerApiResponse(statusCode, errorMessage);
  }

  @Test
  void testDeleteNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.24");
    enqueueServerApiEmptyResponse(200);

    dockerClient.deleteNode("node-1234");
  }

  @Test
  void testDeleteNode_NodeNotFound() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.24");
    enqueueServerApiEmptyResponse(404);

    assertThatThrownBy(() -> dockerClient.deleteNode("node-1234"))
        .isInstanceOf(NodeNotFoundException.class);
  }

  @Test
  void testDeleteNode_NodeNotPartOfSwarm() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.24");
    enqueueServerApiEmptyResponse(503);

    assertThatThrownBy(() -> dockerClient.deleteNode("node-1234"))
        .isInstanceOf(NonSwarmNodeException.class);
  }

  private void enqueueServerApiEmptyResponse(final int statusCode) {
    server.enqueue(new MockResponse()
        .setResponseCode(statusCode)
        .addHeader("Content-Type", "application/json")
    );
  }

  @Test
  void testCreateConfig_ConflictingName() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(409)
        .addHeader("Content-Type", "application/json")
    );

    final ConfigSpec configSpec = ConfigSpec
        .builder()
        .data(Base64.getEncoder().encodeToString("foobar".getBytes(StandardCharsets.UTF_8)))
        .name("foo.yaml")
        .build();

    assertThatThrownBy(() -> dockerClient.createConfig(configSpec))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void testCreateConfig_NonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(503)
        .addHeader("Content-Type", "application/json")
    );

    final ConfigSpec configSpec = ConfigSpec
        .builder()
        .data(Base64.getEncoder().encodeToString("foobar".getBytes(StandardCharsets.UTF_8)))
        .name("foo.yaml")
        .build();

    assertThatThrownBy(() -> dockerClient.createConfig(configSpec))
        .isInstanceOf(NonSwarmNodeException.class);
  }

  @Test
  void testInspectConfig_NotFound() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(404)
        .addHeader("Content-Type", "application/json")
    );

    assertThatThrownBy(() -> dockerClient.inspectConfig("ktnbjxoalbkvbvedmg1urrz8h"))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void testInspectConfig_NonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(503)
        .addHeader("Content-Type", "application/json")
    );

    assertThatThrownBy(() -> dockerClient.inspectConfig("ktnbjxoalbkvbvedmg1urrz8h"))
        .isInstanceOf(NonSwarmNodeException.class);
  }

  @Test
  void testDeleteConfig() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(204)
        .addHeader("Content-Type", "application/json")
    );

    dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h");
  }

  @Test
  void testDeleteConfig_NotFound() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(404)
        .addHeader("Content-Type", "application/json")
    );

    assertThatThrownBy(() -> dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h"))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void testDeleteConfig_NonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(503)
        .addHeader("Content-Type", "application/json")
    );

    assertThatThrownBy(() -> dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h"))
        .isInstanceOf(NonSwarmNodeException.class);
  }

  @Test
  void testUpdateConfig_NotFound() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(404)
        .addHeader("Content-Type", "application/json")
    );

    final ConfigSpec configSpec = ConfigSpec
        .builder()
        .data(Base64.getEncoder().encodeToString("foobar".getBytes(StandardCharsets.UTF_8)))
        .name("foo.yaml")
        .build();

    assertThatThrownBy(() -> dockerClient.updateConfig("ktnbjxoalbkvbvedmg1urrz8h", 11L, configSpec))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void testUpdateConfig_NonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(503)
        .addHeader("Content-Type", "application/json")
    );

    final ConfigSpec configSpec = ConfigSpec
        .builder()
        .data(Base64.getEncoder().encodeToString("foobar".getBytes(StandardCharsets.UTF_8)))
        .name("foo.yaml")
        .build();

    assertThatThrownBy(() -> dockerClient.updateConfig("ktnbjxoalbkvbvedmg1urrz8h", 11L, configSpec))
        .isInstanceOf(NonSwarmNodeException.class);
  }

  @Test
  void testListNodesWithServerError() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.28");

    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .addHeader("Content-Type", "application/json")
    );

    assertThatThrownBy(() -> dockerClient.listNodes())
        .isInstanceOf(DockerException.class);
  }
  
  @Test
  void testBindBuilderSelinuxLabeling() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    final Bind bindNoSelinuxLabel = HostConfig.Bind.builder()
        .from("noselinux")
        .to("noselinux")
        .build();

    final Bind bindSharedSelinuxContent = HostConfig.Bind.builder()
        .from("shared")
        .to("shared")
        .selinuxLabeling(true)
        .build();

    final Bind bindPrivateSelinuxContent = HostConfig.Bind.builder()
        .from("private")
        .to("private")
        .selinuxLabeling(false)
        .build();

    final HostConfig hostConfig = HostConfig.builder()
        .binds(bindNoSelinuxLabel, bindSharedSelinuxContent, bindPrivateSelinuxContent)
        .build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostConfig(hostConfig)
        .build();

    server.enqueue(new MockResponse());

    dockerClient.createContainer(containerConfig);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    final JsonNode requestJson = toJson(recordedRequest.getBody());

    final JsonNode binds = requestJson.get("HostConfig").get("Binds");

    assertThat(binds.isArray()).isTrue();

    Set<String> bindSet = childrenTextNodes((ArrayNode) binds);
    assertThat(bindSet).hasSize(3);

    assertThat(bindSet).anyMatch(s -> s.contains("noselinux") && !s.contains("z") && !s.contains("Z"));
    assertThat(bindSet).anyMatch(s -> s.contains("shared") && s.contains("z"));
    assertThat(bindSet).anyMatch(s -> s.contains("private") && s.contains("Z"));
  }
  
  @Test
  void testKillContainer() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    server.enqueue(new MockResponse());

    final Signal signal = Signal.SIGHUP;
    dockerClient.killContainer("1234", signal);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    final HttpUrl requestUrl = recordedRequest.getRequestUrl();
    assertThat(requestUrl.queryParameter("signal")).isEqualTo(signal.toString());
  }

  @Test
  void testBufferedRequestEntityProcessing() throws Exception {
    builder.entityProcessing(EntityProcessing.BUFFERED);
    final DefaultDockerClient dockerClient = builder.build();
    
    final HostConfig hostConfig = HostConfig.builder().build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostConfig(hostConfig)
        .build();

    server.enqueue(new MockResponse());

    dockerClient.createContainer(containerConfig);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    assertThat(recordedRequest.getHeader("Content-Length")).isNotNull();
    assertThat(recordedRequest.getHeader("Transfer-Encoding")).isNull();
  }
  
  @Test
  void testChunkedRequestEntityProcessing() throws Exception {
    builder.entityProcessing(EntityProcessing.CHUNKED);
    
    try (final DefaultDockerClient dockerClient = builder.build()) {
    
      final HostConfig hostConfig = HostConfig.builder().build();
  
      final ContainerConfig containerConfig = ContainerConfig.builder()
          .hostConfig(hostConfig)
          .build();
  
      server.enqueue(new MockResponse());
  
      dockerClient.createContainer(containerConfig);
  
      final RecordedRequest recordedRequest = takeRequestImmediately();
  
      assertThat(recordedRequest.getHeader("Content-Length")).isNull();
      assertThat(recordedRequest.getHeader("Transfer-Encoding")).isEqualTo("chunked");
    }
  }

  private void enqueueServerApiResponse(final int statusCode, final ObjectNode objectResponse) {
    server.enqueue(new MockResponse()
        .setResponseCode(statusCode)
        .addHeader("Content-Type", "application/json")
        .setBody(
            objectResponse.toString()
        )
    );
  }

  private void enqueueServerApiVersion(final String apiVersion) {
    enqueueServerApiResponse(200,
        createObjectNode()
            .put("ApiVersion", apiVersion)
            .put("Arch", "foobar")
            .put("GitCommit", "foobar")
            .put("GoVersion", "foobar")
            .put("KernelVersion", "foobar")
            .put("Os", "foobar")
            .put("Version", "1.20")
    );
  }
}
