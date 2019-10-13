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

package org.mandas.docker.client.messages;

import java.math.BigInteger;

import org.immutables.value.Value.Enclosing;
import org.immutables.value.Value.Immutable;
import org.mandas.docker.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ImmutableMemoryStats.Builder.class)
@Immutable
@Enclosing
public interface MemoryStats {

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

    @JsonProperty("active_file")
    Long activeFile();

    @JsonProperty("total_active_file")
    Long totalActiveFile();

    @JsonProperty("inactive_file")
    Long inactiveFile();

    @JsonProperty("total_inactive_file")
    Long totalInactiveFile();

    @JsonProperty("cache")
    Long cache();

    @JsonProperty("total_cache")
    Long totalCache();

    @JsonProperty("active_anon")
    Long activeAnon();

    @JsonProperty("total_active_anon")
    Long totalActiveAnon();

    @JsonProperty("inactive_anon")
    Long inactiveAnon();

    @JsonProperty("total_inactive_anon")
    Long totalInactiveAnon();

    @JsonProperty("hierarchical_memory_limit")
    BigInteger hierarchicalMemoryLimit();

    @JsonProperty("mapped_file")
    Long mappedFile();

    @JsonProperty("total_mapped_file")
    Long totalMappedFile();

    @JsonProperty("pgmajfault")
    Long pgmajfault();

    @JsonProperty("total_pgmajfault")
    Long totalPgmajfault();

    @JsonProperty("pgpgin")
    Long pgpgin();

    @JsonProperty("total_pgpgin")
    Long totalPgpgin();

    @JsonProperty("pgpgout")
    Long pgpgout();

    @JsonProperty("total_pgpgout")
    Long totalPgpgout();

    @JsonProperty("pgfault")
    Long pgfault();

    @JsonProperty("total_pgfault")
    Long totalPgfault();

    @JsonProperty("rss")
    Long rss();

    @JsonProperty("total_rss")
    Long totalRss();

    @JsonProperty("rss_huge")
    Long rssHuge();

    @JsonProperty("total_rss_huge")
    Long totalRssHuge();

    @JsonProperty("unevictable")
    Long unevictable();

    @JsonProperty("total_unevictable")
    Long totalUnevictable();

    @Nullable
    @JsonProperty("total_writeback")
    Long totalWriteback();

    @Nullable
    @JsonProperty("writeback")
    Long writeback();
  }
}
