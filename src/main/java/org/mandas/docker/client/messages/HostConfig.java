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

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Check;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.mount.Mount;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableHostConfig.Builder.class)
@Immutable
@Enclosing
public interface HostConfig {

  @Nullable
  @JsonProperty("Binds")
  List<String> binds();

  @Nullable
  @JsonProperty("BlkioWeight")
  Integer blkioWeight();

  @Nullable
  @JsonProperty("BlkioWeightDevice")
  List<BlkioWeightDevice> blkioWeightDevice();

  @Nullable
  @JsonProperty("BlkioDeviceReadBps")
  List<BlkioDeviceRate> blkioDeviceReadBps();

  @Nullable
  @JsonProperty("BlkioDeviceWriteBps")
  List<BlkioDeviceRate> blkioDeviceWriteBps();

  @Nullable
  @JsonProperty("BlkioDeviceReadIOps")
  List<BlkioDeviceRate> blkioDeviceReadIOps();

  @Nullable
  @JsonProperty("BlkioDeviceWriteIOps")
  List<BlkioDeviceRate> blkioDeviceWriteIOps();

  @Nullable
  @JsonProperty("ContainerIDFile")
  String containerIdFile();

  @Nullable
  @JsonProperty("LxcConf")
  List<LxcConfParameter> lxcConf();

  @Nullable
  @JsonProperty("Privileged")
  Boolean privileged();

  @Nullable
  @JsonProperty("PortBindings")
  Map<String, List<PortBinding>> portBindings();

  @Nullable
  @JsonProperty("Links")
  List<String> links();

  @Nullable
  @JsonProperty("PublishAllPorts")
  Boolean publishAllPorts();

  @Nullable
  @JsonProperty("Dns")
  List<String> dns();

  @Nullable
  @JsonProperty("DnsOptions")
  List<String> dnsOptions();

  @Nullable
  @JsonProperty("DnsSearch")
  List<String> dnsSearch();

  @Nullable
  @JsonProperty("ExtraHosts")
  List<String> extraHosts();
  
  @Nullable
  @JsonProperty("GroupAdd")
  List<String> groupAdd();
  
  @Nullable
  @JsonProperty("VolumesFrom")
  List<String> volumesFrom();

  @Nullable
  @JsonProperty("CapAdd")
  List<String> capAdd();

  @Nullable
  @JsonProperty("CapDrop")
  List<String> capDrop();

  @Nullable
  @JsonProperty("NetworkMode")
  String networkMode();

  @Nullable
  @JsonProperty("SecurityOpt")
  List<String> securityOpt();

  @Nullable
  @JsonProperty("Devices")
  List<Device> devices();

  @Nullable
  @JsonProperty("Memory")
  Long memory();

  @Nullable
  @JsonProperty("MemorySwap")
  Long memorySwap();

  @Nullable
  @JsonProperty("KernelMemory")
  @Deprecated // as of 20.10.0
  Long kernelMemory();

  @Nullable
  @JsonProperty("MemorySwappiness")
  Integer memorySwappiness();

  @Nullable
  @JsonProperty("MemoryReservation")
  Long memoryReservation();

  @Nullable
  @JsonProperty("NanoCpus")
  Long nanoCpus();

  @Nullable
  @JsonProperty("CpuPeriod")
  Long cpuPeriod();

  @Nullable
  @JsonProperty("CpuShares")
  Long cpuShares();

  @Nullable
  @JsonProperty("CpusetCpus")
  String cpusetCpus();

  @Nullable
  @JsonProperty("CpusetMems")
  String cpusetMems();

  @Nullable
  @JsonProperty("CpuQuota")
  Long cpuQuota();

  @Nullable
  @JsonProperty("CgroupParent")
  String cgroupParent();

  @Nullable
  @JsonProperty("RestartPolicy")
  RestartPolicy restartPolicy();

  @Nullable
  @JsonProperty("LogConfig")
  LogConfig logConfig();

  @Nullable
  @JsonProperty("IpcMode")
  String ipcMode();

  @Nullable
  @JsonProperty("Ulimits")
  List<Ulimit> ulimits();

  @Nullable
  @JsonProperty("PidMode")
  String pidMode();

