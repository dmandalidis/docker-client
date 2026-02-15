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

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.mount.Mount;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record HostConfig(
  @Nullable
  @JsonProperty("Binds")
  List<String> binds,

  @Nullable
  @JsonProperty("BlkioWeight")
  Integer blkioWeight,

  @Nullable
  @JsonProperty("BlkioWeightDevice")
  List<BlkioWeightDevice> blkioWeightDevice,

  @Nullable
  @JsonProperty("BlkioDeviceReadBps")
  List<BlkioDeviceRate> blkioDeviceReadBps,

  @Nullable
  @JsonProperty("BlkioDeviceWriteBps")
  List<BlkioDeviceRate> blkioDeviceWriteBps,

  @Nullable
  @JsonProperty("BlkioDeviceReadIOps")
  List<BlkioDeviceRate> blkioDeviceReadIOps,

  @Nullable
  @JsonProperty("BlkioDeviceWriteIOps")
  List<BlkioDeviceRate> blkioDeviceWriteIOps,

  @Nullable
  @JsonProperty("ContainerIDFile")
  String containerIdFile,

  @Nullable
  @JsonProperty("LxcConf")
  List<LxcConfParameter> lxcConf,

  @Nullable
  @JsonProperty("Privileged")
  Boolean privileged,

  @Nullable
  @JsonProperty("PortBindings")
  Map<String, List<PortBinding>> portBindings,

  @Nullable
  @JsonProperty("Links")
  List<String> links,

  @Nullable
  @JsonProperty("PublishAllPorts")
  Boolean publishAllPorts,

  @Nullable
  @JsonProperty("Dns")
  List<String> dns,

  @Nullable
  @JsonProperty("DnsOptions")
  List<String> dnsOptions,

  @Nullable
  @JsonProperty("DnsSearch")
  List<String> dnsSearch,

  @Nullable
  @JsonProperty("ExtraHosts")
  List<String> extraHosts,
  
  @Nullable
  @JsonProperty("GroupAdd")
  List<String> groupAdd,
  
  @Nullable
  @JsonProperty("VolumesFrom")
  List<String> volumesFrom,

  @Nullable
  @JsonProperty("CapAdd")
  List<String> capAdd,

  @Nullable
  @JsonProperty("CapDrop")
  List<String> capDrop,

  @Nullable
  @JsonProperty("NetworkMode")
  String networkMode,

  @Nullable
  @JsonProperty("SecurityOpt")
  List<String> securityOpt,

  @Nullable
  @JsonProperty("Devices")
  List<Device> devices,

  @Nullable
  @JsonProperty("Memory")
  Long memory,

  @Nullable
  @JsonProperty("MemorySwap")
  Long memorySwap,

  @Nullable
  @JsonProperty("MemorySwappiness")
  Integer memorySwappiness,

  @Nullable
  @JsonProperty("MemoryReservation")
  Long memoryReservation,

  @Nullable
  @JsonProperty("NanoCpus")
  Long nanoCpus,

  @Nullable
  @JsonProperty("CpuPeriod")
  Long cpuPeriod,

  @Nullable
  @JsonProperty("CpuShares")
  Long cpuShares,

  @Nullable
  @JsonProperty("CpusetCpus")
  String cpusetCpus,

  @Nullable
  @JsonProperty("CpusetMems")
  String cpusetMems,

  @Nullable
  @JsonProperty("CpuQuota")
  Long cpuQuota,

  @Nullable
  @JsonProperty("CgroupParent")
  String cgroupParent,

  @Nullable
  @JsonProperty("RestartPolicy")
  RestartPolicy restartPolicy,

  @Nullable
  @JsonProperty("LogConfig")
  LogConfig logConfig,

  @Nullable
  @JsonProperty("IpcMode")
  String ipcMode,

  @Nullable
  @JsonProperty("Ulimits")
  List<Ulimit> ulimits,

  @Nullable
  @JsonProperty("PidMode")
  String pidMode,

  @Nullable
  @JsonProperty("UsernsMode")
  String usernsMode,

  @Nullable
  @JsonProperty("ShmSize")
  Long shmSize,

  @Nullable
  @JsonProperty("OomKillDisable")
  Boolean oomKillDisable,

  @Nullable
  @JsonProperty("OomScoreAdj")
  Integer oomScoreAdj,

  @Nullable
  @JsonProperty("AutoRemove")
  Boolean autoRemove,

  @Nullable
  @JsonProperty("PidsLimit")
  Integer pidsLimit,

  @Nullable
  @JsonProperty("Tmpfs")
  Map<String, String> tmpfs,

  @Nullable
  @JsonProperty("ReadonlyRootfs")
  Boolean readonlyRootfs,
  
  @Nullable
  @JsonProperty("StorageOpt")
  Map<String, String> storageOpt,

  @Nullable
  @JsonProperty("Runtime")
  String runtime,
  
  @Nullable
  @JsonProperty("Mounts")
  List<Mount> mounts,

  @Nullable
  @JsonProperty("Init")
  Boolean init,
  
  @JsonProperty("Sysctls")
  Map<String, String> sysctls,
  
  @Nullable
  @JsonProperty("DeviceRequests")
  List<DeviceRequest> deviceRequests) {
  
  public HostConfig {
    if (extraHosts != null) {
      for (final String extraHost : extraHosts) {
        if (!extraHost.contains(":")) {
          throw new IllegalArgumentException(format("extra host arg '%s' must contain a ':'", extraHost));
        }
      }
    }
  }

  public record LxcConfParameter(
    @JsonProperty("Key")
    String key,

    @JsonProperty("Value")
    String value) {
  }

  public record RestartPolicy(
    @JsonProperty("Name")
    String name,

    @Nullable
    @JsonProperty("MaximumRetryCount")
    Integer maxRetryCount) {

    public static RestartPolicy always() {
      return new RestartPolicy("always", null);
    }

    public static RestartPolicy unlessStopped() {
      return new RestartPolicy("unless-stopped", null);
    }

    public static RestartPolicy onFailure(Integer maxRetryCount) {
      return new RestartPolicy("on-failure", maxRetryCount);
    }
  }

  @JsonIgnore
  public Builder toBuilder() {
    return new Builder(this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<String> binds;
    private Integer blkioWeight;
    private List<BlkioWeightDevice> blkioWeightDevice;
    private List<BlkioDeviceRate> blkioDeviceReadBps;
    private List<BlkioDeviceRate> blkioDeviceWriteBps;
    private List<BlkioDeviceRate> blkioDeviceReadIOps;
    private List<BlkioDeviceRate> blkioDeviceWriteIOps;
    private String containerIdFile;
    private List<LxcConfParameter> lxcConf;
    private Boolean privileged;
    private Map<String, List<PortBinding>> portBindings;
    private List<String> links;
    private Boolean publishAllPorts;
    private List<String> dns;
    private List<String> dnsOptions;
    private List<String> dnsSearch;
    private List<String> extraHosts;
    private List<String> groupAdd;
    private List<String> volumesFrom;
    private List<String> capAdd;
    private List<String> capDrop;
    private String networkMode;
    private List<String> securityOpt;
    private List<Device> devices;
    private Long memory;
    private Long memorySwap;
    private Integer memorySwappiness;
    private Long memoryReservation;
    private Long nanoCpus;
    private Long cpuPeriod;
    private Long cpuShares;
    private String cpusetCpus;
    private String cpusetMems;
    private Long cpuQuota;
    private String cgroupParent;
    private RestartPolicy restartPolicy;
    private LogConfig logConfig;
    private String ipcMode;
    private List<Ulimit> ulimits;
    private String pidMode;
    private String usernsMode;
    private Long shmSize;
    private Boolean oomKillDisable;
    private Integer oomScoreAdj;
    private Boolean autoRemove;
    private Integer pidsLimit;
    private Map<String, String> tmpfs;
    private Boolean readonlyRootfs;
    private Map<String, String> storageOpt;
    private String runtime;
    private List<Mount> mounts;
    private Boolean init;
    private Map<String, String> sysctls;
    private List<DeviceRequest> deviceRequests;

    public Builder() {
    }

    public Builder(HostConfig config) {
      this.binds = config.binds;
      this.blkioWeight = config.blkioWeight;
      this.blkioWeightDevice = config.blkioWeightDevice;
      this.blkioDeviceReadBps = config.blkioDeviceReadBps;
      this.blkioDeviceWriteBps = config.blkioDeviceWriteBps;
      this.blkioDeviceReadIOps = config.blkioDeviceReadIOps;
      this.blkioDeviceWriteIOps = config.blkioDeviceWriteIOps;
      this.containerIdFile = config.containerIdFile;
      this.lxcConf = config.lxcConf;
      this.privileged = config.privileged;
      this.portBindings = config.portBindings;
      this.links = config.links;
      this.publishAllPorts = config.publishAllPorts;
      this.dns = config.dns;
      this.dnsOptions = config.dnsOptions;
      this.dnsSearch = config.dnsSearch;
      this.extraHosts = config.extraHosts;
      this.groupAdd = config.groupAdd;
      this.volumesFrom = config.volumesFrom;
      this.capAdd = config.capAdd;
      this.capDrop = config.capDrop;
      this.networkMode = config.networkMode;
      this.securityOpt = config.securityOpt;
      this.devices = config.devices;
      this.memory = config.memory;
      this.memorySwap = config.memorySwap;
      this.memorySwappiness = config.memorySwappiness;
      this.memoryReservation = config.memoryReservation;
      this.nanoCpus = config.nanoCpus;
      this.cpuPeriod = config.cpuPeriod;
      this.cpuShares = config.cpuShares;
      this.cpusetCpus = config.cpusetCpus;
      this.cpusetMems = config.cpusetMems;
      this.cpuQuota = config.cpuQuota;
      this.cgroupParent = config.cgroupParent;
      this.restartPolicy = config.restartPolicy;
      this.logConfig = config.logConfig;
      this.ipcMode = config.ipcMode;
      this.ulimits = config.ulimits;
      this.pidMode = config.pidMode;
      this.usernsMode = config.usernsMode;
      this.shmSize = config.shmSize;
      this.oomKillDisable = config.oomKillDisable;
      this.oomScoreAdj = config.oomScoreAdj;
      this.autoRemove = config.autoRemove;
      this.pidsLimit = config.pidsLimit;
      this.tmpfs = config.tmpfs;
      this.readonlyRootfs = config.readonlyRootfs;
      this.storageOpt = config.storageOpt;
      this.runtime = config.runtime;
      this.mounts = config.mounts;
      this.init = config.init;
      this.sysctls = config.sysctls;
      this.deviceRequests = config.deviceRequests;
    }

    public Builder binds(List<String> binds) {
      this.binds = binds;
      return this;
    }

    public Builder binds(String... binds) {
      if (binds != null) {
        return binds(List.of(binds));
      }
      return this;
    }
    
    public Builder binds(Bind... binds) {
      List<String> bindsValue = stream(binds).map(Bind::representation).toList();
      return binds(bindsValue);
    }

    public Builder blkioWeight(Integer blkioWeight) {
      this.blkioWeight = blkioWeight;
      return this;
    }

    public Builder blkioWeightDevice(List<BlkioWeightDevice> blkioWeightDevice) {
      this.blkioWeightDevice = blkioWeightDevice;
      return this;
    }

    public Builder blkioDeviceReadBps(List<BlkioDeviceRate> blkioDeviceReadBps) {
      this.blkioDeviceReadBps = blkioDeviceReadBps;
      return this;
    }

    public Builder blkioDeviceWriteBps(List<BlkioDeviceRate> blkioDeviceWriteBps) {
      this.blkioDeviceWriteBps = blkioDeviceWriteBps;
      return this;
    }

    public Builder blkioDeviceReadIOps(List<BlkioDeviceRate> blkioDeviceReadIOps) {
      this.blkioDeviceReadIOps = blkioDeviceReadIOps;
      return this;
    }

    public Builder blkioDeviceWriteIOps(List<BlkioDeviceRate> blkioDeviceWriteIOps) {
      this.blkioDeviceWriteIOps = blkioDeviceWriteIOps;
      return this;
    }

    public Builder containerIdFile(String containerIdFile) {
      this.containerIdFile = containerIdFile;
      return this;
    }

    public Builder lxcConf(List<LxcConfParameter> lxcConf) {
      this.lxcConf = lxcConf;
      return this;
    }

    public Builder lxcConf(LxcConfParameter... lxcConf) {
      this.lxcConf = lxcConf != null ? List.of(lxcConf) : null;
      return this;
    }

    public Builder privileged(Boolean privileged) {
      this.privileged = privileged;
      return this;
    }

    public Builder portBindings(Map<String, ? extends List<PortBinding>> portBindings) {
      this.portBindings = portBindings != null ? Map.copyOf(portBindings) : null;
      return this;
    }

    public Builder links(List<String> links) {
      this.links = links;
      return this;
    }

    public Builder links(String... links) {
      this.links = links != null ? List.of(links) : null;
      return this;
    }

    public Builder publishAllPorts(Boolean publishAllPorts) {
      this.publishAllPorts = publishAllPorts;
      return this;
    }

    public Builder dns(List<String> dns) {
      this.dns = dns;
      return this;
    }

    public Builder dns(String... dns) {
      this.dns = dns != null ? List.of(dns) : null;
      return this;
    }

    public Builder dnsOptions(List<String> dnsOptions) {
      this.dnsOptions = dnsOptions;
      return this;
    }

    public Builder dnsOptions(String... dnsOptions) {
      this.dnsOptions = dnsOptions != null ? List.of(dnsOptions) : null;
      return this;
    }

    public Builder dnsSearch(List<String> dnsSearch) {
      this.dnsSearch = dnsSearch;
      return this;
    }

    public Builder dnsSearch(String... dnsSearch) {
      this.dnsSearch = dnsSearch != null ? List.of(dnsSearch) : null;
      return this;
    }

    public Builder extraHosts(List<String> extraHosts) {
      this.extraHosts = extraHosts;
      return this;
    }

    public Builder extraHosts(String... extraHosts) {
      this.extraHosts = extraHosts != null ? List.of(extraHosts) : null;
      return this;
    }
    
    public Builder groupAdd(List<String> groupAdd) {
      this.groupAdd = groupAdd;
      return this;
    }

    public Builder groupAdd(String... groupAdd) {
      this.groupAdd = groupAdd != null ? List.of(groupAdd) : null;
      return this;
    }

    public Builder volumesFrom(List<String> volumesFrom) {
      this.volumesFrom = volumesFrom;
      return this;
    }

    public Builder volumesFrom(String... volumesFrom) {
      this.volumesFrom = volumesFrom != null ? List.of(volumesFrom) : null;
      return this;
    }

    public Builder capAdd(List<String> capAdd) {
      this.capAdd = capAdd;
      return this;
    }

    public Builder capAdd(String... capAdd) {
      this.capAdd = capAdd != null ? List.of(capAdd) : null;
      return this;
    }

    public Builder capDrop(List<String> capDrop) {
      this.capDrop = capDrop;
      return this;
    }

    public Builder capDrop(String... capDrop) {
      this.capDrop = capDrop != null ? List.of(capDrop) : null;
      return this;
    }

    public Builder networkMode(String networkMode) {
      this.networkMode = networkMode;
      return this;
    }

    public Builder securityOpt(List<String> securityOpt) {
      this.securityOpt = securityOpt;
      return this;
    }

    public Builder securityOpt(String... securityOpt) {
      this.securityOpt = securityOpt != null ? List.of(securityOpt) : null;
      return this;
    }

    public Builder devices(List<Device> devices) {
      this.devices = devices;
      return this;
    }

    public Builder devices(Device... devices) {
      this.devices = devices != null ? List.of(devices) : null;
      return this;
    }

    public Builder memory(Long memory) {
      this.memory = memory;
      return this;
    }

    public Builder memorySwap(Long memorySwap) {
      this.memorySwap = memorySwap;
      return this;
    }

    public Builder memorySwappiness(Integer memorySwappiness) {
      this.memorySwappiness = memorySwappiness;
      return this;
    }
    
    public Builder memoryReservation(Long memoryReservation) {
      this.memoryReservation = memoryReservation;
      return this;
    }

    public Builder nanoCpus(Long nanoCpus) {
      this.nanoCpus = nanoCpus;
      return this;
    }

    public Builder cpuPeriod(Long cpuPeriod) {
      this.cpuPeriod = cpuPeriod;
      return this;
    }

    public Builder cpuShares(Long cpuShares) {
      this.cpuShares = cpuShares;
      return this;
    }

    public Builder cpusetCpus(String cpusetCpus) {
      this.cpusetCpus = cpusetCpus;
      return this;
    }

    public Builder cpusetMems(String cpusetMems) {
      this.cpusetMems = cpusetMems;
      return this;
    }

    public Builder cpuQuota(Long cpuQuota) {
      this.cpuQuota = cpuQuota;
      return this;
    }

    public Builder cgroupParent(String cgroupParent) {
      this.cgroupParent = cgroupParent;
      return this;
    }

    public Builder restartPolicy(RestartPolicy restartPolicy) {
      this.restartPolicy = restartPolicy;
      return this;
    }

    public Builder logConfig(LogConfig logConfig) {
      this.logConfig = logConfig;
      return this;
    }

    public Builder ipcMode(String ipcMode) {
      this.ipcMode = ipcMode;
      return this;
    }

    public Builder ulimits(List<Ulimit> ulimits) {
      this.ulimits = ulimits;
      return this;
    }

    public Builder pidMode(String pidMode) {
      this.pidMode = pidMode;
      return this;
    }

    public Builder containerPidMode(final String container) {
      pidMode("container:" + container);
      return this;
    }

    public Builder hostPidMode() {
      pidMode("host");
      return this;
    }

    public Builder usernsMode(String usernsMode) {
      this.usernsMode = usernsMode;
      return this;
    }

    public Builder shmSize(Long shmSize) {
      this.shmSize = shmSize;
      return this;
    }

    public Builder oomKillDisable(Boolean oomKillDisable) {
      this.oomKillDisable = oomKillDisable;
      return this;
    }

    public Builder oomScoreAdj(Integer oomScoreAdj) {
      this.oomScoreAdj = oomScoreAdj;
      return this;
    }

    public Builder autoRemove(Boolean autoRemove) {
      this.autoRemove = autoRemove;
      return this;
    }

    public Builder pidsLimit(Integer pidsLimit) {
      this.pidsLimit = pidsLimit;
      return this;
    }

    public Builder tmpfs(Map<String, String> tmpfs) {
      this.tmpfs = tmpfs != null ? Map.copyOf(tmpfs) : null;
      return this;
    }

    public Builder readonlyRootfs(Boolean readonlyRootfs) {
      this.readonlyRootfs = readonlyRootfs;
      return this;
    }
    
    public Builder storageOpt(Map<String, String> storageOpt) {
      this.storageOpt = storageOpt != null ? Map.copyOf(storageOpt) : null;
      return this;
    }

    public Builder runtime(String runtime) {
      this.runtime = runtime;
      return this;
    }

    public Builder mounts(final List<Mount> mounts) {
      this.mounts = mounts;
      return this;
    }

    public Builder mounts(final Mount... mounts) {
      this.mounts = mounts != null ? List.of(mounts) : null;
      return this;
    }
    
    public Builder init(Boolean init) {
      this.init = init;
      return this;
    }
    
    public Builder sysctls(Map<String, String> sysctls) {
      this.sysctls = sysctls != null ? Map.copyOf(sysctls) : null;
      return this;
    }
    
    public Builder addSysctl(String key, String value) {
      if (this.sysctls == null) {
        this.sysctls = Map.of(key, value);
      } else {
        Map<String, String> updated = new java.util.HashMap<>(this.sysctls);
        updated.put(key, value);
        this.sysctls = Map.copyOf(updated);
      }
      return this;
    }
    
    public Builder deviceRequests(List<DeviceRequest> deviceRequests) {
      this.deviceRequests = deviceRequests;
      return this;
    }
    
    public HostConfig build() {
      return new HostConfig(binds, blkioWeight, blkioWeightDevice, blkioDeviceReadBps, 
          blkioDeviceWriteBps, blkioDeviceReadIOps, blkioDeviceWriteIOps, containerIdFile,
          lxcConf, privileged, portBindings, links, publishAllPorts, dns, dnsOptions, 
          dnsSearch, extraHosts, groupAdd, volumesFrom, capAdd, capDrop, networkMode, 
          securityOpt, devices, memory, memorySwap, memorySwappiness, memoryReservation, 
          nanoCpus, cpuPeriod, cpuShares, cpusetCpus, cpusetMems, cpuQuota, cgroupParent, 
          restartPolicy, logConfig, ipcMode, ulimits, pidMode, usernsMode, shmSize, 
          oomKillDisable, oomScoreAdj, autoRemove, pidsLimit, tmpfs, readonlyRootfs, 
          storageOpt, runtime, mounts, init, sysctls, deviceRequests);
    }
  }

  public record Bind(
    String to,
    String from,
    boolean readOnly,
    @Nullable Boolean noCopy,
    @Nullable Boolean selinuxLabeling) {

    public Bind {
      // Compact constructor with default value handling already done by builder
    }

    public static Builder builder() {
      return new Builder();
    }

    @JsonIgnore
    public String representation() {
      if (to == null || "".equals(to.trim())) {
        return "";
      } else if (from == null || "".equals(from.trim())) {
        return to;
      }

      final String bind = from + ":" + to;

      final List<String> options = new ArrayList<>();
      if (readOnly) {
        options.add("ro");
      }
      if (noCopy != null && noCopy) {
        options.add("nocopy");
      }

      if (selinuxLabeling != null) {
        if (Boolean.TRUE.equals(selinuxLabeling)) {
          options.add("z");
        } else {
          options.add("Z");
        }
      }

      final String optionsValue = options.stream().collect(joining(","));

      return (optionsValue.isEmpty()) ? bind : bind + ":" + optionsValue;
    }

    public static class Builder {
      private String to;
      private String from;
      private boolean readOnly = false;
      private Boolean noCopy;
      private Boolean selinuxLabeling;

      public Builder to(String to) {
        this.to = to;
        return this;
      }

      public Builder from(String from) {
        this.from = from;
        return this;
      }

      public Builder readOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
      }

      public Builder noCopy(Boolean noCopy) {
        this.noCopy = noCopy;
        return this;
      }

      public Builder selinuxLabeling(Boolean sharedContent) {
        this.selinuxLabeling = sharedContent;
        return this;
      }

      public Bind build() {
        return new Bind(to, from, readOnly, noCopy, selinuxLabeling);
      }
    }
  }

  public record Ulimit(
    @JsonProperty("Name")
    String name,

    @JsonProperty("Soft")
    Long soft,

    @JsonProperty("Hard")
    Long hard) {

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private String name;
      private Long soft;
      private Long hard;

      public Builder name(String name) {
        this.name = name;
        return this;
      }

      public Builder soft(Long soft) {
        this.soft = soft;
        return this;
      }

      public Builder hard(Long hard) {
        this.hard = hard;
        return this;
      }

      public Ulimit build() {
        return new Ulimit(name, soft, hard);
      }
    }
  }

  public record BlkioWeightDevice(
    @JsonProperty("Path")
    String path,

    @JsonProperty("Weight")
    Integer weight) {

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private String path;
      private Integer weight;

      public Builder path(final String path) {
        this.path = path;
        return this;
      }

      public Builder weight(final Integer weight) {
        this.weight = weight;
        return this;
      }

      public BlkioWeightDevice build() {
        return new BlkioWeightDevice(path, weight);
      }
    }
  }

  public record BlkioDeviceRate(
    @JsonProperty("Path")
    String path,

    @JsonProperty("Rate")
    Integer rate) {

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private String path;
      private Integer rate;

      public Builder path(final String path) {
        this.path = path;
        return this;
      }

      public Builder rate(final Integer rate) {
        this.rate = rate;
        return this;
      }

      public BlkioDeviceRate build() {
        return new BlkioDeviceRate(path, rate);
      }
    }
  }
  
  public record DeviceRequest(
    @Nullable
    @JsonProperty("Driver")
    String driver,
    
    @Nullable
    @JsonProperty("Count")
    Integer count,
    
    @JsonProperty("DeviceIDs")
    List<String> deviceIds,
    
    @JsonProperty("Capabilities")
    List<List<String>> capabilities,
    
    @JsonProperty("Options")
    Map<String, String> options) {
    
    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private String driver;
      private Integer count;
      private List<String> deviceIds;
      private List<List<String>> capabilities;
      private Map<String, String> options;

      public Builder driver(final String driver) {
        this.driver = driver;
        return this;
      }

      public Builder count(final Integer count) {
        this.count = count;
        return this;
      }

      public Builder deviceIds(final List<String> deviceIds) {
        this.deviceIds = deviceIds;
        return this;
      }
      
      public Builder capabilities(final List<List<String>> capabilities) {
        this.capabilities = capabilities;
        return this;
      }
      
      public Builder options(final Map<String, String> options) {
        this.options = options != null ? Map.copyOf(options) : null;
        return this;
      }
      
      public DeviceRequest build() {
        return new DeviceRequest(driver, count, deviceIds, capabilities, options);
      }
    }
  }
}
