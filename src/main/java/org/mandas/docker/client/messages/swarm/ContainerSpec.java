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

package org.mandas.docker.client.messages.swarm;

import java.util.List;
import java.util.Map;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.messages.ContainerConfig;
import org.mandas.docker.client.messages.mount.Mount;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableContainerSpec.Builder.class)
@Immutable
public interface ContainerSpec {

  @JsonProperty("Image")
  String image();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Hostname")
  String hostname();

  @JsonProperty("Labels")
  Map<String, String> labels();

  @Nullable
  @JsonProperty("Command")
  List<String> command();

  @Nullable
  @JsonProperty("Args")
  List<String> args();

  @Nullable
  @JsonProperty("Env")
  List<String> env();

  @Nullable
  @JsonProperty("Dir")
  String dir();

  @Nullable
  @JsonProperty("User")
  String user();

  @Nullable
  @JsonProperty("Groups")
  List<String> groups();

  @Nullable
  @JsonProperty("TTY")
  Boolean tty();

  @Nullable
  @JsonProperty("Mounts")
  List<Mount> mounts();

  @Nullable
  @JsonProperty("StopGracePeriod")
  Long stopGracePeriod();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Healthcheck")
  ContainerConfig.Healthcheck healthcheck();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Hosts")
  List<String> hosts();

  /**
   * @since API 1.26
   */
  @Nullable
  @JsonProperty("Secrets")
  List<SecretBind> secrets();

  /**
   * @since API 1.30
   */
  @Nullable
  @JsonProperty("Configs")
  List<ConfigBind> configs();

  @Nullable
  @JsonProperty("DNSConfig")
  DnsConfig dnsConfig();
  
  @JsonProperty("Sysctls")
  Map<String, String> sysctls();

  /**
   * @since 1.37
   */
  @Nullable
  @JsonProperty("Init")
  Boolean init();

  interface Builder {

    Builder image(String image);

    Builder addLabel(final String label, final String value);

    Builder hostname(String hostname);

    Builder labels(Map<String, ? extends String> labels);

    Builder command(String... commands);

    Builder command(Iterable<String> commands);

    Builder args(String... args);

    Builder args(Iterable<String> args);

    Builder env(String... env);

    Builder env(Iterable<String> env);

    Builder dir(String dir);

    Builder user(String user);

    Builder groups(String... groups);

    Builder groups(Iterable<String> groups);

    Builder tty(Boolean tty);

    Builder mounts(Mount... mounts);

    Builder mounts(Iterable<? extends Mount> mounts);

    Builder stopGracePeriod(Long stopGracePeriod);

    Builder dnsConfig(DnsConfig dnsConfig);

    Builder healthcheck(ContainerConfig.Healthcheck healthcheck);

    Builder hosts(Iterable<String> hosts);

    Builder secrets(Iterable<? extends SecretBind> secrets);

    Builder configs(Iterable<? extends ConfigBind> configs);
    
    Builder init(Boolean init);
    
    Builder sysctls(Map<String, ? extends String> sysctls);
    
    Builder addSysctl(String key, String value);

    ContainerSpec build();
  }

  public static Builder builder() {
    return ImmutableContainerSpec.builder();
  }
}