  @Nullable
  @JsonProperty("ShmSize")
  Long shmSize();

  @Nullable
  @JsonProperty("OomKillDisable")
  Boolean oomKillDisable();

  @Nullable
  @JsonProperty("OomScoreAdj")
  Integer oomScoreAdj();

  @Nullable
  @JsonProperty("AutoRemove")
  Boolean autoRemove();

  /**
   * Tune container pids limit (set -1 for unlimited).
   * Only works for kernels &gt;= 4.3
   * @return An integer indicating the pids limit.
   */
  @Nullable
  @JsonProperty("PidsLimit")
  Integer pidsLimit();

  @Nullable
  @JsonProperty("Tmpfs")
  Map<String, String> tmpfs();

  @Nullable
  @JsonProperty("ReadonlyRootfs")
  Boolean readonlyRootfs();
  
  @Nullable
  @JsonProperty("StorageOpt")
  Map<String, String> storageOpt();

  @Nullable
  @JsonProperty("Runtime")
  String runtime();
  
  @Nullable
  @JsonProperty("Mounts")
  List<Mount> mounts();

  @Nullable
  @JsonProperty("Init")
  Boolean init();
  
  @JsonProperty("Sysctls")
  Map<String, String> sysctls();
  
  @Nullable
  @JsonProperty("Capabilities")
  List<String> capabilities();
  
  @Nullable
  @JsonProperty("DeviceRequests")
  List<DeviceRequest> deviceRequests();
  
  @JsonDeserialize(builder = ImmutableHostConfig.LxcConfParameter.Builder.class)
  @Immutable
  public interface LxcConfParameter {

    @JsonProperty("Key")
    String key();

    @JsonProperty("Value")
    String value();
  }

  @JsonDeserialize(builder = ImmutableHostConfig.RestartPolicy.Builder.class)
  @Immutable
  public interface RestartPolicy {

    @JsonProperty("Name")
    String name();

    @Nullable
    @JsonProperty("MaximumRetryCount")
    Integer maxRetryCount();

    public static RestartPolicy always() {
      return ImmutableHostConfig.RestartPolicy.builder().name("always").build();
    }

    public static RestartPolicy unlessStopped() {
      return ImmutableHostConfig.RestartPolicy.builder().name("unless-stopped").build();
    }

    public static RestartPolicy onFailure(Integer maxRetryCount) {
      return ImmutableHostConfig.RestartPolicy.builder().name("on-failure").maxRetryCount(maxRetryCount).build();
    }
  }

  @JsonIgnore
  @Derived
  @Auxiliary
  default Builder toBuilder() {
	return ImmutableHostConfig.builder().from(this);
  }

  public static Builder builder() {
    return ImmutableHostConfig.builder();
  }
  
  @Check
  default void check() {
    if (extraHosts() != null) {
	    for (final String extraHost : extraHosts()) {
	      if (!extraHost.contains(":")) {
	    	 throw new IllegalArgumentException(format("extra host arg '%s' must contain a ':'", extraHost));
	      }
	    }
	  }
  }

  interface Builder {

    Builder binds(Iterable<String> binds);

    Builder binds(String... binds);
    
    default Builder binds(Bind... binds) {
    	List<String> bindsValue = stream(binds).map(Bind::representation).collect(toList());
		return binds(bindsValue.toArray(new String[bindsValue.size()]));
    }

    Builder blkioWeight(Integer blkioWeight);

    Builder blkioWeightDevice(Iterable<? extends BlkioWeightDevice> blkioWeightDevice);

    Builder blkioDeviceReadBps(Iterable<? extends BlkioDeviceRate> blkioDeviceReadBps);

    Builder blkioDeviceWriteBps(Iterable<? extends BlkioDeviceRate> blkioDeviceWriteBps);

    Builder blkioDeviceReadIOps(Iterable<? extends BlkioDeviceRate> blkioDeviceReadIOps);

    Builder blkioDeviceWriteIOps(Iterable<? extends BlkioDeviceRate> blkioDeviceWriteIOps);

    Builder containerIdFile(String containerIdFile);

    Builder lxcConf(Iterable<? extends LxcConfParameter> lxcConf);

    Builder lxcConf(LxcConfParameter... lxcConf);

