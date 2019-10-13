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

package org.mandas.docker.client.messages.swarm;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.mandas.docker.client.messages.ContainerConfig;
import org.mandas.docker.client.messages.mount.Mount;

import java.util.List;
import java.util.Map;
import org.mandas.docker.Nullable;

@AutoValue
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public abstract class ContainerSpec {

  @JsonProperty("Image")
  public abstract String image();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Hostname")
  public abstract String hostname();

  @Nullable
  @JsonProperty("Labels")
  public abstract ImmutableMap<String, String> labels();

  @Nullable
  @JsonProperty("Command")
  public abstract ImmutableList<String> command();

  @Nullable
  @JsonProperty("Args")
  public abstract ImmutableList<String> args();

  @Nullable
  @JsonProperty("Env")
  public abstract ImmutableList<String> env();

  @Nullable
  @JsonProperty("Dir")
  public abstract String dir();

  @Nullable
  @JsonProperty("User")
  public abstract String user();

  @Nullable
  @JsonProperty("Groups")
  public abstract ImmutableList<String> groups();

  @Nullable
  @JsonProperty("TTY")
  public abstract Boolean tty();

  @Nullable
  @JsonProperty("Mounts")
  public abstract ImmutableList<Mount> mounts();

  @Nullable
  @JsonProperty("StopGracePeriod")
  public abstract Long stopGracePeriod();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Healthcheck")
  public abstract ContainerConfig.Healthcheck healthcheck();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Hosts")
  public abstract ImmutableList<String> hosts();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Secrets")
  public abstract ImmutableList<SecretBind> secrets();

  /**
   * @since API 1.30
   */
  @Nullable
  @JsonProperty("Configs")
  public abstract ImmutableList<ConfigBind> configs();

  @Nullable
  @JsonProperty("DNSConfig")
  public abstract DnsConfig dnsConfig();

  /**
   * @since 1.37
   */
  @Nullable
  @JsonProperty("Init")
  public abstract Boolean init();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder image(String image);

    abstract ImmutableMap.Builder<String, String> labelsBuilder();

    public Builder addLabel(final String label, final String value) {
      labelsBuilder().put(label, value);
      return this;
    }

    public abstract Builder hostname(String hostname);

    public abstract Builder labels(Map<String, String> labels);

    public abstract Builder command(String... commands);

    public abstract Builder command(List<String> commands);

    public abstract Builder args(String... args);

    public abstract Builder args(List<String> args);

    public abstract Builder env(String... env);

    public abstract Builder env(List<String> env);

    public abstract Builder dir(String dir);

    public abstract Builder user(String user);

    public abstract Builder groups(String... groups);

    public abstract Builder groups(List<String> groups);

    public abstract Builder tty(Boolean tty);

    public abstract Builder mounts(Mount... mounts);

    public abstract Builder mounts(List<Mount> mounts);

    public abstract Builder stopGracePeriod(Long stopGracePeriod);

    public abstract Builder dnsConfig(DnsConfig dnsConfig);

    public abstract Builder healthcheck(ContainerConfig.Healthcheck healthcheck);

    public abstract Builder hosts(List<String> hosts);

    public abstract Builder secrets(List<SecretBind> secrets);

    public abstract Builder configs(List<ConfigBind> configs);
    
    public abstract Builder init(Boolean init);

    public abstract ContainerSpec build();
  }

  public static ContainerSpec.Builder builder() {
    return new AutoValue_ContainerSpec.Builder();
  }

  @JsonCreator
  static ContainerSpec create(
      @JsonProperty("Image") final String image,
      @JsonProperty("Labels") final Map<String, String> labels,
      @JsonProperty("Hostname") final String hostname,
      @JsonProperty("Command") final List<String> command,
      @JsonProperty("Args") final List<String> args,
      @JsonProperty("Env") final List<String> env,
      @JsonProperty("Dir") final String dir,
      @JsonProperty("User") final String user,
      @JsonProperty("Groups") final List<String> groups,
      @JsonProperty("TTY") final Boolean tty,
      @JsonProperty("Mounts") final List<Mount> mounts,
      @JsonProperty("StopGracePeriod") final Long stopGracePeriod,
      @JsonProperty("Healthcheck") final ContainerConfig.Healthcheck healthcheck,
      @JsonProperty("Hosts") final List<String> hosts,
      @JsonProperty("Secrets") final List<SecretBind> secrets,
      @JsonProperty("DNSConfig") final DnsConfig dnsConfig,
      @JsonProperty("Configs") final List<ConfigBind> configs,
      @JsonProperty("Init") final Boolean init) {
    final Builder builder = builder()
        .image(image)
        .hostname(hostname)
        .args(args)
        .env(env)
        .dir(dir)
        .user(user)
        .groups(groups)
        .tty(tty)
        .mounts(mounts)
        .stopGracePeriod(stopGracePeriod)
        .healthcheck(healthcheck)
        .hosts(hosts)
        .dnsConfig(dnsConfig)
        .command(command)
        .secrets(secrets)
        .configs(configs)
        .init(init);

    if (labels != null) {
      builder.labels(labels);
    }

    return builder.build();
  }
}
