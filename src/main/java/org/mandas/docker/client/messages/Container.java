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

import java.util.List;
import java.util.Map;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@JsonDeserialize(builder = ImmutableContainer.Builder.class)
@Enclosing
public interface Container {

  @JsonProperty("Id")
  String id();

  @Nullable
  @JsonProperty("Names")
  List<String> names();

  @JsonProperty("Image")
  String image();

  @Nullable
  @JsonProperty("ImageID")
  String imageId();

  @JsonProperty("Command")
  String command();

  @JsonProperty("Created")
  Long created();

  @Nullable
  @JsonProperty("State")
  String state();

  @JsonProperty("Status")
  String status();

  @Nullable
  @JsonProperty("Ports")
  List<PortMapping> ports();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  @Nullable
  @JsonProperty("SizeRw")
  Long sizeRw();

  @Nullable
  @JsonProperty("SizeRootFs")
  Long sizeRootFs();

  @Nullable
  @JsonProperty("NetworkSettings")
  NetworkSettings networkSettings();

  @Nullable
  @JsonProperty("Mounts")
  List<ContainerMount> mounts();

  /**
   * Returns port information the way that <code>docker ps</code> does.
   * <code>0.0.0.0:5432-&gt;5432/tcp</code> or <code>6379/tcp</code>.
   *
   * <p>It should not be used to extract detailed information of ports. To do so, please refer to
   * {@link org.mandas.docker.client.messages.PortBinding}.
   *
   * @return port information as docker ps does.
   * @see org.mandas.docker.client.messages.PortBinding
   */
  @JsonIgnore
  @Derived
  public default String portsAsString() {
    final StringBuilder sb = new StringBuilder();
    if (ports() != null) {
      for (final PortMapping port : ports()) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        if (port.ip() != null) {
          sb.append(port.ip()).append(":");
        }
        if (port.publicPort() > 0) {
          sb.append(port.privatePort()).append("->").append(port.publicPort());
        } else {
          sb.append(port.privatePort());
        }
        sb.append("/").append(port.type());
      }
    }

    return sb.toString();
  }

  @JsonDeserialize(builder = ImmutableContainer.PortMapping.Builder.class)
  @Immutable
  public interface PortMapping {

    @JsonProperty("PrivatePort")
    @Default
    default int privatePort() {
    	return 0;
    }

    @JsonProperty("PublicPort")
    @Default
    default int publicPort() {
    	return 0;
    }

    @JsonProperty("Type")
    String type();

    @Nullable
    @JsonProperty("IP")
    String ip();
  }
}
