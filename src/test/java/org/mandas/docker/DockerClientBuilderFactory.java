/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2019-2020 Dimitris Mandalidis
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
package org.mandas.docker;

import static java.lang.System.getProperty;

import org.mandas.docker.client.builder.DockerClientBuilder;
import org.mandas.docker.client.builder.jersey.JerseyDockerClientBuilder;
import org.mandas.docker.client.builder.resteasy.ResteasyDockerClientBuilder;

public class DockerClientBuilderFactory {

  public static final String JAXRS_CLIENT_PROPERTY = "jaxrs.client";
  
  private DockerClientBuilderFactory() {}

  public static DockerClientBuilder newInstance() {
    if ("resteasy".equals(getProperty(JAXRS_CLIENT_PROPERTY))) {
      return new ResteasyDockerClientBuilder();
    }
    return new JerseyDockerClientBuilder();
  }
}
