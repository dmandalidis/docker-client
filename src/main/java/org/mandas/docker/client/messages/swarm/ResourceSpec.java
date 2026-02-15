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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use=Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({
	@JsonSubTypes.Type(value = ResourceSpec.NamedResourceSpec.class, name = "NamedResourceSpec"),
	@JsonSubTypes.Type(value = ResourceSpec.DiscreteResourceSpec.class, name = "DiscreteResourceSpec"),
})
public sealed interface ResourceSpec permits ResourceSpec.NamedResourceSpec, ResourceSpec.DiscreteResourceSpec {
	
	@JsonProperty("Kind")
	String kind();
	
	@JsonTypeName("NamedResourceSpec")
	record NamedResourceSpec(
		@JsonProperty("Kind") String kind,
		@JsonProperty("Value") String value
	) implements ResourceSpec {
		
		public static Builder builder() {
			return new Builder();
		}
		
		public static class Builder {
			private String kind;
			private String value;
			
			public Builder kind(String kind) {
				this.kind = kind;
				return this;
			}
			
			public Builder value(String value) {
				this.value = value;
				return this;
			}
			
			public NamedResourceSpec build() {
				return new NamedResourceSpec(kind, value);
			}
		}
	}
	
	@JsonTypeName("DiscreteResourceSpec")
	record DiscreteResourceSpec(
		@JsonProperty("Kind") String kind,
		@JsonProperty("Value") int value
	) implements ResourceSpec {
		
		public static Builder builder() {
			return new Builder();
		}
		
		public static class Builder {
			private String kind;
			private int value;
			
			public Builder kind(String kind) {
				this.kind = kind;
				return this;
			}
			
			public Builder value(int value) {
				this.value = value;
				return this;
			}
			
			public DiscreteResourceSpec build() {
				return new DiscreteResourceSpec(kind, value);
			}
		}
	}
}
