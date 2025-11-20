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

import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonText;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mandas.docker.FixtureUtil.fixture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
public class DefaultDockerClientUnitTest {

  private final MockWebServer server = new MockWebServer();

  private DockerClientBuilder builder;

  @Before
  public void setup() throws Exception {
    server.start();

    builder = DockerClientBuilder.fromEnv();
    builder.uri(server.url("/").uri());
  }

  @After
  public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  public void testHostForUnixSocket() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("unix:///var/run/docker.sock").build()) {
      assertThat(client.getHost(), equalTo("localhost"));
    }
  }

  @Test
  public void testHostForLocalHttps() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("https://localhost:2375").build()) {
      assertThat(client.getHost(), equalTo("localhost"));
    }
  }

  @Test
  public void testHostForFqdnHttps() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("https://perdu.com:2375").build()) {
      assertThat(client.getHost(), equalTo("perdu.com"));
    }
  }

  @Test
  public void testHostForIpHttps() throws DockerCertificateException {
    try (final DefaultDockerClient client = DockerClientBuilder.fromEnv()
        .uri("https://192.168.53.103:2375").build()) {
      assertThat(client.getHost(), equalTo("192.168.53.103"));
    }
  }

  private RecordedRequest takeRequestImmediately() throws InterruptedException {
    return server.takeRequest(1, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testCustomHeaders() throws Exception {
    builder.header("int", 1);
    builder.header("string", "2");
    builder.header("list", Arrays.asList("a", "b", "c"));

    server.enqueue(new MockResponse());

    final DefaultDockerClient dockerClient = builder.build();
    dockerClient.info();

    final RecordedRequest recordedRequest = takeRequestImmediately();
    assertThat(recordedRequest.getMethod(), is("GET"));
    assertThat(recordedRequest.getPath(), is("/info"));

    assertThat(recordedRequest.getHeader("int"), is("1"));
    assertThat(recordedRequest.getHeader("string"), is("2"));
    // TODO (mbrown): this seems like incorrect behavior - the client should send 3 headers with
    // name "list", not one header with a value of "[a, b, c]"
    assertThat(recordedRequest.getHeaders().values("list"), contains("[a, b, c]"));
  }

  private static JsonNode toJson(Buffer buffer) throws IOException {
    return ObjectMapperProvider.objectMapper().readTree(buffer.inputStream());
  }

  private static JsonNode toJson(final String string) throws IOException {
    return ObjectMapperProvider.objectMapper().readTree(string);
  }

  private static JsonNode toJson(byte[] bytes) throws IOException {
    return ObjectMapperProvider.objectMapper().readTree(bytes);
  }

  private static JsonNode toJson(Object object) {
    return ObjectMapperProvider.objectMapper().valueToTree(object);
  }

  private static ObjectNode createObjectNode() {
    return ObjectMapperProvider.objectMapper().createObjectNode();
  }
  
  @Test
  public void testGroupAdd() throws Exception {
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
    assertThat(groupAdd.isArray(), is(true));

    assertThat(childrenTextNodes((ArrayNode) groupAdd), containsInAnyOrder("63", "65"));
  }

  @Test
  public void testCapAddAndDrop() throws Exception {
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

    assertThat(recordedRequest.getMethod(), is("POST"));
    assertThat(recordedRequest.getPath(), is("/containers/create"));

    assertThat(recordedRequest.getHeader("Content-Type"), is("application/json"));

    final JsonNode requestJson = toJson(recordedRequest.getBody());
    assertThat(requestJson, is(jsonObject()
        .where("HostConfig", is(jsonObject()
            .where("CapAdd", is(jsonArray(
                containsInAnyOrder(jsonText("baz"), jsonText("qux")))))))));
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
  public void testNanoCpus() throws Exception {
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

    assertThat(hostConfig.nanoCpus(), is(nanoCpus.longValue()));
  }

  @Test(expected = NodeNotFoundException.class)
  public void testInspectMissingNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    // build() calls /version to check what format of header to send
    enqueueServerApiVersion("1.28");
    enqueueServerApiEmptyResponse(404);

    dockerClient.inspectNode("24ifsmvkjbyhk");
  }

  @Test(expected = NonSwarmNodeException.class)
  public void testInspectNonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    // build() calls /version to check what format of header to send
    enqueueServerApiVersion("1.28");
    enqueueServerApiEmptyResponse(503);

    dockerClient.inspectNode("24ifsmvkjbyhk");
  }

  @Test(expected = DockerException.class)
  public void testUpdateNodeWithInvalidVersion() throws Exception {
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

    dockerClient.updateNode("24ifsmvkjbyhk", 7L, nodeSpec);
  }

  @Test(expected = NodeNotFoundException.class)
  public void testUpdateMissingNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.28");
    enqueueServerApiError(404, "Error updating node: '24ifsmvkjbyhk'");

    final NodeSpec nodeSpec = NodeSpec.builder()
        .addLabel("foo", "baz")
        .name("foobar")
        .availability("active")
        .role("manager")
        .build();

    dockerClient.updateNode("24ifsmvkjbyhk", 8L, nodeSpec);
  }

  @Test(expected = NonSwarmNodeException.class)
  public void testUpdateNonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.28");
    enqueueServerApiError(503, "Error updating node: '24ifsmvkjbyhk'");

    final NodeSpec nodeSpec = NodeSpec.builder()
        .name("foobar")
        .addLabel("foo", "baz")
        .availability("active")
        .role("manager")
        .build();

    dockerClient.updateNode("24ifsmvkjbyhk", 8L, nodeSpec);
  }

  @Test
  public void testJoinSwarm() throws Exception {
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
  public void testDeleteNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.24");
    enqueueServerApiEmptyResponse(200);

    dockerClient.deleteNode("node-1234");
  }

  @Test(expected = NodeNotFoundException.class)
  public void testDeleteNode_NodeNotFound() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.24");
    enqueueServerApiEmptyResponse(404);

    dockerClient.deleteNode("node-1234");
  }

  @Test(expected = NonSwarmNodeException.class)
  public void testDeleteNode_NodeNotPartOfSwarm() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.24");
    enqueueServerApiEmptyResponse(503);

    dockerClient.deleteNode("node-1234");
  }

  private void enqueueServerApiEmptyResponse(final int statusCode) {
    server.enqueue(new MockResponse()
        .setResponseCode(statusCode)
        .addHeader("Content-Type", "application/json")
    );
  }

  @Test(expected = ConflictException.class)
  public void testCreateConfig_ConflictingName() throws Exception {
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

    dockerClient.createConfig(configSpec);
  }

  @Test(expected = NonSwarmNodeException.class)
  public void testCreateConfig_NonSwarmNode() throws Exception {
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

    dockerClient.createConfig(configSpec);
  }

  @Test(expected = NotFoundException.class)
  public void testInspectConfig_NotFound() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(404)
        .addHeader("Content-Type", "application/json")
    );

    dockerClient.inspectConfig("ktnbjxoalbkvbvedmg1urrz8h");
  }

  @Test(expected = NonSwarmNodeException.class)
  public void testInspectConfig_NonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(503)
        .addHeader("Content-Type", "application/json")
    );

    dockerClient.inspectConfig("ktnbjxoalbkvbvedmg1urrz8h");
  }

  @Test
  public void testDeleteConfig() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(204)
        .addHeader("Content-Type", "application/json")
    );

    dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h");
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteConfig_NotFound() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(404)
        .addHeader("Content-Type", "application/json")
    );

    dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h");
  }

  @Test(expected = NonSwarmNodeException.class)
  public void testDeleteConfig_NonSwarmNode() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.30");

    server.enqueue(new MockResponse()
        .setResponseCode(503)
        .addHeader("Content-Type", "application/json")
    );

    dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h");
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateConfig_NotFound() throws Exception {
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

    dockerClient.updateConfig("ktnbjxoalbkvbvedmg1urrz8h", 11L, configSpec);
  }

  @Test(expected = NonSwarmNodeException.class)
  public void testUpdateConfig_NonSwarmNode() throws Exception {
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

    dockerClient.updateConfig("ktnbjxoalbkvbvedmg1urrz8h", 11L, configSpec);
  }

  @Test(expected = DockerException.class)
  public void testListNodesWithServerError() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    enqueueServerApiVersion("1.28");

    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .addHeader("Content-Type", "application/json")
    );

    dockerClient.listNodes();
  }
  
  @Test
  public void testBindBuilderSelinuxLabeling() throws Exception {
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

    assertThat(binds.isArray(), is(true));

    Set<String> bindSet = childrenTextNodes((ArrayNode) binds);
    assertThat(bindSet, hasSize(3));

    assertThat(bindSet, hasItem(allOf(containsString("noselinux"),
        not(containsString("z")), not(containsString("Z")))));

    assertThat(bindSet, hasItem(allOf(containsString("shared"), containsString("z"))));
    assertThat(bindSet, hasItem(allOf(containsString("private"), containsString("Z"))));
  }
  
  @Test
  public void testKillContainer() throws Exception {
    final DefaultDockerClient dockerClient = builder.build();

    server.enqueue(new MockResponse());

    final Signal signal = Signal.SIGHUP;
    dockerClient.killContainer("1234", signal);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    final HttpUrl requestUrl = recordedRequest.getRequestUrl();
    assertThat(requestUrl.queryParameter("signal"), equalTo(signal.toString()));
  }

  @Test
  public void testBufferedRequestEntityProcessing() throws Exception {
    builder.entityProcessing(EntityProcessing.BUFFERED);
    final DefaultDockerClient dockerClient = builder.build();
    
    final HostConfig hostConfig = HostConfig.builder().build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostConfig(hostConfig)
        .build();

    server.enqueue(new MockResponse());

    dockerClient.createContainer(containerConfig);

    final RecordedRequest recordedRequest = takeRequestImmediately();

    assertThat(recordedRequest.getHeader("Content-Length"), notNullValue());
    assertThat(recordedRequest.getHeader("Transfer-Encoding"), nullValue());
  }
  
  @Test
  public void testChunkedRequestEntityProcessing() throws Exception {
    builder.entityProcessing(EntityProcessing.CHUNKED);
    
    try (final DefaultDockerClient dockerClient = builder.build()) {
    
      final HostConfig hostConfig = HostConfig.builder().build();
  
      final ContainerConfig containerConfig = ContainerConfig.builder()
          .hostConfig(hostConfig)
          .build();
  
      server.enqueue(new MockResponse());
  
      dockerClient.createContainer(containerConfig);
  
      final RecordedRequest recordedRequest = takeRequestImmediately();
  
      assertThat(recordedRequest.getHeader("Content-Length"), nullValue());
      assertThat(recordedRequest.getHeader("Transfer-Encoding"), is("chunked"));
    }
  }

  private void enqueueServerApiResponse(final int statusCode, final String fileName)
      throws IOException {
    server.enqueue(new MockResponse()
        .setResponseCode(statusCode)
        .addHeader("Content-Type", "application/json")
        .setBody(
            fixture(fileName)
        )
    );
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
