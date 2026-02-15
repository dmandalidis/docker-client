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

package org.mandas.docker.client.messages.swarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.Healthcheck;
import org.mandas.docker.client.messages.mount.Mount;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContainerSpec(
  @JsonProperty("Image") String image,
  @Nullable @JsonProperty("Hostname") String hostname,
  @JsonProperty("Labels") Map<String, String> labels,
  @Nullable @JsonProperty("Command") List<String> command,
  @Nullable @JsonProperty("Args") List<String> args,
  @Nullable @JsonProperty("Env") List<String> env,
  @Nullable @JsonProperty("Dir") String dir,
  @Nullable @JsonProperty("User") String user,
  @Nullable @JsonProperty("Groups") List<String> groups,
  @Nullable @JsonProperty("TTY") Boolean tty,
  @Nullable @JsonProperty("Mounts") List<Mount> mounts,
  @Nullable @JsonProperty("StopGracePeriod") Long stopGracePeriod,
  @Nullable @JsonProperty("Healthcheck") Healthcheck healthcheck,
  @Nullable @JsonProperty("Hosts") List<String> hosts,
  @Nullable @JsonProperty("Secrets") List<SecretBind> secrets,
  @Nullable @JsonProperty("Configs") List<ConfigBind> configs,
  @Nullable @JsonProperty("DNSConfig") DnsConfig dnsConfig,
  @JsonProperty("Sysctls") Map<String, String> sysctls,
  @Nullable @JsonProperty("Init") Boolean init
) {

  // Compact constructor to ensure non-nullable Maps are never null
  public ContainerSpec {
    if (labels == null) {
      labels = Map.of();
    }
    if (sysctls == null) {
      sysctls = Map.of();
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String image;
    private String hostname;
    private Map<String, String> labels = new HashMap<>();
    private List<String> command;
    private List<String> args;
    private List<String> env;
    private String dir;
    private String user;
    private List<String> groups;
    private Boolean tty;
    private List<Mount> mounts;
    private Long stopGracePeriod;
    private DnsConfig dnsConfig;
    private Healthcheck healthcheck;
    private List<String> hosts;
    private List<SecretBind> secrets;
    private List<ConfigBind> configs;
    private Boolean init;
    private Map<String, String> sysctls = new HashMap<>();

    public Builder image(String image) {
      this.image = image;
      return this;
    }

    public Builder addLabel(final String label, final String value) {
      this.labels.put(label, value);
      return this;
    }

    public Builder hostname(String hostname) {
      this.hostname = hostname;
      return this;
    }

    public Builder labels(Map<String, String> labels) {
      this.labels = labels != null ? new HashMap<>(labels) : new HashMap<>();
      return this;
    }

    public Builder command(String... commands) {
      this.command = List.of(commands);
      return this;
    }

    public Builder command(List<String> commands) {
      this.command = commands;
      return this;
    }

    public Builder args(String... args) {
      this.args = List.of(args);
      return this;
    }

    public Builder args(List<String> args) {
      this.args = args;
      return this;
    }

    public Builder env(String... env) {
      this.env = List.of(env);
      return this;
    }

    public Builder env(List<String> env) {
      this.env = env;
      return this;
    }

    public Builder dir(String dir) {
      this.dir = dir;
      return this;
    }

    public Builder user(String user) {
      this.user = user;
      return this;
    }

    public Builder groups(String... groups) {
      this.groups = List.of(groups);
      return this;
    }

    public Builder groups(List<String> groups) {
      this.groups = groups;
      return this;
    }

    public Builder tty(Boolean tty) {
      this.tty = tty;
      return this;
    }

    public Builder mounts(Mount... mounts) {
      this.mounts = List.of(mounts);
      return this;
    }

    public Builder mounts(List<Mount> mounts) {
      this.mounts = mounts;
      return this;
    }

    public Builder stopGracePeriod(Long stopGracePeriod) {
      this.stopGracePeriod = stopGracePeriod;
      return this;
    }

    public Builder dnsConfig(DnsConfig dnsConfig) {
      this.dnsConfig = dnsConfig;
      return this;
    }

    public Builder healthcheck(Healthcheck healthcheck) {
      this.healthcheck = healthcheck;
      return this;
    }

    public Builder hosts(List<String> hosts) {
      this.hosts = hosts;
      return this;
    }

    public Builder secrets(List<SecretBind> secrets) {
      this.secrets = secrets;
      return this;
    }

    public Builder configs(List<ConfigBind> configs) {
      this.configs = configs;
      return this;
    }

    public Builder init(Boolean init) {
      this.init = init;
      return this;
    }

    public Builder sysctls(Map<String, String> sysctls) {
      this.sysctls = sysctls != null ? new HashMap<>(sysctls) : new HashMap<>();
      return this;
    }

    public Builder addSysctl(String key, String value) {
      this.sysctls.put(key, value);
      return this;
    }

    public ContainerSpec build() {
      return new ContainerSpec(image, hostname, labels, command, args, env, dir, user, 
                              groups, tty, mounts, stopGracePeriod, healthcheck, hosts, 
                              secrets, configs, dnsConfig, sysctls, init);
    }
  }
}
