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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(builder = ImmutableImageConfig.Builder.class)
@Immutable
public interface ImageConfig {

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("Hostname")
  String hostname();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("Domainname")
  String domainname();

  @Nullable
  @JsonProperty("User")
  String user();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("AttachStdin")
  Boolean attachStdin();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("AttachStdout")
  Boolean attachStdout();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("AttachStderr")
  Boolean attachStderr();

  @Nullable
  @JsonProperty("PortSpecs")
  List<String> portSpecs();

  @Nullable
  @JsonProperty("ExposedPorts")
  @JsonSerialize(using=ObjectMapperProvider.SetSerializer.class)
  @JsonDeserialize(using=ObjectMapperProvider.SetDeserializer.class)
  Set<String> exposedPorts();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("Tty")
  Boolean tty();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("OpenStdin")
  Boolean openStdin();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("StdinOnce")
  Boolean stdinOnce();

  @Nullable
  @JsonProperty("Env")
  List<String> env();

  @Nullable
  @JsonProperty("Cmd")
  List<String> cmd();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("Image")
  String image();

  @Nullable
  @JsonProperty("Volumes")
  @JsonSerialize(using=ObjectMapperProvider.SetSerializer.class)
  @JsonDeserialize(using=ObjectMapperProvider.SetDeserializer.class)
  Set<String> volumes();

  @Nullable
  @JsonProperty("WorkingDir")
  String workingDir();

  @Nullable
  @JsonProperty("Entrypoint")
  List<String> entrypoint();

  @Deprecated // as of v1.46
  @Nullable
  @JsonProperty("NetworkDisabled")
  Boolean networkDisabled();

  @Nullable
  @JsonProperty("OnBuild")
  List<String> onBuild();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  @Deprecated // as of v1.44
  @Nullable
  @JsonProperty("MacAddress")
  String macAddress();

  @Nullable
  @JsonProperty("HostConfig")
  HostConfig hostConfig();

  @Nullable
  @JsonProperty("StopSignal")
  String stopSignal();

  @Nullable
  @JsonProperty("Healthcheck")
  Healthcheck healthcheck();

  @Nullable
  @JsonProperty("NetworkingConfig")
  NetworkingConfig networkingConfig();

  @JsonIgnore
  @Derived
  @Auxiliary
  default Builder toBuilder() {
	return ImmutableImageConfig.builder().from(this);
  }
  
  public static Builder builder() {
    return ImmutableImageConfig.builder();
  }

  public interface Builder {

    @Deprecated // as of v1.46
    Builder hostname(final String hostname);

    @Deprecated // as of v1.46
    Builder domainname(final String domainname);

    Builder user(final String user);

    @Deprecated // as of v1.46
    Builder attachStdin(final Boolean attachStdin);

    @Deprecated // as of v1.46
    Builder attachStdout(final Boolean attachStdout);

    @Deprecated // as of v1.46
    Builder attachStderr(final Boolean attachStderr);

    Builder portSpecs(final Iterable<String> portSpecs);

    Builder portSpecs(final String... portSpecs);

    Builder exposedPorts(final Iterable<String> exposedPorts);

    Builder exposedPorts(final String... exposedPorts);

    @Deprecated // as of v1.46
    Builder tty(final Boolean tty);

    @Deprecated // as of v1.46
    Builder openStdin(final Boolean openStdin);

    @Deprecated // as of v1.46
    Builder stdinOnce(final Boolean stdinOnce);

    Builder env(final Iterable<String> env);

    Builder env(final String... env);

    Builder cmd(final Iterable<String> cmd);

    Builder cmd(final String... cmds);

    @Deprecated // as of v1.46
    Builder image(final String image);

    default Builder addVolume(String volume) {
    	volumes(volume);
    	return this;
    }
    
    default Builder addVolumes(String... volumes) {
    	volumes(volumes);
    	return this;
    }

    Builder volumes(final Iterable<String> volumes);

    Builder volumes(final String... volumes);

    Builder workingDir(final String workingDir);

    Builder entrypoint(final Iterable<String> entrypoint);

    Builder entrypoint(final String... entrypoint);

    Builder networkDisabled(final Boolean networkDisabled);

    Builder onBuild(final Iterable<String> onBuild);

    Builder onBuild(final String... onBuild);

    Builder labels(final Map<String, ? extends String> labels);

    @Deprecated // as of v1.46
    Builder macAddress(final String macAddress);

    Builder hostConfig(final HostConfig hostConfig);

    Builder stopSignal(final String stopSignal);

    Builder healthcheck(final Healthcheck healthcheck);

    Builder networkingConfig(final NetworkingConfig networkingConfig);

    ImageConfig build();
  }
}
