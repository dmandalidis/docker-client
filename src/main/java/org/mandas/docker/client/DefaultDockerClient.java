/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * Copyright (c) 2014 Oleg Poleshuk
 * Copyright (c) 2014 CyDesign Ltd
 * Copyright (c) 2016 ThoughtWorks, Inc
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

package org.mandas.docker.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.mandas.docker.client.ObjectMapperProvider.objectMapper;
import static org.mandas.docker.client.VersionCompare.compareVersion;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import org.mandas.docker.client.auth.RegistryAuthSupplier;
import org.mandas.docker.client.exceptions.BadParamException;
import org.mandas.docker.client.exceptions.ConflictException;
import org.mandas.docker.client.exceptions.ContainerNotFoundException;
import org.mandas.docker.client.exceptions.ContainerRenameConflictException;
import org.mandas.docker.client.exceptions.DockerException;
import org.mandas.docker.client.exceptions.DockerRequestException;
import org.mandas.docker.client.exceptions.DockerTimeoutException;
import org.mandas.docker.client.exceptions.ExecCreateConflictException;
import org.mandas.docker.client.exceptions.ExecNotFoundException;
import org.mandas.docker.client.exceptions.ExecStartConflictException;
import org.mandas.docker.client.exceptions.ImageNotFoundException;
import org.mandas.docker.client.exceptions.NetworkNotFoundException;
import org.mandas.docker.client.exceptions.NodeNotFoundException;
import org.mandas.docker.client.exceptions.NonSwarmNodeException;
import org.mandas.docker.client.exceptions.NotFoundException;
import org.mandas.docker.client.exceptions.PermissionException;
import org.mandas.docker.client.exceptions.ServiceNotFoundException;
import org.mandas.docker.client.exceptions.TaskNotFoundException;
import org.mandas.docker.client.exceptions.UnsupportedApiVersionException;
import org.mandas.docker.client.exceptions.VolumeNotFoundException;
import org.mandas.docker.client.messages.Container;
import org.mandas.docker.client.messages.ContainerChange;
import org.mandas.docker.client.messages.ContainerConfig;
import org.mandas.docker.client.messages.ContainerCreation;
import org.mandas.docker.client.messages.ContainerExit;
import org.mandas.docker.client.messages.ContainerInfo;
import org.mandas.docker.client.messages.ContainerStats;
import org.mandas.docker.client.messages.ContainerUpdate;
import org.mandas.docker.client.messages.Distribution;
import org.mandas.docker.client.messages.ExecCreation;
import org.mandas.docker.client.messages.ExecState;
import org.mandas.docker.client.messages.HostConfig;
import org.mandas.docker.client.messages.Image;
import org.mandas.docker.client.messages.ImageHistory;
import org.mandas.docker.client.messages.ImageInfo;
import org.mandas.docker.client.messages.ImageSearchResult;
import org.mandas.docker.client.messages.Info;
import org.mandas.docker.client.messages.Network;
import org.mandas.docker.client.messages.NetworkConfig;
import org.mandas.docker.client.messages.NetworkConnection;
import org.mandas.docker.client.messages.NetworkCreation;
import org.mandas.docker.client.messages.ProgressMessage;
import org.mandas.docker.client.messages.RegistryAuth;
import org.mandas.docker.client.messages.RegistryConfigs;
import org.mandas.docker.client.messages.RemovedImage;
import org.mandas.docker.client.messages.ServiceCreateResponse;
import org.mandas.docker.client.messages.TopResults;
import org.mandas.docker.client.messages.Version;
import org.mandas.docker.client.messages.Volume;
import org.mandas.docker.client.messages.VolumeList;
import org.mandas.docker.client.messages.swarm.Config;
import org.mandas.docker.client.messages.swarm.ConfigCreateResponse;
import org.mandas.docker.client.messages.swarm.ConfigSpec;
import org.mandas.docker.client.messages.swarm.Node;
import org.mandas.docker.client.messages.swarm.NodeInfo;
import org.mandas.docker.client.messages.swarm.NodeSpec;
import org.mandas.docker.client.messages.swarm.Secret;
import org.mandas.docker.client.messages.swarm.SecretCreateResponse;
import org.mandas.docker.client.messages.swarm.SecretSpec;
import org.mandas.docker.client.messages.swarm.Service;
import org.mandas.docker.client.messages.swarm.ServiceSpec;
import org.mandas.docker.client.messages.swarm.Swarm;
import org.mandas.docker.client.messages.swarm.SwarmInit;
import org.mandas.docker.client.messages.swarm.SwarmJoin;
import org.mandas.docker.client.messages.swarm.SwarmSpec;
import org.mandas.docker.client.messages.swarm.Task;
import org.mandas.docker.client.messages.swarm.UnlockKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;

public class DefaultDockerClient implements DockerClient, Closeable {

  @Override
  public URI uri() {
    return uri;
  }
  
  /**
   * Hack: this {@link ProgressHandler} is meant to capture the image ID (or image digest in Docker
   * 1.10+) of an image being created. Weirdly enough, Docker returns the ID or digest of a newly
   * created image in the status of a progress message.
   *
   * <p>The image ID/digest is required to tag the just loaded image since,
   * also weirdly enough, the pull operation with the <code>fromSrc</code> parameter does not
   * support the <code>tag</code> parameter. By retrieving the ID/digest, the image can be tagged
   * with its image name, given its ID/digest.
   */
  private static class CreateProgressHandler implements ProgressHandler {

    // The length of the image hash
    private static final int EXPECTED_CHARACTER_NUM1 = 64;
    // The length of the image digest
    private static final int EXPECTED_CHARACTER_NUM2 = 71;

    private final ProgressHandler delegate;

    private String imageId;

    private CreateProgressHandler(ProgressHandler delegate) {
      this.delegate = delegate;
    }

    private String getImageId() {
      if (imageId == null) {
         throw new IllegalStateException("Could not acquire image ID or digest following create");
      }
      return imageId;
    }

    @Override
    public void progress(ProgressMessage message) throws DockerException {
      delegate.progress(message);
      final String status = message.status();
      if (status != null && (status.length() == EXPECTED_CHARACTER_NUM1
                             || status.length() == EXPECTED_CHARACTER_NUM2)) {
        imageId = message.status();
      }
    }

  }

  /**
   * Hack: this {@link ProgressHandler} is meant to capture the image names
   * of an image being loaded. Weirdly enough, Docker returns the name of a newly
   * created image in the stream of a progress message.
   *
   */
  private static class LoadProgressHandler implements ProgressHandler {

    // The length of the image hash
    private static final Pattern IMAGE_STREAM_PATTERN =
        Pattern.compile("Loaded image: (?<image>.+)\n");

    private final ProgressHandler delegate;

    private Set<String> imageNames;

    private LoadProgressHandler(ProgressHandler delegate) {
      this.delegate = delegate;
      this.imageNames = new HashSet<>();
    }

    private Set<String> getImageNames() {
      return unmodifiableSet(new HashSet<>(imageNames));
    }

    @Override
    public void progress(ProgressMessage message) throws DockerException {
      delegate.progress(message);
      final String stream = message.stream();
      if (stream != null) {
        Matcher streamMatcher = IMAGE_STREAM_PATTERN.matcher(stream);
        if (streamMatcher.matches()) {
          imageNames.add(streamMatcher.group("image"));
        }

      }
    }

  }

  
  /**
   * Hack: this {@link ProgressHandler} is meant to capture the image ID
   * of an image being built.
   */
  private static class BuildProgressHandler implements ProgressHandler {

    private final ProgressHandler delegate;

    private String imageId;

    private BuildProgressHandler(ProgressHandler delegate) {
      this.delegate = delegate;
    }

    private String getImageId() {
	  if (imageId == null) {
        throw new IllegalStateException("Could not acquire image ID or digest following build");
      }
      return imageId;
    }

    @Override
    public void progress(ProgressMessage message) throws DockerException {
      delegate.progress(message);
      
      final String id = message.buildImageId();
      if (id != null) {
        imageId = id;
      }
    }

  }
  // ==========================================================================

