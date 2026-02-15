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

package org.mandas.docker.client;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Produces(MediaType.APPLICATION_JSON)
@SuppressWarnings({"rawtypes"})
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

  private static final Logger log = LoggerFactory.getLogger(ObjectMapperProvider.class);

  private static final SimpleModule MODULE = new SimpleModule();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    try {
      MODULE.addSerializer(Set.class, new SetSerializer());
      MODULE.addDeserializer(Set.class, new SetDeserializer());
      OBJECT_MAPPER.registerModule(MODULE);
      OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      OBJECT_MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    } catch (Exception t) {
      log.error("Failure during static initialization", t);
      throw t;
    }
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return OBJECT_MAPPER;
  }

  public static ObjectMapper objectMapper() {
    return OBJECT_MAPPER;
  }

  public static class SetSerializer extends JsonSerializer<Set> {

    @Override
    public void serialize(final Set value, final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException {
      final Map map = (value == null) ? null : (Map) value.stream().collect(toMap(s -> s, s -> emptyMap()));
      OBJECT_MAPPER.writeValue(jgen, map);
    }
  }

  public static class SetDeserializer extends JsonDeserializer<Set> {

    @Override
    public Set<?> deserialize(final JsonParser jp, final DeserializationContext ctxt)
        throws IOException {
      final Map map = OBJECT_MAPPER.readValue(jp, Map.class);
      return (map == null) ? null : map.keySet();
    }
  }
}