    Builder privileged(Boolean privileged);

    Builder portBindings(Map<String, ? extends List<PortBinding>> portBindings);

    Builder links(Iterable<String> links);

    Builder links(String... links);

    Builder publishAllPorts(Boolean publishAllPorts);

    Builder dns(Iterable<String> dns);

    Builder dns(String... dns);

    Builder dnsOptions(Iterable<String> dnsOptions);

    Builder dnsOptions(String... dnsOptions);

    Builder dnsSearch(Iterable<String> dnsSearch);

    Builder dnsSearch(String... dnsSearch);

    Builder extraHosts(Iterable<String> extraHosts);

    Builder extraHosts(String... extraHosts);
    
    Builder groupAdd(Iterable<String> groupAdd);

    Builder groupAdd(String... groupAdd);

    Builder volumesFrom(Iterable<String> volumesFrom);

    Builder volumesFrom(String... volumesFrom);

    Builder capAdd(Iterable<String> capAdd);

    Builder capAdd(String... capAdd);

    Builder capDrop(Iterable<String> capDrop);

    Builder capDrop(String... capDrop);

    Builder networkMode(String networkMode);

    Builder securityOpt(Iterable<String> securityOpt);

    Builder securityOpt(String... securityOpt);

    Builder devices(Iterable<? extends Device> devices);

    Builder devices(Device... devices);

    Builder memory(Long memory);

    Builder memorySwap(Long memorySwap);

    Builder memorySwappiness(Integer memorySwappiness);
    
    @Deprecated // as of 20.10.0
    Builder kernelMemory(Long kernelMemory);

    Builder memoryReservation(Long memoryReservation);

    Builder nanoCpus(Long nanoCpus);

    Builder cpuPeriod(Long cpuPeriod);

    Builder cpuShares(Long cpuShares);

    Builder cpusetCpus(String cpusetCpus);

    Builder cpusetMems(String cpusetMems);

    Builder cpuQuota(Long cpuQuota);

    Builder cgroupParent(String cgroupParent);

    Builder restartPolicy(RestartPolicy restartPolicy);

    Builder logConfig(LogConfig logConfig);

    Builder ipcMode(String ipcMode);

    Builder ulimits(Iterable<? extends Ulimit> ulimits);

    Builder pidMode(String pidMode);

    /**
     * Set the PID (Process) Namespace mode for the container.
     * Use this method to join another container's PID namespace. To use the host
     * PID namespace, use {@link #hostPidMode()}.
     *
     * @param container Join the namespace of this container (Name or ID)
     * @return Builder
     */
    default Builder containerPidMode(final String container) {
      pidMode("container:" + container);
      return this;
    }

    /**
     * Set the PID (Process) Namespace mode for the container.
     * Use this method to use the host's PID namespace. To use another container's
     * PID namespace, use {@link #containerPidMode(String)}.
     *
     * @return {@link Builder}
     */
    default Builder hostPidMode() {
      pidMode("host");
      return this;
    }

    Builder shmSize(Long shmSize);

    Builder oomKillDisable(Boolean oomKillDisable);

    Builder oomScoreAdj(Integer oomScoreAdj);

    /**
     * Only works for Docker API version &gt;= 1.25.
     * @param autoRemove Whether to automatically remove the container when it exits
     * @return {@link Builder}
     */
    Builder autoRemove(Boolean autoRemove);

    Builder pidsLimit(Integer pidsLimit);

    Builder tmpfs(Map<String, ? extends String> tmpfs);

    Builder readonlyRootfs(Boolean readonlyRootfs);
    
    Builder storageOpt(Map<String, ? extends String> tmpfs);

    Builder runtime(String runtime);

    Builder mounts(final Iterable<? extends Mount> mounts);

    Builder mounts(final Mount... mounts);
    
    Builder init(Boolean init);
    
    Builder sysctls(Map<String, ? extends String> sysctls);
    
    Builder addSysctl(String key, String value);
    
    Builder capabilities(Iterable<String> capabilities);
    
    Builder deviceRequests(Iterable<? extends DeviceRequest> deviceRequests);
    
    HostConfig build();
  }

  @JsonDeserialize(builder = ImmutableHostConfig.Bind.Builder.class)
  @Immutable
  public interface Bind {