  private static final Logger log = LoggerFactory.getLogger(DefaultDockerClient.class);

  private static final Pattern CONTAINER_NAME_PATTERN =
          Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_.-]+$");

  private static final GenericType<List<Container>> CONTAINER_LIST =
      new GenericType<List<Container>>() {
      };

  private static final GenericType<List<ContainerChange>> CONTAINER_CHANGE_LIST =
      new GenericType<List<ContainerChange>>() {
      };

  private static final GenericType<List<Image>> IMAGE_LIST =
      new GenericType<List<Image>>() {
      };

  private static final GenericType<List<Network>> NETWORK_LIST =
      new GenericType<List<Network>>() {
      };

  private static final GenericType<List<ImageSearchResult>> IMAGES_SEARCH_RESULT_LIST =
      new GenericType<List<ImageSearchResult>>() {
      };

  private static final GenericType<List<RemovedImage>> REMOVED_IMAGE_LIST =
      new GenericType<List<RemovedImage>>() {
      };

  private static final GenericType<List<ImageHistory>> IMAGE_HISTORY_LIST =
      new GenericType<List<ImageHistory>>() {
      };

  private static final GenericType<List<Service>> SERVICE_LIST =
      new GenericType<List<Service>>() {
      };

  private  static final GenericType<Distribution> DISTRIBUTION =
      new GenericType<Distribution>(){
      };

  private static final GenericType<List<Task>> TASK_LIST = new GenericType<List<Task>>() { };

  private static final GenericType<List<Node>> NODE_LIST = new GenericType<List<Node>>() { };

  private static final GenericType<List<Config>> CONFIG_LIST = new GenericType<List<Config>>() { };

  private static final GenericType<List<Secret>> SECRET_LIST = new GenericType<List<Secret>>() { };

  private final Client client;

  private final URI uri;
  private final String apiVersion;
  private final RegistryAuthSupplier registryAuthSupplier;

  private final Map<String, Object> headers;

  Client getClient() {
    return client;
  }

  DefaultDockerClient(final URI uri, final String apiVersion, Client client, RegistryAuthSupplier registryAuthSupplier, Map<String, Object> customHeaders) {
    this.uri = uri;
    this.apiVersion = apiVersion;
    this.client = client;
    this.registryAuthSupplier = registryAuthSupplier;
    this.headers = customHeaders;
  }
  
  @Override
  public String getHost() {
    return ofNullable(uri.getHost()).orElse("localhost");
  }

  @Override
  public void close() {
    client.close();
  }

  @Override
  public String ping() throws DockerException, InterruptedException {
    final WebTarget resource = client.target(uri).path("_ping");
    return request(GET, String.class, resource, resource.request());
  }

  @Override
  public Version version() throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("version");
    return request(GET, Version.class, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public int auth(final RegistryAuth registryAuth) throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("auth");
    final Response response =
        request(POST, Response.class, resource, resource.request(APPLICATION_JSON_TYPE),
                Entity.json(registryAuth));
    return response.getStatus();
  }

  @Override
  public Info info() throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("info");
    return request(GET, Info.class, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public List<Container> listContainers(final ListContainersParam... params)
      throws DockerException, InterruptedException {
    WebTarget resource = resource()
        .path("containers").path("json");
    resource = addParameters(resource, params);

    try {
      return request(GET, CONTAINER_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new BadParamException(getQueryParamMap(resource), e);
        default:
          throw e;
      }
    }
  }

  private WebTarget addParameters(WebTarget resource, final Param... params)
      throws DockerException {
    final Map<String, List<String>> filters = new HashMap<>();
    for (final Param param : params) {
      if (param instanceof FilterParam) {
        List<String> filterValueList;
        if (filters.containsKey(param.name())) {
          filterValueList = filters.get(param.name());
        } else {
          filterValueList = new ArrayList<>();
        }
        filterValueList.add(param.value());
        filters.put(param.name(), filterValueList);
      } else {
        resource = resource.queryParam(urlEncode(param.name()), urlEncode(param.value()));
      }
    }

    if (!filters.isEmpty()) {
      // If filters were specified, we must put them in a JSON object and pass them using the
      // 'filters' query param like this: filters={"dangling":["true"]}. If filters is an empty map,
      // urlEncodeFilters will return null and queryParam() will remove that query parameter.
      resource = resource.queryParam("filters", urlEncodeFilters(filters));
    }
    return resource;
  }

  private Map<String, String> getQueryParamMap(final WebTarget resource) {
    final String queryParams = resource.getUri().getQuery();
    final Map<String, String> paramsMap = new HashMap<>();
    if (queryParams != null) {
      for (final String queryParam : queryParams.split("&")) {
        final String[] kv = queryParam.split("=");
        paramsMap.put(kv[0], kv[1]);
      }
    }
    return paramsMap;
  }

  /**
   * URL-encodes a string when used as a URL query parameter's value.
   *
   * @param unencoded A string that may contain characters not allowed in URL query parameters.
   * @return URL-encoded String
   * @throws DockerException if there's an UnsupportedEncodingException
   */
  private String urlEncode(final String unencoded) throws DockerException {
    try {
      final String encode = URLEncoder.encode(unencoded, UTF_8.name());
      return encode.replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      throw new DockerException(e);
    }
  }

  /**
   * Takes a map of filters and URL-encodes them. If the map is empty or an exception occurs, return
   * null.
   *
   * @param filters A map of filters.
   * @return String
   * @throws DockerException if there's an IOException
   */
  private String urlEncodeFilters(final Map<String, List<String>> filters) throws DockerException {
    try {
      final String unencodedFilters = objectMapper().writeValueAsString(filters);
      if (!unencodedFilters.isEmpty()) {
        return urlEncode(unencodedFilters);
      }
    } catch (IOException e) {
      throw new DockerException(e);
    }
    return null;
  }

  @Override
  public List<Image> listImages(final ListImagesParam... params)
      throws DockerException, InterruptedException {
    WebTarget resource = resource()
        .path("images").path("json");
    resource = addParameters(resource, params);
    return request(GET, IMAGE_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public ContainerCreation createContainer(final ContainerConfig config)
      throws DockerException, InterruptedException {
    return createContainer(config, null);
  }

  @Override
  public ContainerCreation createContainer(final ContainerConfig config, final String name)
      throws DockerException, InterruptedException {
    WebTarget resource = resource()
        .path("containers").path("create");

    if (name != null) {
      if (!CONTAINER_NAME_PATTERN.matcher(name).matches()) {
    	 throw new IllegalArgumentException(String.format("Invalid container name: \"%s\"", name));
      }
      resource = resource.queryParam("name", name);
    }

    log.debug("Creating container with ContainerConfig: {}", config);

    try {
      return request(POST, ContainerCreation.class, resource, resource
          .request(APPLICATION_JSON_TYPE), Entity.json(config));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ImageNotFoundException(config.image(), e);
        case 406:
          throw new DockerException("Impossible to attach. Container not running.", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void startContainer(final String containerId)
      throws DockerException, InterruptedException {
    requireNonNull(containerId, "containerId");

    log.info("Starting container with Id: {}", containerId);

    containerAction(containerId, "start");
  }

  private void containerAction(final String containerId, final String action)
      throws DockerException, InterruptedException {
    containerAction(containerId, action, new MultivaluedHashMap<String, String>());
  }

  private void containerAction(final String containerId, final String action,
                               final MultivaluedMap<String, String> queryParameters)
          throws DockerException, InterruptedException {
    try {
      WebTarget resource = resource()
              .path("containers").path(containerId).path(action);

      for (Map.Entry<String, List<String>> queryParameter : queryParameters.entrySet()) {
        for (String parameterValue : queryParameter.getValue()) {
          resource = resource.queryParam(queryParameter.getKey(), parameterValue);
        }
      }
      request(POST, resource, resource.request());
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void pauseContainer(final String containerId)
      throws DockerException, InterruptedException {
    requireNonNull(containerId, "containerId");
    containerAction(containerId, "pause");
  }

  @Override
  public void unpauseContainer(final String containerId)
      throws DockerException, InterruptedException {
	requireNonNull(containerId, "containerId");
    containerAction(containerId, "unpause");
  }

  @Override
  public void restartContainer(String containerId) throws DockerException, InterruptedException {
    restartContainer(containerId, 10);
  }

  @Override
  public void restartContainer(String containerId, int secondsToWaitBeforeRestart)
      throws DockerException, InterruptedException {
	requireNonNull(containerId, "containerId");
	requireNonNull(secondsToWaitBeforeRestart, "secondsToWait");

    MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
    queryParameters.add("t", String.valueOf(secondsToWaitBeforeRestart));

    containerAction(containerId, "restart", queryParameters);
  }

  @Override
  public void killContainer(final String containerId) throws DockerException, InterruptedException {
	requireNonNull(containerId, "containerId");
    containerAction(containerId, "kill");
  }

  @Override
  public void killContainer(final String containerId, final Signal signal)
      throws DockerException, InterruptedException {
	requireNonNull(containerId, "containerId");

    MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
    queryParameters.add("signal", signal.getName());

    containerAction(containerId, "kill", queryParameters);
  }

  @Override
  public Distribution getDistribution(String imageName)
      throws DockerException, InterruptedException {
	requireNonNull(imageName, "containerName");
    final WebTarget resource = resource().path("distribution").path(imageName).path("json");
    return request(GET, DISTRIBUTION, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public void stopContainer(final String containerId, final int secondsToWaitBeforeKilling)
      throws DockerException, InterruptedException {
    try {
      final WebTarget resource = resource()
          .path("containers").path(containerId).path("stop")
          .queryParam("t", String.valueOf(secondsToWaitBeforeKilling));
      request(POST, resource, resource.request());
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 304: // already stopped, so we're cool
          return;
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ContainerExit waitContainer(final String containerId)
      throws DockerException, InterruptedException {
    try {
      final WebTarget resource = resource()
          .path("containers").path(containerId).path("wait");
      // Wait forever
      return request(POST, ContainerExit.class, resource,
                     resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void removeContainer(final String containerId)
      throws DockerException, InterruptedException {
    removeContainer(containerId, new RemoveContainerParam[0]);
  }

  @Override
  public void removeContainer(final String containerId, final RemoveContainerParam... params)
      throws DockerException, InterruptedException {
    try {
      WebTarget resource = resource().path("containers").path(containerId);

      for (final RemoveContainerParam param : params) {
        resource = resource.queryParam(param.name(), param.value());
      }

      request(DELETE, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new BadParamException(getQueryParamMap(resource()), e);
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public InputStream exportContainer(String containerId)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource()
        .path("containers").path(containerId).path("export");
    try {
      return request(GET, InputStream.class, resource,
                     resource.request(APPLICATION_OCTET_STREAM_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public InputStream archiveContainer(String containerId, String path)
      throws DockerException, InterruptedException {
    final String apiVersion = version().apiVersion();
    final int versionComparison = compareVersion(apiVersion, "1.20");

    // Version below 1.20
    if (versionComparison < 0) {
      throw new UnsupportedApiVersionException(apiVersion);
    }

    final WebTarget resource = resource()
        .path("containers").path(containerId).path("archive")
        .queryParam("path", path);

    try {
      return request(GET, InputStream.class, resource,
                     resource.request(APPLICATION_OCTET_STREAM_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public TopResults topContainer(final String containerId)
      throws DockerException, InterruptedException {
    return topContainer(containerId, null);
  }

  @Override
  public TopResults topContainer(final String containerId, final String psArgs)
      throws DockerException, InterruptedException {
    try {
      WebTarget resource = resource().path("containers").path(containerId).path("top");
      if (psArgs != null && !"".equals(psArgs.trim())) {
        resource = resource.queryParam("ps_args", psArgs);
      }
      return request(GET, TopResults.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void copyToContainer(final Path directory, String containerId, String path)
      throws DockerException, InterruptedException, IOException {
    try (final CompressedDirectory compressedDirectory = CompressedDirectory.create(directory);
         final InputStream fileStream = Files.newInputStream(compressedDirectory.file())) {
      copyToContainer(fileStream, containerId, path);
    }
  }

  @Override
  public void copyToContainer(InputStream tarStream, String containerId, String path)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource()
        .path("containers")
        .path(containerId)
        .path("archive")
        .queryParam("noOverwriteDirNonDir", true)
        .queryParam("path", path);

    try {
      request(PUT, String.class, resource,
              resource.request(APPLICATION_OCTET_STREAM_TYPE),
              Entity.entity(tarStream, "application/tar"));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new BadParamException(getQueryParamMap(resource), e);
        case 403:
          throw new PermissionException("Volume or container rootfs is marked as read-only.", e);
        case 404:
          throw new NotFoundException(
              String.format("Either container %s or path %s not found.", containerId, path), e);
        default:
          throw e;
      }
    }
  }

  @Override
  public List<ContainerChange> inspectContainerChanges(final String containerId)
      throws DockerException, InterruptedException {
    try {
      final WebTarget resource = resource().path("containers").path(containerId).path("changes");
      return request(GET, CONTAINER_CHANGE_LIST, resource,
                     resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ContainerInfo inspectContainer(final String containerId)
      throws DockerException, InterruptedException {
    try {
      final WebTarget resource = resource().path("containers").path(containerId).path("json");
      return request(GET, ContainerInfo.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ContainerCreation commitContainer(final String containerId,
                                           final String repo,
                                           final String tag,
                                           final ContainerConfig config,
                                           final String comment,
                                           final String author)
      throws DockerException, InterruptedException {

	requireNonNull(containerId, "containerId");
	requireNonNull(repo, "repo");
	requireNonNull(config, "containerConfig");

    WebTarget resource = resource()
        .path("commit")
        .queryParam("container", containerId)
        .queryParam("repo", repo);

    if (author != null && !"".equals(author.trim())) {
      resource = resource.queryParam("author", author);
    }
    if (comment != null && !"".equals(comment.trim())) {
      resource = resource.queryParam("comment", comment);
    }
    if (tag != null && !"".equals(tag.trim())) {
      resource = resource.queryParam("tag", tag);
    }

    log.debug("Committing container id: {} to repository: {} with ContainerConfig: {}", containerId,
             repo, config);

    try {
      return request(POST, ContainerCreation.class, resource, resource
          .request(APPLICATION_JSON_TYPE), Entity.json(config));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void renameContainer(final String containerId, final String name)
      throws DockerException, InterruptedException {
    WebTarget resource = resource()
        .path("containers").path(containerId).path("rename");

    if (name == null) {
      throw new IllegalArgumentException("Cannot rename container to null");
    }

    if (!CONTAINER_NAME_PATTERN.matcher(name).matches()) {
    	throw new IllegalArgumentException(String.format("Invalid container name: \"%s\"", name));
    }
    resource = resource.queryParam("name", name);

    log.info("Renaming container with id {}. New name {}.", containerId, name);

    try {
      request(POST, resource, resource.request());
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        case 409:
          throw new ContainerRenameConflictException(containerId, name, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ContainerUpdate updateContainer(final String containerId, final HostConfig config)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.22");
    try {
      WebTarget resource = resource().path("containers").path(containerId).path("update");
      return request(POST, ContainerUpdate.class, resource, resource.request(APPLICATION_JSON_TYPE),
              Entity.json(config));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId);
        default:
          throw e;
      }
    }
  }

  @Override
  public List<ImageSearchResult> searchImages(final String term)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("images").path("search").queryParam("term", term);
    return request(GET, IMAGES_SEARCH_RESULT_LIST, resource,
                   resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public Set<String> load(final InputStream imagePayload)
      throws DockerException, InterruptedException {
    return load(imagePayload, new LoggingLoadHandler());
  }

  @Override
  public Set<String> load(final InputStream imagePayload, final ProgressHandler handler)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource()
            .path("images")
            .path("load")
            .queryParam("quiet", "false");

    final LoadProgressHandler loadProgressHandler = new LoadProgressHandler(handler);
    final Entity<InputStream> entity = Entity.entity(imagePayload, APPLICATION_OCTET_STREAM);

    try (final ProgressStream load =
            request(POST, ProgressStream.class, resource,
                    resource.request(APPLICATION_JSON_TYPE), entity)) {
      load.tail(loadProgressHandler, POST, resource.getUri());
      return loadProgressHandler.getImageNames();
    } catch (IOException e) {
      throw new DockerException(e);
    } finally {
      IOUtils.closeQuietly(imagePayload);
    }
  }

  @Override
  public void create(final String image, final InputStream imagePayload)
      throws DockerException, InterruptedException {
    create(image, imagePayload, new LoggingPullHandler("image stream"));
  }

  @Override
  public void create(final String image, final InputStream imagePayload,
                     final ProgressHandler handler)
      throws DockerException, InterruptedException {
    WebTarget resource = resource().path("images").path("create");

    resource = resource
        .queryParam("fromSrc", "-")
        .queryParam("tag", image);

    final CreateProgressHandler createProgressHandler = new CreateProgressHandler(handler);
    final Entity<InputStream> entity = Entity.entity(imagePayload,
                                                     APPLICATION_OCTET_STREAM);
    try {
      requestAndTail(POST, createProgressHandler, resource,
              resource.request(APPLICATION_JSON_TYPE), entity);
      tag(createProgressHandler.getImageId(), image, true);
    } finally {
      IOUtils.closeQuietly(imagePayload);
    }
  }

  @Override
  public InputStream save(final String... images)
      throws DockerException, IOException, InterruptedException {
    WebTarget resource;
    if (images.length == 1) {
      resource = resource().path("images").path(images[0]).path("get");
    } else {
      resource = resource().path("images").path("get");
      if (images.length > 1) {
        for (final String image : images) {
          if (image != null && !"".equals(image.trim())) {
            resource = resource.queryParam("names", urlEncode(image));
          }
        }
      }
    }

    return request(
        GET,
        InputStream.class,
        resource,
        resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public InputStream saveMultiple(final String... images)
      throws DockerException, IOException, InterruptedException {

    final WebTarget resource = resource().path("images").path("get");
    for (final String image : images) {
      resource.queryParam("names", urlEncode(image));
    }

    return request(
        GET,
        InputStream.class,
        resource,
        resource.request(APPLICATION_JSON_TYPE).header("X-Registry-Auth", authHeader(
            registryAuthSupplier.authFor(images[0])))
    );
  }

  @Override
  public void pull(final String image) throws DockerException, InterruptedException {
    pull(image, new LoggingPullHandler(image));
  }

  @Override
  public void pull(final String image, final ProgressHandler handler)
      throws DockerException, InterruptedException {
    pull(image, registryAuthSupplier.authFor(image), handler);
  }

  @Override
  public void pull(final String image, final RegistryAuth registryAuth)
      throws DockerException, InterruptedException {
    pull(image, registryAuth, new LoggingPullHandler(image));
  }

  @Override
  public void pull(final String image, final RegistryAuth registryAuth,
                   final ProgressHandler handler)
      throws DockerException, InterruptedException {
    final ImageRef imageRef = new ImageRef(image);

    WebTarget resource = resource().path("images").path("create");

    resource = resource.queryParam("fromImage", imageRef.getImage());
    if (imageRef.getTag() != null) {
      resource = resource.queryParam("tag", imageRef.getTag());
    }

    try {
      requestAndTail(POST, handler, resource,
              resource
                  .request(APPLICATION_JSON_TYPE)
                  .header("X-Registry-Auth", authHeader(registryAuth)));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ImageNotFoundException(image, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void push(final String image) throws DockerException, InterruptedException {
    push(image, new LoggingPushHandler(image));
  }

  @Override
  public void push(final String image, final RegistryAuth registryAuth)
      throws DockerException, InterruptedException {
    push(image, new LoggingPushHandler(image), registryAuth);
  }

  @Override
  public void push(final String image, final ProgressHandler handler)
      throws DockerException, InterruptedException {
    push(image, handler, registryAuthSupplier.authFor(image));
  }

  @Override
  public void push(final String image,
                   final ProgressHandler handler,
                   final RegistryAuth registryAuth)
      throws DockerException, InterruptedException {
    final ImageRef imageRef = new ImageRef(image);

    WebTarget resource = resource().path("images").path(imageRef.getImage()).path("push");

    if (imageRef.getTag() != null) {
      resource = resource.queryParam("tag", imageRef.getTag());
    }

    try {
      requestAndTail(POST, handler, resource,
              resource.request(APPLICATION_JSON_TYPE)
                  .header("X-Registry-Auth", authHeader(registryAuth)));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ImageNotFoundException(image, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void tag(final String image, final String name)
      throws DockerException, InterruptedException {
    tag(image, name, false);
  }

  @Override
  public void tag(final String image, final String name, final boolean force)
      throws DockerException, InterruptedException {
    final ImageRef imageRef = new ImageRef(name);

    WebTarget resource = resource().path("images").path(image).path("tag");

    resource = resource.queryParam("repo", imageRef.getImage());
    if (imageRef.getTag() != null) {
      resource = resource.queryParam("tag", imageRef.getTag());
    }

    if (force) {
      resource = resource.queryParam("force", true);
    }

    try {
      request(POST, resource, resource.request());
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new BadParamException(getQueryParamMap(resource), e);
        case 404:
          throw new ImageNotFoundException(image, e);
        case 409:
          throw new ConflictException(e);
        default:
          throw e;
      }
    }
  }

  @Override
  public String build(final Path directory, final BuildParam... params)
      throws DockerException, InterruptedException, IOException {
    return build(directory, null, new LoggingBuildHandler(), params);
  }

  @Override
  public String build(final Path directory, final String name, final BuildParam... params)
      throws DockerException, InterruptedException, IOException {
    return build(directory, name, new LoggingBuildHandler(), params);
  }

  @Override
  public String build(final Path directory, final ProgressHandler handler,
                      final BuildParam... params)
      throws DockerException, InterruptedException, IOException {
    return build(directory, null, handler, params);
  }

  @Override
  public String build(final Path directory, final String name, final ProgressHandler handler,
                      final BuildParam... params)
      throws DockerException, InterruptedException, IOException {
    return build(directory, name, null, handler, params);
  }

  @Override
  public String build(final Path directory, final String name, final String dockerfile,
                      final ProgressHandler handler, final BuildParam... params)
      throws DockerException, InterruptedException, IOException {
	requireNonNull(handler, "handler");

    WebTarget resource = resource().path("build");

    for (final BuildParam param : params) {
      resource = resource.queryParam(param.name(), param.value());
    }
    if (name != null) {
      resource = resource.queryParam("t", name);
    }
    if (dockerfile != null) {
      resource = resource.queryParam("dockerfile", dockerfile);
    }

    // Convert auth to X-Registry-Config format
    final RegistryConfigs registryConfigs = registryAuthSupplier.authForBuild();

    final BuildProgressHandler buildHandler = new BuildProgressHandler(handler);

    try (final CompressedDirectory compressedDirectory = CompressedDirectory.create(directory);
         final InputStream fileStream = Files.newInputStream(compressedDirectory.file())) {
        
      requestAndTail(POST, buildHandler, resource,
                     resource.request(APPLICATION_JSON_TYPE)
                         .header("X-Registry-Config",
                                 authRegistryHeader(registryConfigs)),
                     Entity.entity(fileStream, "application/tar"));

      return buildHandler.getImageId();
    }
  }

  @Override
  public ImageInfo inspectImage(final String image) throws DockerException, InterruptedException {
    try {
      final WebTarget resource = resource().path("images").path(image).path("json");
      return request(GET, ImageInfo.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ImageNotFoundException(image, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public List<RemovedImage> removeImage(String image)
      throws DockerException, InterruptedException {
    return removeImage(image, false, false);
  }

  @Override
  public List<RemovedImage> removeImage(String image, boolean force, boolean noPrune)
      throws DockerException, InterruptedException {
    try {
      final WebTarget resource = resource().path("images").path(image)
          .queryParam("force", String.valueOf(force))
          .queryParam("noprune", String.valueOf(noPrune));
      return request(DELETE, REMOVED_IMAGE_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ImageNotFoundException(image, e);
        case 409:
          throw new ConflictException(e);
        default:
          throw e;
      }
    }
  }

  @Override
  public List<ImageHistory> history(final String image)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource()
        .path("images")
        .path(image)
        .path("history");
    try {
      return request(GET, IMAGE_HISTORY_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ImageNotFoundException(image, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public LogStream logs(final String containerId, final LogsParam... params)
      throws DockerException, InterruptedException {
    WebTarget resource = resource()
        .path("containers").path(containerId)
        .path("logs");

    for (final LogsParam param : params) {
      resource = resource.queryParam(param.name(), param.value());
    }

    return getLogStream(GET, resource, containerId);
  }

  @Override
  public EventStream events(EventsParam... params)
      throws DockerException, InterruptedException {
    WebTarget resource = resource().path("events");
    resource = addParameters(resource, params);

    InputStream stream = resource.request(MediaType.APPLICATION_JSON).get(InputStream.class);
    return new EventStream(stream, objectMapper());
  }

  @Override
  public LogStream attachContainer(final String containerId,
                                   final AttachParameter... params) throws DockerException,
                                                                           InterruptedException {
    requireNonNull(containerId, "containerId");
    WebTarget resource = resource()
    		.path("containers").path(containerId).path("attach");

    for (final AttachParameter param : params) {
      resource = resource.queryParam(param.name().toLowerCase(Locale.ROOT), String.valueOf(true));
    }

    return getLogStream(POST, resource, containerId);
  }

  private LogStream getLogStream(final String method, final WebTarget resource,
                                 final String containerId)
      throws DockerException, InterruptedException {
    try {
      final Invocation.Builder request = resource.request("application/vnd.docker.raw-stream");
      return request(method, LogStream.class, resource, request);
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new BadParamException(getQueryParamMap(resource), e);
        case 404:
          throw new ContainerNotFoundException(containerId);
        default:
          throw e;
      }
    }
  }

  private LogStream getServiceLogStream(final String method, final WebTarget resource,
                                        final String serviceId)
      throws DockerException, InterruptedException {
    try {
      final Invocation.Builder request = resource.request("application/vnd.docker.raw-stream");
      return request(method, LogStream.class, resource, request);
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new BadParamException(getQueryParamMap(resource), e);
        case 404:
          throw new ServiceNotFoundException(serviceId);
        default:
          throw e;
      }
    }
  }

  @Override
  public ExecCreation execCreate(final String containerId,
                                 final String[] cmd,
                                 final ExecCreateParam... params)
      throws DockerException, InterruptedException {
    final ContainerInfo containerInfo = inspectContainer(containerId);
    if (!containerInfo.state().running()) {
      throw new IllegalStateException("Container " + containerId + " is not running.");
    }

    final WebTarget resource = resource().path("containers").path(containerId).path("exec");

    final StringWriter writer = new StringWriter();
    try (final JsonGenerator generator = objectMapper().getFactory().createGenerator(writer)) {
      generator.writeStartObject();

      for (final ExecCreateParam param : params) {
        if (param.value().equals("true") || param.value().equals("false")) {
          generator.writeBooleanField(param.name(), Boolean.valueOf(param.value()));
        } else {
          generator.writeStringField(param.name(), param.value());
        }
      }

      generator.writeArrayFieldStart("Cmd");
      for (final String s : cmd) {
        generator.writeString(s);
      }
      generator.writeEndArray();

      generator.writeEndObject();
    } catch (IOException e) {
      throw new DockerException(e);
    }

    try {
      return request(POST, ExecCreation.class, resource, resource.request(APPLICATION_JSON_TYPE),
                     Entity.json(writer.toString()));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        case 409:
          throw new ExecCreateConflictException(containerId, e);
        default:
          throw e;
      }
    }
  }


  @Override
  public LogStream execStart(final String execId, final ExecStartParameter... params)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource()
    		.path("exec").path(execId).path("start");

    final StringWriter writer = new StringWriter();
    try (final JsonGenerator generator = objectMapper().getFactory().createGenerator(writer)) {
      generator.writeStartObject();

      for (final ExecStartParameter param : params) {
        generator.writeBooleanField(param.getName(), true);
      }

      generator.writeEndObject();
    } catch (IOException e) {
      throw new DockerException(e);
    }

    try {
      return request(POST, LogStream.class, resource,
                     resource.request("application/vnd.docker.raw-stream"),
                     Entity.json(writer.toString()));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ExecNotFoundException(execId, e);
        case 409:
          throw new ExecStartConflictException(execId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public Swarm inspectSwarm() throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    final WebTarget resource = resource().path("swarm");
    return request(GET, Swarm.class, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public String initSwarm(final SwarmInit swarmInit) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    try {
      final WebTarget resource = resource().path("swarm").path("init");
      return request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
          Entity.json(swarmInit));

    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new DockerException("bad parameter", e);
        case 500:
          throw new DockerException("server error", e);
        case 503:
          throw new DockerException("node is already part of a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void joinSwarm(final SwarmJoin swarmJoin) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    try {
      final WebTarget resource = resource().path("swarm").path("join");
      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
          Entity.json(swarmJoin));

    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new DockerException("bad parameter", e);
        case 500:
          throw new DockerException("server error", e);
        case 503:
          throw new DockerException("node is already part of a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void leaveSwarm() throws DockerException, InterruptedException {
    leaveSwarm(false);
  }

  @Override
  public void leaveSwarm(final boolean force) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    try {
      final WebTarget resource = resource().path("swarm").path("leave").queryParam("force", force);
      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE));

    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 500:
          throw new DockerException("server error", e);
        case 503:
          throw new DockerException("node is not part of a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void updateSwarm(final Long version,
                          final boolean rotateWorkerToken,
                          final boolean rotateManagerToken,
                          final boolean rotateManagerUnlockKey,
                          final SwarmSpec spec)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    try {
      final WebTarget resource = resource().path("swarm").path("update")
          .queryParam("version", version)
          .queryParam("rotateWorkerToken", rotateWorkerToken)
          .queryParam("rotateManagerToken", rotateManagerToken)
          .queryParam("rotateManagerUnlockKey", rotateManagerUnlockKey);

      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
          Entity.json(spec));

    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 400:
          throw new DockerException("bad parameter", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void updateSwarm(final Long version,
                          final boolean rotateWorkerToken,
                          final boolean rotateManagerToken,
                          final SwarmSpec spec)
      throws DockerException, InterruptedException {
    updateSwarm(version, rotateWorkerToken, rotateWorkerToken, false, spec);
  }

  @Override
  public void updateSwarm(final Long version,
                          final boolean rotateWorkerToken,
                          final SwarmSpec spec)
      throws DockerException, InterruptedException {
    updateSwarm(version, rotateWorkerToken, false, false, spec);
  }

  @Override
  public void updateSwarm(final Long version,
                          final SwarmSpec spec)
      throws DockerException, InterruptedException {
    updateSwarm(version, false, false, false, spec);
  }

  @Override
  public UnlockKey unlockKey() throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    try {
      final WebTarget resource = resource().path("swarm").path("unlockkey");

      return request(GET, UnlockKey.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 500:
          throw new DockerException("server error", e);
        case 503:
          throw new DockerException("node is not part of a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void unlock(final UnlockKey unlockKey) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    try {
      final WebTarget resource = resource().path("swarm").path("unlock");

      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
          Entity.json(unlockKey));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 500:
          throw new DockerException("server error", e);
        case 503:
          throw new DockerException("node is not part of a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ServiceCreateResponse createService(ServiceSpec spec)
      throws DockerException, InterruptedException {
    return createService(spec, registryAuthSupplier.authForSwarm());
  }

  @Override
  public ServiceCreateResponse createService(final ServiceSpec spec,
                                             final RegistryAuth config)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    final WebTarget resource = resource().path("services").path("create");

    try {
      return request(POST, ServiceCreateResponse.class, resource,
                     resource.request(APPLICATION_JSON_TYPE)
                         .header("X-Registry-Auth", authHeader(config)), Entity.json(spec));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 406:
          throw new DockerException("Server error or node is not part of swarm.", e);
        case 409:
          throw new DockerException("Name conflicts with an existing object.", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public Service inspectService(final String serviceId)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    try {
      final WebTarget resource = resource().path("services").path(serviceId);
      return request(GET, Service.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ServiceNotFoundException(serviceId);
        default:
          throw e;
      }
    }
  }

  @Override
  public void updateService(final String serviceId, final Long version, final ServiceSpec spec)
      throws DockerException, InterruptedException {
    updateService(serviceId, version, spec, registryAuthSupplier.authForSwarm());
  }
  
  @Override
  public void updateService(final String serviceId, final Long version, final ServiceSpec spec,
                                             final RegistryAuth config)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    try {
      WebTarget resource = resource().path("services").path(serviceId).path("update");
      resource = resource.queryParam("version", version);
      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE)
              .header("X-Registry-Auth", authHeader(config)),
              Entity.json(spec));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ServiceNotFoundException(serviceId);
        default:
          throw e;
      }
    }
  }

  @Override
  public List<Service> listServices() throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    final WebTarget resource = resource().path("services");
    return request(GET, SERVICE_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public List<Service> listServices(final Service.Criteria criteria)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    final Map<String, List<String>> filters = new HashMap<>();

    if (criteria.serviceId() != null) {
      filters.put("id", Collections.singletonList(criteria.serviceId()));
    }
    if (criteria.serviceName() != null) {
      filters.put("name", Collections.singletonList(criteria.serviceName()));
    }

    final List<String> labels = new ArrayList<>();
    for (Entry<String, String> input: criteria.labels().entrySet()) {
      if ("".equals(input.getValue())) {
        labels.add(input.getKey());
      } else {
        labels.add(String.format("%s=%s", input.getKey(), input.getValue()));
      }
    }

    if (!labels.isEmpty()) {
      filters.put("label", labels);
    }

    WebTarget resource = resource().path("services");
    resource = resource.queryParam("filters", urlEncodeFilters(filters));
    return request(GET, SERVICE_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public void removeService(final String serviceId) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    try {
      final WebTarget resource = resource().path("services").path(serviceId);
      request(DELETE, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ServiceNotFoundException(serviceId);
        default:
          throw e;
      }
    }
  }

  @Override
  public LogStream serviceLogs(String serviceId, LogsParam... params)
          throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.25");
    WebTarget resource = resource()
            .path("services").path(serviceId)
            .path("logs");

    for (final LogsParam param : params) {
      resource = resource.queryParam(param.name(), param.value());
    }

    return getServiceLogStream(GET, resource, serviceId);
  }

  @Override
  public Task inspectTask(final String taskId) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    try {
      final WebTarget resource = resource().path("tasks").path(taskId);
      return request(GET, Task.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new TaskNotFoundException(taskId);
        default:
          throw e;
      }
    }
  }

  @Override
  public List<Task> listTasks() throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    final WebTarget resource = resource().path("tasks");
    return request(GET, TASK_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public List<Task> listTasks(final Task.Criteria criteria)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    final Map<String, List<String>> filters = new HashMap<>();

    if (criteria.taskId() != null) {
      filters.put("id", Collections.singletonList(criteria.taskId()));
    }
    if (criteria.taskName() != null) {
      filters.put("name", Collections.singletonList(criteria.taskName()));
    }
    if (criteria.serviceName() != null) {
      filters.put("service", Collections.singletonList(criteria.serviceName()));
    }
    if (criteria.nodeId() != null) {
      filters.put("node", Collections.singletonList(criteria.nodeId()));
    }
    if (criteria.label() != null) {
      filters.put("label", Collections.singletonList(criteria.label()));
    }
    if (criteria.desiredState() != null) {
      filters.put("desired-state", Collections.singletonList(criteria.desiredState()));
    }

    WebTarget resource = resource().path("tasks");
    resource = resource.queryParam("filters", urlEncodeFilters(filters));
    return request(GET, TASK_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public List<Config> listConfigs() throws DockerException, InterruptedException {
    return listConfigs(null);
  }

  @Override
  public List<Config> listConfigs(final Config.Criteria criteria)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.30");

    WebTarget resource = resource().path("configs");
    
    if (criteria != null) {
	  final Map<String, List<String>> filters = new HashMap<>();
	
	  if (criteria.configId() != null) {
	    filters.put("id", Collections.singletonList(criteria.configId()));
	  }
	  if (criteria.label() != null) {
	    filters.put("label", Collections.singletonList(criteria.label()));
	  }
	  if (criteria.name() != null) {
	    filters.put("name", Collections.singletonList(criteria.name()));
	  }
	
	  resource = resource.queryParam("filters", urlEncodeFilters(filters));
    }

    try {
      return request(GET, CONFIG_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 503:
          throw new NonSwarmNodeException("node is not part of a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ConfigCreateResponse createConfig(final ConfigSpec config)
      throws DockerException, InterruptedException {

    assertApiVersionIsAbove("1.30");
    final WebTarget resource = resource().path("configs").path("create");

    try {
      return request(POST, ConfigCreateResponse.class, resource,
          resource.request(APPLICATION_JSON_TYPE),
          Entity.json(config));
    } catch (final DockerRequestException ex) {
      switch (ex.status()) {
        case 503:
          throw new NonSwarmNodeException("Server not part of swarm.", ex);
        case 409:
          throw new ConflictException("Name conflicts with an existing object.", ex);
        default:
          throw ex;
      }
    }
  }

  @Override
  public Config inspectConfig(final String configId) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.30");
    final WebTarget resource = resource().path("configs").path(configId);

    try {
      return request(GET, Config.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (final DockerRequestException ex) {
      switch (ex.status()) {
        case 404:
          throw new NotFoundException("Config " + configId + " not found.", ex);
        case 503:
          throw new NonSwarmNodeException("Config not part of swarm.", ex);
        default:
          throw ex;
      }
    }
  }

  @Override
  public void deleteConfig(final String configId) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.30");
    final WebTarget resource = resource().path("configs").path(configId);

    try {
      request(DELETE, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (final DockerRequestException ex) {
      switch (ex.status()) {
        case 404:
          throw new NotFoundException("Config " + configId + " not found.", ex);
        case 503:
          throw new NonSwarmNodeException("Config not part of a swarm.", ex);
        default:
          throw ex;
      }
    }
  }

  @Override
  public void updateConfig(final String configId, final Long version, final ConfigSpec nodeSpec)
      throws DockerException, InterruptedException {

    assertApiVersionIsAbove("1.30");

    final WebTarget resource = resource().path("configs")
        .path(configId)
        .path("update")
        .queryParam("version", version);

    try {
      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
          Entity.json(nodeSpec));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new NotFoundException("Config " + configId + " not found.");
        case 503:
          throw new NonSwarmNodeException("Config not part of a swarm.", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public List<Node> listNodes() throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    WebTarget resource = resource().path("nodes");
    return request(GET, NODE_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public List<Node> listNodes(Node.Criteria criteria) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");
    final Map<String, List<String>> filters = new HashMap<>();

    if (criteria.nodeId() != null) {
      filters.put("id", Collections.singletonList(criteria.nodeId()));
    }
    if (criteria.label() != null) {
      filters.put("label", Collections.singletonList(criteria.label()));
    }
    if (criteria.membership() != null) {
      filters.put("membership", Collections.singletonList(criteria.membership()));
    }
    if (criteria.nodeName() != null) {
      filters.put("name", Collections.singletonList(criteria.nodeName()));
    }
    if (criteria.nodeRole() != null) {
      filters.put("role", Collections.singletonList(criteria.nodeRole()));
    }

    WebTarget resource = resource().path("nodes");
    resource = resource.queryParam("filters", urlEncodeFilters(filters));
    return request(GET, NODE_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public NodeInfo inspectNode(final String nodeId) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    WebTarget resource = resource().path("nodes")
        .path(nodeId);

    try {
      return request(GET, NodeInfo.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new NodeNotFoundException(nodeId);
        case 503:
          throw new NonSwarmNodeException("Node " + nodeId + " is not in a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void updateNode(final String nodeId, final Long version, final NodeSpec nodeSpec)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    WebTarget resource = resource().path("nodes")
        .path(nodeId)
        .path("update")
        .queryParam("version", version);

    try {
      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
          Entity.json(nodeSpec));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new NodeNotFoundException(nodeId);
        case 503:
          throw new NonSwarmNodeException("Node " + nodeId + " is not a swarm node", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void deleteNode(final String nodeId) throws DockerException, InterruptedException {
    deleteNode(nodeId, false);
  }

  @Override
  public void deleteNode(final String nodeId, final boolean force)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.24");

    final WebTarget resource = resource().path("nodes")
        .path(nodeId)
        .queryParam("force", String.valueOf(force));

    try {
      request(DELETE, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new NodeNotFoundException(nodeId);
        case 503:
          throw new NonSwarmNodeException("Node " + nodeId + " is not a swarm node", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void execResizeTty(final String execId,
                            final Integer height,
                            final Integer width)
      throws DockerException, InterruptedException {
    checkTtyParams(height, width);

    WebTarget resource = resource().path("exec").path(execId).path("resize");
    if (height != null && height > 0) {
      resource = resource.queryParam("h", height);
    }
    if (width != null && width > 0) {
      resource = resource.queryParam("w", width);
    }

    try {
      request(POST, resource, resource.request(TEXT_PLAIN_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ExecNotFoundException(execId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ExecState execInspect(final String execId) throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("exec").path(execId).path("json");

    try {
      return request(GET, ExecState.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ExecNotFoundException(execId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public ContainerStats stats(final String containerId)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("containers").path(containerId).path("stats")
        .queryParam("stream", "0");

    try {
      return request(GET, ContainerStats.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void resizeTty(final String containerId, final Integer height, final Integer width)
      throws DockerException, InterruptedException {
    checkTtyParams(height, width);

    WebTarget resource = resource().path("containers").path(containerId).path("resize");
    if (height != null && height > 0) {
      resource = resource.queryParam("h", height);
    }
    if (width != null && width > 0) {
      resource = resource.queryParam("w", width);
    }

    try {
      request(POST, resource, resource.request(TEXT_PLAIN_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new ContainerNotFoundException(containerId, e);
        default:
          throw e;
      }
    }
  }

  private void checkTtyParams(final Integer height, final Integer width) throws BadParamException {
    if ((height == null && width == null) || (height != null && height == 0)
        || (width != null && width == 0)) {
      final Map<String, String> paramMap = new HashMap<>();
      paramMap.put("h", height == null ? null : height.toString());
      paramMap.put("w", width == null ? null : width.toString());
      throw new BadParamException(paramMap, "Either width or height must be non-null and > 0");
    }
  }

  @Override
  public List<Network> listNetworks(final ListNetworksParam... params)
      throws DockerException, InterruptedException {
    WebTarget resource = resource().path("networks");
    resource = addParameters(resource, params);
    return request(GET, NETWORK_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public Network inspectNetwork(String networkId) throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("networks").path(networkId);
    try {
      return request(GET, Network.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new NetworkNotFoundException(networkId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public NetworkCreation createNetwork(NetworkConfig networkConfig)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("networks").path("create");

    try {
      return request(POST, NetworkCreation.class, resource, resource.request(APPLICATION_JSON_TYPE),
                     Entity.json(networkConfig));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new NotFoundException("Plugin not found", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void removeNetwork(String networkId) throws DockerException, InterruptedException {
    try {
      final WebTarget resource = resource().path("networks").path(networkId);
      request(DELETE, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new NetworkNotFoundException(networkId, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void connectToNetwork(String containerId, String networkId)
      throws DockerException, InterruptedException {
    connectToNetwork(networkId, NetworkConnection.builder().containerId(containerId).build());
  }

  @Override
  public void connectToNetwork(String networkId, NetworkConnection networkConnection)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("networks").path(networkId).path("connect");

    try {
      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
              Entity.json(networkConnection));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          final String message = String.format("Container %s or network %s not found.",
                  networkConnection.containerId(), networkId);
          throw new NotFoundException(message, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void disconnectFromNetwork(String containerId, String networkId)
      throws DockerException, InterruptedException {
    disconnectFromNetwork(containerId, networkId, false);
  }

  @Override
  public void disconnectFromNetwork(String containerId, String networkId, boolean force)
          throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("networks").path(networkId).path("disconnect");

    final Map<String, Object> request = new HashMap<>();
    request.put("Container", containerId);
    request.put("Force", force);

    try {
      request(POST, String.class, resource, resource.request(APPLICATION_JSON_TYPE),
              Entity.json(request));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          final String message = String.format("Container %s or network %s not found.",
                                               containerId, networkId);
          throw new NotFoundException(message, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public Volume createVolume() throws DockerException, InterruptedException {
    return createVolume(Volume.builder().build());
  }

  @Override
  public Volume createVolume(final Volume volume) throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("volumes").path("create");

    return request(POST, Volume.class, resource,
                   resource.request(APPLICATION_JSON_TYPE),
                   Entity.json(volume));
  }

  @Override
  public Volume inspectVolume(final String volumeName)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("volumes").path(volumeName);
    try {
      return request(GET, Volume.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new VolumeNotFoundException(volumeName, e);
        default:
          throw e;
      }
    }
  }

  @Override
  public void removeVolume(final Volume volume)
      throws DockerException, InterruptedException {
    removeVolume(volume.name());
  }

  @Override
  public void removeVolume(final String volumeName)
      throws DockerException, InterruptedException {
    final WebTarget resource = resource().path("volumes").path(volumeName);
    try {
      request(DELETE, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 404:
          throw new VolumeNotFoundException(volumeName, e);
        case 409:
          throw new ConflictException("Volume is in use and cannot be removed", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public VolumeList listVolumes(ListVolumesParam... params)
      throws DockerException, InterruptedException {
    WebTarget resource = resource().path("volumes");
    resource = addParameters(resource, params);
    return request(GET, VolumeList.class, resource, resource.request(APPLICATION_JSON_TYPE));
  }

  @Override
  public List<Secret> listSecrets() throws DockerException, InterruptedException {
    return listSecrets(null);
  }
  
  @Override
  public List<Secret> listSecrets(final Secret.Criteria criteria)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.25");

    WebTarget resource = resource().path("secrets");
    
    if (criteria != null) {
	  final Map<String, List<String>> filters = new HashMap<>();
	
	  if (criteria.id() != null) {
	    filters.put("id", Collections.singletonList(criteria.id()));
	  }
	  if (criteria.label() != null) {
	    filters.put("label", Collections.singletonList(criteria.label()));
	  }
	  if (criteria.name() != null) {
	    filters.put("name", Collections.singletonList(criteria.name()));
	  }
	
	  resource = resource.queryParam("filters", urlEncodeFilters(filters));
    }

    try {
      return request(GET, SECRET_LIST, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (DockerRequestException e) {
      switch (e.status()) {
        case 503:
          throw new NonSwarmNodeException("node is not part of a swarm", e);
        default:
          throw e;
      }
    }
  }

  @Override
  public SecretCreateResponse createSecret(final SecretSpec secret)
      throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.25");
    final WebTarget resource = resource().path("secrets").path("create");

    try {
      return request(POST, SecretCreateResponse.class, resource,
                     resource.request(APPLICATION_JSON_TYPE),
                     Entity.json(secret));
    } catch (final DockerRequestException ex) {
      switch (ex.status()) {
        case 406:
          throw new NonSwarmNodeException("Server not part of swarm.", ex);
        case 409:
          throw new ConflictException("Name conflicts with an existing object.", ex);
        default:
          throw ex;
      }
    }
  }

  @Override
  public Secret inspectSecret(final String secretId) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.25");
    final WebTarget resource = resource().path("secrets").path(secretId);

    try {
      return request(GET, Secret.class, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (final DockerRequestException ex) {
      switch (ex.status()) {
        case 404:
          throw new NotFoundException("Secret " + secretId + " not found.", ex);
        case 406:
          throw new NonSwarmNodeException("Server not part of swarm.", ex);
        default:
          throw ex;
      }
    }
  }

  @Override
  public void deleteSecret(final String secretId) throws DockerException, InterruptedException {
    assertApiVersionIsAbove("1.25");
    final WebTarget resource = resource().path("secrets").path(secretId);

    try {
      request(DELETE, resource, resource.request(APPLICATION_JSON_TYPE));
    } catch (final DockerRequestException ex) {
      switch (ex.status()) {
        case 404:
          throw new NotFoundException("Secret " + secretId + " not found.", ex);
        default:
          throw ex;
      }
    }
  }

  private WebTarget resource() {
    final WebTarget target = client.target(uri);
    if (apiVersion != null && !"".equals(apiVersion.trim())) {
      return target.path(apiVersion);
    }
    return target;
  }

  private <T> T request(final String method, final GenericType<T> type,
                        final WebTarget resource, final Invocation.Builder request)
      throws DockerException, InterruptedException {
    try {
      return headers(request).async().method(method, type).get();
    } catch (ExecutionException e) {
      throw propagate(method, resource, e);
    }
  }

  private <T> T request(final String method, final Class<T> clazz,
                        final WebTarget resource, final Invocation.Builder request)
      throws DockerException, InterruptedException {
    try {
      return headers(request).async().method(method, clazz).get();
    } catch (ExecutionException e) {
      throw propagate(method, resource, e);
    }
  }

  private <T> T request(final String method, final Class<T> clazz,
                        final WebTarget resource, final Invocation.Builder request,
                        final Entity<?> entity)
      throws DockerException, InterruptedException {
    try {
      return headers(request).async().method(method, entity, clazz).get();
    } catch (ExecutionException e) {
      throw propagate(method, resource, e);
    }
  }

  private void request(final String method,
                       final WebTarget resource,
                       final Invocation.Builder request)
      throws DockerException, InterruptedException {
    try {
      headers(request).async().method(method, String.class).get();
    } catch (ExecutionException e) {
      throw propagate(method, resource, e);
    }
  }

  private static class ResponseTailReader implements Callable<Void> {
    private final ProgressStream stream;
    private final ProgressHandler handler;
    private final String method;
    private final WebTarget resource;

    public ResponseTailReader(ProgressStream stream, ProgressHandler handler,
                              String method, WebTarget resource) {
      this.stream = stream;
      this.handler = handler;
      this.method = method;
      this.resource = resource;
    }

    @Override
    public Void call() throws DockerException, InterruptedException, IOException {
      stream.tail(handler, method, resource.getUri());
      return null;
    }
  }

  private void tailResponse(final String method, final Response response,
                            final ProgressHandler handler, final WebTarget resource)
        throws DockerException, InterruptedException {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      final ProgressStream stream = response.readEntity(ProgressStream.class);
      final Future<?> future = executor.submit(
              new ResponseTailReader(stream, handler, method, resource));
      future.get();
    } catch (ExecutionException e) {
      final Throwable cause = e.getCause();
      if (cause instanceof DockerException) {
        throw (DockerException)cause;
      } else {
        throw new DockerException(cause);
      }
    } finally {
      executor.shutdownNow();
      try {
        response.close();
      } catch (ProcessingException e) {
        // ignore, thrown by jnr-unixsocket when httpcomponent try to read after close
        // the socket is closed before this exception
      }
    }
  }

  private void requestAndTail(final String method, final ProgressHandler handler,
                               final WebTarget resource, final Invocation.Builder request,
                               final Entity<?> entity)
      throws DockerException, InterruptedException {
    Response response = request(method, Response.class, resource, request, entity);
    tailResponse(method, response, handler, resource);
  }
  
  private void requestAndTail(final String method, final ProgressHandler handler,
                              final WebTarget resource, final Invocation.Builder request)
      throws DockerException, InterruptedException {
    Response response = request(method, Response.class, resource, request);
    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
      throw new DockerRequestException(method, resource.getUri(), response.getStatus(),
    		  response.readEntity(String.class), null);
    }
    tailResponse(method, response, handler, resource);
  }

  private Invocation.Builder headers(final Invocation.Builder request) {
    final Set<Map.Entry<String, Object>> entries = headers.entrySet();

    for (final Map.Entry<String, Object> entry : entries) {
      request.header(entry.getKey(), entry.getValue());
    }

    return request;
  }

  private RuntimeException propagate(final String method, final WebTarget resource,
                                     final Exception ex)
      throws DockerException, InterruptedException {
    Throwable cause = ex.getCause();

    Response response = null;
    if (cause instanceof ResponseProcessingException) {
      response = ((ResponseProcessingException) cause).getResponse();
    } else if (cause instanceof WebApplicationException) {
      response = ((WebApplicationException) cause).getResponse();
    } else if ((cause instanceof ProcessingException) && (cause.getCause() != null)) {
      // For a ProcessingException, The exception message or nested Throwable cause SHOULD contain
      // additional information about the reason of the processing failure.
      cause = cause.getCause();
    }
    
    if (cause instanceof ExecutionException) {
      cause = cause.getCause();
    }

    if (response != null) {
      throw new DockerRequestException(method, resource.getUri(), response.getStatus(),
    		  	response.readEntity(String.class), cause);
    } else if (cause instanceof InterruptedIOException) {
      throw new DockerTimeoutException(method, resource.getUri(), ex);
    } else if (cause instanceof InterruptedException) {
      throw new InterruptedException("Interrupted: " + method + " " + resource);
    } else {
      throw new DockerException(ex);
    }
  }

  private String authHeader(final RegistryAuth registryAuth) throws DockerException {
    // the docker daemon requires that the X-Registry-Auth header is specified
    // with a non-empty string even if your registry doesn't use authentication
    if (registryAuth == null) {
      return "null";
    }
    try {
      return Base64.encodeBase64String(ObjectMapperProvider
                                       .objectMapper()
                                       .writeValueAsBytes(registryAuth));
    } catch (JsonProcessingException ex) {
      throw new DockerException("Could not encode X-Registry-Auth header", ex);
    }
  }

  private String authRegistryHeader(final RegistryConfigs registryConfigs)
      throws DockerException {
    if (registryConfigs == null) {
      return null;
    }
    try {
      String authRegistryJson =
          ObjectMapperProvider.objectMapper().writeValueAsString(registryConfigs.configs());

      final String apiVersion = version().apiVersion();
      final int versionComparison = compareVersion(apiVersion, "1.19");

      // Version below 1.19
      if (versionComparison < 0) {
        authRegistryJson = "{\"configs\":" + authRegistryJson + "}";
      } else if (versionComparison == 0) {
        // Version equal 1.19
        authRegistryJson = "{\"auths\":" + authRegistryJson + "}";
      }

      return Base64.encodeBase64String(authRegistryJson.getBytes(StandardCharsets.UTF_8));
    } catch (JsonProcessingException | InterruptedException ex) {
      throw new DockerException("Could not encode X-Registry-Config header", ex);
    }
  }

  private void assertApiVersionIsAbove(String minimumVersion)
      throws DockerException, InterruptedException {
    final String apiVersion = version().apiVersion();
    final int versionComparison = compareVersion(apiVersion, minimumVersion);

    // Version above minimumVersion
    if (versionComparison < 0) {
      throw new UnsupportedApiVersionException(apiVersion);
    }
  }
}
