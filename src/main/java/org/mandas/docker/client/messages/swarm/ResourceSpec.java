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
package org.mandas.docker.client.messages.swarm;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.client.messages.swarm.ImmutableResourceSpec.NamedResourceSpec;
import org.mandas.docker.client.messages.swarm.ResourceSpec.DiscreteResourceSpec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Enclosing
@JsonTypeInfo(use=Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({
	@JsonSubTypes.Type(value = NamedResourceSpec.class, name = "NamedResourceSpec"),
	@JsonSubTypes.Type(value = DiscreteResourceSpec.class, name = "DiscreteResourceSpec"),
})
public interface ResourceSpec {
	
	@JsonProperty("Kind")
	String kind();
	
	@JsonDeserialize(builder = ImmutableResourceSpec.NamedResourceSpec.Builder.class)
	@Immutable
	@JsonTypeName("NamedResourceSpec")
	interface NamedResourceSpec extends ResourceSpec {
		@JsonProperty("Value")
		String value();
		
		interface Builder extends ResourceSpec.Builder<Builder> {
			Builder value(String value);
			NamedResourceSpec build();
		}
		
		static Builder builder() {
			return ImmutableResourceSpec.NamedResourceSpec.builder();
		}
	}
	
	@JsonDeserialize(builder = ImmutableResourceSpec.DiscreteResourceSpec.Builder.class)
	@Immutable
	@JsonTypeName("DiscreteResourceSpec")
	interface DiscreteResourceSpec extends ResourceSpec {
		@JsonProperty("Value")
		int value();
		
		interface Builder extends ResourceSpec.Builder<Builder> {
			Builder value(int value);
			DiscreteResourceSpec build();
		}
		
		static Builder builder() {
			return ImmutableResourceSpec.DiscreteResourceSpec.builder();
		}
	}
	
	interface Builder<T> {
		T kind(String kind);
	}
}