    String to();

    String from();

    @Value.Default
    default boolean readOnly() {
    	return false;
    }

    @Nullable
    Boolean noCopy();

    @Nullable
    Boolean selinuxLabeling();

    static Builder builder() {
      return ImmutableHostConfig.Bind.builder();
    }

    @Derived
    @JsonIgnore
	default String representation() {
      if (to() == null || "".equals(to().trim())) {
        return "";
      } else if (from() == null || "".equals(from().trim())) {
        return to();
      }

      final String bind = from() + ":" + to();

      final List<String> options = new ArrayList<>();
      if (readOnly()) {
        options.add("ro");
      }
      //noinspection ConstantConditions
      if (noCopy() != null && noCopy()) {
        options.add("nocopy");
      }

      if (selinuxLabeling() != null) {
        // shared
        if (Boolean.TRUE.equals(selinuxLabeling())) {
          options.add("z");
        } else {
          options.add("Z");
        }
      }

      final String optionsValue = options.stream().collect(joining(","));

      return (optionsValue.isEmpty()) ? bind : bind + ":" + optionsValue;
    }

    interface Builder {

      Builder to(String to);

      Builder from(String from);

      Builder readOnly(boolean readOnly);

      Builder noCopy(Boolean noCopy);

      /**
       * Turn on automatic SELinux labeling of the host file or directory being
       * mounted into the container.
       * @param sharedContent True if this bind mount content is shared among multiple 
       *     containers (mount option "z"); false if private and unshared (mount option "Z")
       * @return {@link Builder}
       */
      Builder selinuxLabeling(Boolean sharedContent);

      Bind build();
    }
  }

  @JsonDeserialize(builder = ImmutableHostConfig.Ulimit.Builder.class)
  @Immutable
  public interface Ulimit {

    @JsonProperty("Name")
    String name();

    @JsonProperty("Soft")
    Long soft();

    @JsonProperty("Hard")
    Long hard();

    public static Builder builder() {
      return ImmutableHostConfig.Ulimit.builder();
    }

    interface Builder {
      Builder name(String name);

      Builder soft(Long soft);

      Builder hard(Long hard);

      Ulimit build();
    }
  }

  @JsonDeserialize(builder = ImmutableHostConfig.BlkioWeightDevice.Builder.class)
  @Immutable
  public interface BlkioWeightDevice {

    @JsonProperty("Path")
    String path();

    @JsonProperty("Weight")
    Integer weight();

    public static Builder builder() {
      return ImmutableHostConfig.BlkioWeightDevice.builder();
    }

    interface Builder {

      Builder path(final String path);

      Builder weight(final Integer weight);

      BlkioWeightDevice build();
    }
  }

  @JsonDeserialize(builder = ImmutableHostConfig.BlkioDeviceRate.Builder.class)
  @Immutable
  public interface  BlkioDeviceRate {

    @JsonProperty("Path")
    String path();

    @JsonProperty("Rate")
    Integer rate();

    public static Builder builder() {
      return ImmutableHostConfig.BlkioDeviceRate.builder();
    }

    interface Builder {

      Builder path(final String path);

      Builder rate(final Integer rate);

      BlkioDeviceRate build();
    }
  }
  
  @JsonDeserialize(builder = ImmutableHostConfig.DeviceRequest.Builder.class)
  @Immutable
  public interface DeviceRequest {
	  
	  @JsonProperty("Driver")
	  String driver();
	  
	  @JsonProperty("Count")
	  Integer count();
	  
	  @JsonProperty("DeviceIDs")
	  List<String> deviceIds();
	  
	  @JsonProperty("Capabilities")
	  List<String> capabilities();
	  
	  @JsonProperty("Options")
	  Map<String, String> options();
	  
	  public static Builder builder() {
	    return ImmutableHostConfig.DeviceRequest.builder();
      }

      interface Builder {
        Builder driver(final String driver);

        Builder count(final Integer count);

        Builder deviceIds(final Iterable<String> deviceIds);
        
        Builder capabilities(final Iterable<String> capabilities);
        
        Builder options(final Map<String, ? extends String> options);
        
        DeviceRequest build();
    }
  }
}
