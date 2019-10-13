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

import java.util.List;
import java.util.Map;

import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableServiceSpec.Builder.class)
@Immutable
public interface ServiceSpec {

  @Nullable
  @JsonProperty("Name")
  String name();

  @Nullable
  @JsonProperty("Labels")
  Map<String, String> labels();

  @JsonProperty("TaskTemplate")
  TaskSpec taskTemplate();

  @Nullable
  @JsonProperty("Mode")
  ServiceMode mode();

  @Nullable
  @JsonProperty("UpdateConfig")
  UpdateConfig updateConfig();

  @Nullable
  @JsonProperty("Networks")
  List<NetworkAttachmentConfig> networks();

  @Nullable
  @JsonProperty("EndpointSpec")
  EndpointSpec endpointSpec();

  interface Builder {

    Builder name(String name);

    Builder addLabel(final String label, final String value);

    Builder labels(Map<String, ? extends String> labels);

    Builder taskTemplate(TaskSpec taskTemplate);

    Builder mode(ServiceMode mode);

    Builder updateConfig(UpdateConfig updateConfig);

    Builder networks(NetworkAttachmentConfig... networks);

    Builder networks(Iterable<? extends NetworkAttachmentConfig> networks);

    Builder endpointSpec(EndpointSpec endpointSpec);

    ServiceSpec build();
  }

  public static Builder builder() {
    return ImmutableServiceSpec.builder();
  }

}
