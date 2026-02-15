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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mandas.docker.Nullable;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record ContainerConfig(
  @Nullable @JsonProperty("Hostname") String hostname,
  @Nullable @JsonProperty("Domainname") String domainname,
  @Nullable @JsonProperty("User") String user,
  @Nullable @JsonProperty("AttachStdin") Boolean attachStdin,
  @Nullable @JsonProperty("AttachStdout") Boolean attachStdout,
  @Nullable @JsonProperty("AttachStderr") Boolean attachStderr,
  @Nullable @JsonProperty("PortSpecs") List<String> portSpecs,
  @Nullable @JsonProperty("ExposedPorts")
  @JsonSerialize(using=ObjectMapperProvider.SetSerializer.class)
  @JsonDeserialize(using=ObjectMapperProvider.SetDeserializer.class)
  Set<String> exposedPorts,
  @Nullable @JsonProperty("Tty") Boolean tty,
  @Nullable @JsonProperty("OpenStdin") Boolean openStdin,
  @Nullable @JsonProperty("StdinOnce") Boolean stdinOnce,
  @Nullable @JsonProperty("Env") List<String> env,
  @Nullable @JsonProperty("Cmd") List<String> cmd,
  @Nullable @JsonProperty("Image") String image,
  @Nullable @JsonProperty("Volumes")
  @JsonSerialize(using=ObjectMapperProvider.SetSerializer.class)
  @JsonDeserialize(using=ObjectMapperProvider.SetDeserializer.class)
  Set<String> volumes,
  @Nullable @JsonProperty("WorkingDir") String workingDir,
  @Nullable @JsonProperty("Entrypoint") List<String> entrypoint,
  @Nullable @JsonProperty("NetworkDisabled") Boolean networkDisabled,
  @Nullable @JsonProperty("OnBuild") List<String> onBuild,
  @Nullable @JsonProperty("Labels") Map<String, String> labels,
  @Deprecated @Nullable @JsonProperty("MacAddress") String macAddress,
  @Nullable @JsonProperty("HostConfig") HostConfig hostConfig,
  @Nullable @JsonProperty("StopSignal") String stopSignal,
  @Nullable @JsonProperty("Healthcheck") Healthcheck healthcheck,
  @Nullable @JsonProperty("NetworkingConfig") NetworkingConfig networkingConfig
) {

  @JsonIgnore
  public Builder toBuilder() {
    return new Builder()
        .hostname(hostname)
        .domainname(domainname)
        .user(user)
        .attachStdin(attachStdin)
        .attachStdout(attachStdout)
        .attachStderr(attachStderr)
        .portSpecs(portSpecs)
        .exposedPorts(exposedPorts)
        .tty(tty)
        .openStdin(openStdin)
        .stdinOnce(stdinOnce)
        .env(env)
        .cmd(cmd)
        .image(image)
        .volumes(volumes)
        .workingDir(workingDir)
        .entrypoint(entrypoint)
        .networkDisabled(networkDisabled)
        .onBuild(onBuild)
        .labels(labels)
        .macAddress(macAddress)
        .hostConfig(hostConfig)
        .stopSignal(stopSignal)
        .healthcheck(healthcheck)
        .networkingConfig(networkingConfig);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String hostname;
    private String domainname;
    private String user;
    private Boolean attachStdin;
    private Boolean attachStdout;
    private Boolean attachStderr;
    private List<String> portSpecs;
    private Set<String> exposedPorts;
    private Boolean tty;
    private Boolean openStdin;
    private Boolean stdinOnce;
    private List<String> env;
    private List<String> cmd;
    private String image;
    private Set<String> volumes;
    private String workingDir;
    private List<String> entrypoint;
    private Boolean networkDisabled;
    private List<String> onBuild;
    private Map<String, String> labels;
    private String macAddress;
    private HostConfig hostConfig;
    private String stopSignal;
    private Healthcheck healthcheck;
    private NetworkingConfig networkingConfig;

    public Builder hostname(final String hostname) {
      this.hostname = hostname;
      return this;
    }

    public Builder domainname(final String domainname) {
      this.domainname = domainname;
      return this;
    }

    public Builder user(final String user) {
      this.user = user;
      return this;
    }

    public Builder attachStdin(final Boolean attachStdin) {
      this.attachStdin = attachStdin;
      return this;
    }

    public Builder attachStdout(final Boolean attachStdout) {
      this.attachStdout = attachStdout;
      return this;
    }

    public Builder attachStderr(final Boolean attachStderr) {
      this.attachStderr = attachStderr;
      return this;
    }

    public Builder portSpecs(final List<String> portSpecs) {
      this.portSpecs = portSpecs;
      return this;
    }

    public Builder portSpecs(final String... portSpecs) {
      this.portSpecs = List.of(portSpecs);
      return this;
    }

    public Builder exposedPorts(final Set<String> exposedPorts) {
      this.exposedPorts = exposedPorts;
      return this;
    }

    public Builder exposedPorts(final String... exposedPorts) {
      this.exposedPorts = Set.of(exposedPorts);
      return this;
    }

    public Builder tty(final Boolean tty) {
      this.tty = tty;
      return this;
    }

    public Builder openStdin(final Boolean openStdin) {
      this.openStdin = openStdin;
      return this;
    }

    public Builder stdinOnce(final Boolean stdinOnce) {
      this.stdinOnce = stdinOnce;
      return this;
    }

    public Builder env(final List<String> env) {
      this.env = env;
      return this;
    }

    public Builder env(final String... env) {
      this.env = List.of(env);
      return this;
    }

    public Builder cmd(final List<String> cmd) {
      this.cmd = cmd;
      return this;
    }

    public Builder cmd(final String... cmds) {
      this.cmd = List.of(cmds);
      return this;
    }

    public Builder image(final String image) {
      this.image = image;
      return this;
    }

    public Builder addVolume(String volume) {
      if (this.volumes == null) {
        this.volumes = new HashSet<>();
      } else {
        this.volumes = new HashSet<>(this.volumes);
      }
      this.volumes.add(volume);
      return this;
    }

    public Builder addVolumes(String... volumes) {
      if (this.volumes == null) {
        this.volumes = new HashSet<>();
      } else {
        this.volumes = new HashSet<>(this.volumes);
      }
      this.volumes.addAll(Arrays.asList(volumes));
      return this;
    }

    public Builder volumes(final Set<String> volumes) {
      this.volumes = volumes;
      return this;
    }

    public Builder volumes(final String... volumes) {
      this.volumes = Set.of(volumes);
      return this;
    }

    public Builder workingDir(final String workingDir) {
      this.workingDir = workingDir;
      return this;
    }

    public Builder entrypoint(final List<String> entrypoint) {
      this.entrypoint = entrypoint;
      return this;
    }

    public Builder entrypoint(final String... entrypoint) {
      this.entrypoint = List.of(entrypoint);
      return this;
    }

    public Builder networkDisabled(final Boolean networkDisabled) {
      this.networkDisabled = networkDisabled;
      return this;
    }

    public Builder onBuild(final List<String> onBuild) {
      this.onBuild = onBuild;
      return this;
    }

    public Builder onBuild(final String... onBuild) {
      this.onBuild = List.of(onBuild);
      return this;
    }

    public Builder labels(final Map<String, String> labels) {
      this.labels = labels != null ? Map.copyOf(labels) : null;
      return this;
    }

    public Builder macAddress(final String macAddress) {
      this.macAddress = macAddress;
      return this;
    }

    public Builder hostConfig(final HostConfig hostConfig) {
      this.hostConfig = hostConfig;
      return this;
    }

    public Builder stopSignal(final String stopSignal) {
      this.stopSignal = stopSignal;
      return this;
    }

    public Builder healthcheck(final Healthcheck healthcheck) {
      this.healthcheck = healthcheck;
      return this;
    }

    public Builder networkingConfig(final NetworkingConfig networkingConfig) {
      this.networkingConfig = networkingConfig;
      return this;
    }

    public ContainerConfig build() {
      return new ContainerConfig(hostname, domainname, user, attachStdin, attachStdout, 
                                attachStderr, portSpecs, exposedPorts, tty, openStdin, 
                                stdinOnce, env, cmd, image, volumes, workingDir, entrypoint, 
                                networkDisabled, onBuild, labels, macAddress, hostConfig, 
                                stopSignal, healthcheck, networkingConfig);
    }
  }
}
