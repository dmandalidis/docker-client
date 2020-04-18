/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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

package org.mandas.docker.client.messages;

import java.util.Date;
import java.util.List;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableContainerInfo.Builder.class)
@Immutable
@Enclosing
public interface ContainerInfo {

  @Nullable
  @JsonProperty("Id")
  String id();

  @JsonProperty("Created")
  Date created();

  @JsonProperty("Path")
  String path();

  @JsonProperty("Args")
  List<String> args();

  @JsonProperty("Config")
  ContainerConfig config();

  @Nullable
  @JsonProperty("HostConfig")
  HostConfig hostConfig();

  @JsonProperty("State")
  ContainerState state();

  @JsonProperty("Image")
  String image();

  @JsonProperty("NetworkSettings")
  NetworkSettings networkSettings();

  @JsonProperty("ResolvConfPath")
  String resolvConfPath();

  @JsonProperty("HostnamePath")
  String hostnamePath();

  @JsonProperty("HostsPath")
  String hostsPath();

  @JsonProperty("Name")
  String name();

  @JsonProperty("Driver")
  String driver();

  @Nullable
  @JsonProperty("ExecDriver")
  String execDriver();

  @JsonProperty("ProcessLabel")
  String processLabel();

  @JsonProperty("MountLabel")
  String mountLabel();

  @JsonProperty("AppArmorProfile")
  String appArmorProfile();

  @Nullable
  @JsonProperty("ExecIDs")
  List<String> execIds();

  @JsonProperty("LogPath")
  String logPath();

  @JsonProperty("RestartCount")
  Long restartCount();

  @Nullable
  @JsonProperty("Mounts")
  List<ContainerMount> mounts();

  /**
   * This field is an extension defined by the Docker Swarm API, therefore it will only be populated
   * when communicating with a Swarm cluster.
   * @return the node information where the container is placed
   */
  @Nullable
  @JsonProperty("Node")
  Node node();

  @JsonDeserialize(builder = ImmutableContainerInfo.Node.Builder.class)
  @Immutable
  public interface Node {

    @JsonProperty("ID")
    String id();

    @JsonProperty("IP")
    String ip();

    @JsonProperty("Addr")
    String addr();

    @JsonProperty("Name")
    String name();
  }
}
