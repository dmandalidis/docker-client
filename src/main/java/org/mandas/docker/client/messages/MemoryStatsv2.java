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

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableMemoryStats.Builder.class)
@Immutable
@Enclosing
public interface MemoryStatsv2 {

  @Nullable
  @JsonProperty("stats")
  Stats stats();

  @Nullable
  @JsonProperty("max_usage")
  Long maxUsage();

  @Nullable
  @JsonProperty("usage")
  Long usage();

  @Nullable
  @JsonProperty("failcnt")
  Long failcnt();

  @Nullable
  @JsonProperty("limit")
  Long limit();

  @JsonDeserialize(builder = ImmutableMemoryStats.Stats.Builder.class)
  @Immutable
  public interface Stats {

    @JsonProperty("active_anon")
    Long active_anon();

    @JsonProperty("active_file")
    Long activeFile();

    @JsonProperty("anon")
    Long anon();

    @JsonProperty("anon_thp")
    Long anon_thp();

    @JsonProperty("file")
    Long file();

    @JsonProperty("file_dirty")
    Long file_dirty();

    @JsonProperty("file_mapped")
    Long file_mapped();

    @JsonProperty("file_writeback")
    Long file_writeback();

    @JsonProperty("inactive_anon")
    Long inactive_anon();

    @JsonProperty("inactive_file")
    Long inactive_file();

    @JsonProperty("kernel_stack")
    Long kernel_stack();

    @JsonProperty("pgactivate")
    Long pgactivate();

    @JsonProperty("pgdeactivate")
    Long pgdeactivate();

    @JsonProperty("pgfault")
    Long pgfault();

    @JsonProperty("pglazyfree")
    Long pglazyfree();

    @JsonProperty("pglazyfreed")
    Long pglazyfreed();

    @JsonProperty("pgmajfault")
    Long pgmajfault();

    @JsonProperty("pgrefill")
    Long pgrefill();

    @JsonProperty("pgscan")
    Long pgscan();

    @JsonProperty("pgsteal")
    Long pgsteal();

    @JsonProperty("shmem")
    Long shmem();

    @JsonProperty("slab")
    Long slab();

    @JsonProperty("slab_reclaimable")
    Long slab_reclaimable();

    @JsonProperty("slab_unreclaimable")
    Long slab_unreclaimable();

    @JsonProperty("sock")
    Long sock();

    @JsonProperty("thp_collapse_alloc")
    Long thp_collapse_alloc();

    @JsonProperty("thp_fault_alloc")
    Long thp_fault_alloc();

    @Nullable
    @JsonProperty("unevictable")
    Long unevictable();

    @Nullable
    @JsonProperty("workingset_activate")
    Long workingset_activate();

    @Nullable
    @JsonProperty("workingset_nodereclaim")
    Long workingset_nodereclaim();

    @Nullable
    @JsonProperty("workingset_refault")
    Long workingset_refault();
  }
}
