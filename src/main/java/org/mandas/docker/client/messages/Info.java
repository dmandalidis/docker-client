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

package org.mandas.docker.client.messages;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.swarm.SwarmInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableInfo.Builder.class)
@Immutable
@Enclosing
public interface Info {

  @Nullable
  @JsonProperty("Architecture")
  String architecture();

  @Nullable
  @JsonProperty("ClusterStore")
  String clusterStore();

  @Nullable
  @JsonProperty("CgroupDriver")
  String cgroupDriver();

  @JsonProperty("Containers")
  Integer containers();

  @Nullable
  @JsonProperty("ContainersRunning")
  Integer containersRunning();

  @Nullable
  @JsonProperty("ContainersStopped")
  Integer containersStopped();

  @Nullable
  @JsonProperty("ContainersPaused")
  Integer containersPaused();

  @Nullable
  @JsonProperty("CpuCfsPeriod")
  Boolean cpuCfsPeriod();

  @Nullable
  @JsonProperty("CpuCfsQuota")
  Boolean cpuCfsQuota();

  @JsonProperty("Debug")
  Boolean debug();

  @JsonProperty("DockerRootDir")
  String dockerRootDir();

  @JsonProperty("Driver")
  String storageDriver();

  @JsonProperty("DriverStatus")
  List<List<String>> driverStatus();

  @Nullable
  @JsonProperty("ExperimentalBuild")
  Boolean experimentalBuild();

  @Nullable
  @JsonProperty("HttpProxy")
  String httpProxy();

  @Nullable
  @JsonProperty("HttpsProxy")
  String httpsProxy();

  @JsonProperty("ID")
  String id();

  @JsonProperty("IPv4Forwarding")
  Boolean ipv4Forwarding();

  @JsonProperty("Images")
  Integer images();

  @JsonProperty("IndexServerAddress")
  String indexServerAddress();

  @Nullable
  @JsonProperty("InitPath")
  String initPath();

  @Nullable
  @JsonProperty("InitSha1")
  String initSha1();

  @Nullable
  @JsonProperty("KernelMemory")
  Boolean kernelMemory();

  @JsonProperty("KernelVersion")
  String kernelVersion();

  @JsonProperty("Labels")
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  List<String> labels();

  @JsonProperty("MemTotal")
  Long memTotal();

  @JsonProperty("MemoryLimit")
  Boolean memoryLimit();

  @JsonProperty("NCPU")
  Integer cpus();

  @JsonProperty("NEventsListener")
  Integer eventsListener();

  @JsonProperty("NFd")
  Integer fileDescriptors();

  @JsonProperty("NGoroutines")
  Integer goroutines();

  @JsonProperty("Name")
  String name();

  @Nullable
  @JsonProperty("NoProxy")
  String noProxy();

  @Nullable
  @JsonProperty("OomKillDisable")
  Boolean oomKillDisable();

  @JsonProperty("OperatingSystem")
  String operatingSystem();

  @Nullable
  @JsonProperty("OSType")
  String osType();

  @Nullable
  @JsonProperty("Plugins")
  Plugins plugins();

  @JsonProperty("RegistryConfig")
  RegistryConfig registryConfig();

  @Nullable
  @JsonProperty("ServerVersion")
  String serverVersion();

  @JsonProperty("SwapLimit")
  Boolean swapLimit();
  
  @Nullable
  @JsonProperty("Swarm")
  SwarmInfo swarm();

  @Nullable
  @JsonProperty("SystemStatus")
  List<List<String>> systemStatus();

  @JsonProperty("SystemTime")
  Date systemTime();

  @JsonDeserialize(builder = ImmutableInfo.Plugins.Builder.class)
  @Immutable
  public interface Plugins {

    @JsonProperty("Volume")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    List<String> volumes();

    /**
     * Return the value of the `network` json path.
     * todo this method should be renamed to network
     */
    @JsonProperty("Network")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    List<String> networks();
  }

  @JsonDeserialize(builder = ImmutableInfo.RegistryConfig.Builder.class)
  @Immutable
  public interface RegistryConfig {

    @JsonProperty("IndexConfigs")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    Map<String, IndexConfig> indexConfigs();

    @JsonProperty("InsecureRegistryCIDRs")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    List<String> insecureRegistryCidrs();
  }

  @JsonDeserialize(builder = ImmutableInfo.IndexConfig.Builder.class)
  @Immutable
  public interface IndexConfig {

    @JsonProperty("Name")
    String name();

    @JsonProperty("Mirrors")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    List<String> mirrors();

    @JsonProperty("Secure")
    Boolean secure();

    @JsonProperty("Official")
    Boolean official();
  }
}
