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
import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;
import org.mandas.docker.client.ObjectMapperProvider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(builder = ImmutableContainerConfig.Builder.class)
@Immutable
@Enclosing
public interface ContainerConfig {

  @Nullable
  @JsonProperty("Hostname")
  String hostname();

  @Nullable
  @JsonProperty("Domainname")
  String domainname();

  @Nullable
  @JsonProperty("User")
  String user();

  @Nullable
  @JsonProperty("AttachStdin")
  Boolean attachStdin();

  @Nullable
  @JsonProperty("AttachStdout")
  Boolean attachStdout();

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

  @Nullable
  @JsonProperty("Tty")
  Boolean tty();

  @Nullable
  @JsonProperty("OpenStdin")
  Boolean openStdin();

  @Nullable
  @JsonProperty("StdinOnce")
  Boolean stdinOnce();

  @Nullable
  @JsonProperty("Env")
  List<String> env();

  @Nullable
  @JsonProperty("Cmd")
  List<String> cmd();

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
	return ImmutableContainerConfig.builder().from(this);
  }
  
  public static Builder builder() {
    return ImmutableContainerConfig.builder();
  }

  public interface Builder {

    Builder hostname(final String hostname);

    Builder domainname(final String domainname);

    Builder user(final String user);

    Builder attachStdin(final Boolean attachStdin);

    Builder attachStdout(final Boolean attachStdout);

    Builder attachStderr(final Boolean attachStderr);

    Builder portSpecs(final Iterable<String> portSpecs);

    Builder portSpecs(final String... portSpecs);

    Builder exposedPorts(final Iterable<String> exposedPorts);

    Builder exposedPorts(final String... exposedPorts);

    Builder tty(final Boolean tty);

    Builder openStdin(final Boolean openStdin);

    Builder stdinOnce(final Boolean stdinOnce);

    Builder env(final Iterable<String> env);

    Builder env(final String... env);

    Builder cmd(final Iterable<String> cmd);

    Builder cmd(final String... cmds);

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

    Builder macAddress(final String macAddress);

    Builder hostConfig(final HostConfig hostConfig);

    Builder stopSignal(final String stopSignal);

    Builder healthcheck(final Healthcheck healthcheck);

    Builder networkingConfig(final NetworkingConfig networkingConfig);

    ContainerConfig build();
  }

  @JsonDeserialize(builder = ImmutableContainerConfig.Healthcheck.Builder.class)
  @Immutable
  public interface Healthcheck {
    @Nullable
    @JsonProperty("Test")
    List<String> test();

    /**
     * @return interval in nanoseconds.
     */
    @Nullable
    @JsonProperty("Interval")
    Long interval();

    /**
     * @return timeout in nanoseconds.
     */
    @Nullable
    @JsonProperty("Timeout")
    Long timeout();

    @Nullable
    @JsonProperty("Retries")
    Integer retries();

    /**
     * @return start period in nanoseconds.
     * @since API 1.29
     */
    @Nullable
    @JsonProperty("StartPeriod")
    Long startPeriod();

    static Healthcheck create(
            final List<String> test,
            final Long interval,
            final Long timeout,
            final Integer retries) {
      return create(test, interval, timeout, retries, null);
    }

    static Healthcheck create(
            final List<String> test,
            final Long interval,
            final Long timeout,
            final Integer retries,
            final Long startPeriod) {
      return builder()
          .test(test)
          .interval(interval)
          .timeout(timeout)
          .retries(retries)
          .startPeriod(startPeriod)
          .build();
    }
    
    public static Builder builder() {
      return ImmutableContainerConfig.Healthcheck.builder();
    }

    interface Builder {
      Builder test(final Iterable<String> test);

      Builder interval(final Long interval);

      Builder timeout(final Long timeout);

      Builder retries(final Integer retries);

      Builder startPeriod(final Long startPeriod);

      Healthcheck build();
    }
  }

  @JsonDeserialize(builder = ImmutableContainerConfig.NetworkingConfig.Builder.class)
  @Immutable
  public interface NetworkingConfig {
    @JsonProperty("EndpointsConfig")
    Map<String, EndpointConfig> endpointsConfig();
    
    interface Builder {
      Builder endpointsConfig(Map<String, ? extends EndpointConfig> endpointsConfig);
      NetworkingConfig build();
    }
    
    public static Builder builder() {
      return ImmutableContainerConfig.NetworkingConfig.builder();
    }
  }
}
