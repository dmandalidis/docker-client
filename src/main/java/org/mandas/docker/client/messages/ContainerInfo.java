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

package org.mandas.docker.client.messages;

import java.util.Date;
import java.util.List;

import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = ContainerInfo.Builder.class)
/**
 * Container information
 * @param id optional container ID
 * @param created creation timestamp
 * @param path container path
 * @param args container arguments
 * @param config container configuration
 * @param hostConfig optional host configuration
 * @param state container state
 * @param image container image
 * @param networkSettings network settings
 * @param resolvConfPath resolve configuration path
 * @param hostnamePath hostname path
 * @param hostsPath hosts file path
 * @param name container name
 * @param driver driver name
 * @param execDriver optional exec driver
 * @param processLabel process label
 * @param mountLabel mount label
 * @param appArmorProfile AppArmor profile
 * @param execIds optional exec IDs
 * @param logPath log path
 * @param restartCount restart count
 * @param mounts optional mounted volumes
 * @param node optional node (Swarm extension)
 */
public record ContainerInfo(
    @Nullable
    @JsonProperty("Id")
    String id,
    @JsonProperty("Created")
    Date created,
    @JsonProperty("Path")
    String path,
    @JsonProperty("Args")
    List<String> args,
    @JsonProperty("Config")
    ContainerConfig config,
    @Nullable
    @JsonProperty("HostConfig")
    HostConfig hostConfig,
    @JsonProperty("State")
    ContainerState state,
    @JsonProperty("Image")
    String image,
    @JsonProperty("NetworkSettings")
    NetworkSettings networkSettings,
    @JsonProperty("ResolvConfPath")
    String resolvConfPath,
    @JsonProperty("HostnamePath")
    String hostnamePath,
    @JsonProperty("HostsPath")
    String hostsPath,
    @JsonProperty("Name")
    String name,
    @JsonProperty("Driver")
    String driver,
    @Nullable
    @JsonProperty("ExecDriver")
    String execDriver,
    @JsonProperty("ProcessLabel")
    String processLabel,
    @JsonProperty("MountLabel")
    String mountLabel,
    @JsonProperty("AppArmorProfile")
    String appArmorProfile,
    @Nullable
    @JsonProperty("ExecIDs")
    List<String> execIds,
    @JsonProperty("LogPath")
    String logPath,
    @JsonProperty("RestartCount")
    Long restartCount,
    @Nullable
    @JsonProperty("Mounts")
    List<ContainerMount> mounts,
    @Nullable
    @JsonProperty("Node")
    Node node) {

  /**
   * Node information - This field is an extension defined by the Docker Swarm API, 
   * therefore it will only be populated when communicating with a Swarm cluster.
   * @param id the node ID
   * @param ip the node IP address
   * @param addr the node address
   * @param name the node name
   */
  @JsonDeserialize(builder = Node.Builder.class)
  public record Node(
      @JsonProperty("ID")
      String id,
      @JsonProperty("IP")
      String ip,
      @JsonProperty("Addr")
      String addr,
      @JsonProperty("Name")
      String name) {

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
      private String id;
      private String ip;
      private String addr;
      private String name;

      public Builder id(String id) {
        this.id = id;
        return this;
      }

      public Builder ip(String ip) {
        this.ip = ip;
        return this;
      }

      public Builder addr(String addr) {
        this.addr = addr;
        return this;
      }

      public Builder name(String name) {
        this.name = name;
        return this;
      }

      public Node build() {
        return new Node(id, ip, addr, name);
      }
    }
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String id;
    private Date created;
    private String path;
    private List<String> args;
    private ContainerConfig config;
    private HostConfig hostConfig;
    private ContainerState state;
    private String image;
    private NetworkSettings networkSettings;
    private String resolvConfPath;
    private String hostnamePath;
    private String hostsPath;
    private String name;
    private String driver;
    private String execDriver;
    private String processLabel;
    private String mountLabel;
    private String appArmorProfile;
    private List<String> execIds;
    private String logPath;
    private Long restartCount;
    private List<ContainerMount> mounts;
    private Node node;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder created(Date created) {
      this.created = created;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder args(List<String> args) {
      this.args = args;
      return this;
    }

    public Builder config(ContainerConfig config) {
      this.config = config;
      return this;
    }

    public Builder hostConfig(HostConfig hostConfig) {
      this.hostConfig = hostConfig;
      return this;
    }

    public Builder state(ContainerState state) {
      this.state = state;
      return this;
    }

    public Builder image(String image) {
      this.image = image;
      return this;
    }

    public Builder networkSettings(NetworkSettings networkSettings) {
      this.networkSettings = networkSettings;
      return this;
    }

    public Builder resolvConfPath(String resolvConfPath) {
      this.resolvConfPath = resolvConfPath;
      return this;
    }

    public Builder hostnamePath(String hostnamePath) {
      this.hostnamePath = hostnamePath;
      return this;
    }

    public Builder hostsPath(String hostsPath) {
      this.hostsPath = hostsPath;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder driver(String driver) {
      this.driver = driver;
      return this;
    }

    public Builder execDriver(String execDriver) {
      this.execDriver = execDriver;
      return this;
    }

    public Builder processLabel(String processLabel) {
      this.processLabel = processLabel;
      return this;
    }

    public Builder mountLabel(String mountLabel) {
      this.mountLabel = mountLabel;
      return this;
    }

    public Builder appArmorProfile(String appArmorProfile) {
      this.appArmorProfile = appArmorProfile;
      return this;
    }

    public Builder execIds(List<String> execIds) {
      this.execIds = execIds;
      return this;
    }

    public Builder logPath(String logPath) {
      this.logPath = logPath;
      return this;
    }

    public Builder restartCount(Long restartCount) {
      this.restartCount = restartCount;
      return this;
    }

    public Builder mounts(List<ContainerMount> mounts) {
      this.mounts = mounts;
      return this;
    }

    public Builder node(Node node) {
      this.node = node;
      return this;
    }

    public ContainerInfo build() {
      return new ContainerInfo(id, created, path, args, config, hostConfig, state, image, 
          networkSettings, resolvConfPath, hostnamePath, hostsPath, name, driver, 
          execDriver, processLabel, mountLabel, appArmorProfile, execIds, logPath, 
          restartCount, mounts, node);
    }
  }
}
