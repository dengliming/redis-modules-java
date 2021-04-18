/*
 * Copyright 2020 dengliming.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dengliming.redismodule.redisbloom;

import io.github.dengliming.redismodule.redisbloom.model.BloomFilterInfo;
import io.github.dengliming.redismodule.redisbloom.model.ChunksData;
import io.github.dengliming.redismodule.redisbloom.model.InsertArgs;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class BloomFilterTest extends AbstractTest {

    @Test
    public void testReserve() {
        BloomFilter bloomFilter = getRedisBloomClient().getRBloomFilter("bf_reserve");
        assertThat(bloomFilter.create(0.1d, 100)).isTrue();
    }

    @Test
    public void testAdd() {
        BloomFilter bloomFilter = getRedisBloomClient().getRBloomFilter("bf_add");
        assertThat(bloomFilter.create(0.1d, 100)).isTrue();
        List<Boolean> results = bloomFilter.madd(new String[]{"a", "b", "c"});
        assertThat(results).isNotNull().hasSize(3);
        assertThat(bloomFilter.exists("a")).isTrue();
    }

    @Test
    public void testInsert() {
        BloomFilter bloomFilter = getRedisBloomClient().getRBloomFilter("bf_insert");
        assertThat(bloomFilter.create(0.1d, 100)).isTrue();
        List<Boolean> result = bloomFilter.insert(new InsertArgs(), "a");
        assertThat(result).isNotNull();
        assertThat(result.get(0)).isTrue();
    }

    @Test
    public void testInfo() {
        BloomFilter bloomFilter = getRedisBloomClient().getRBloomFilter("bf_info");
        assertThat(bloomFilter.create(0.1d, 100)).isTrue();
        BloomFilterInfo bloomFilterInfo = bloomFilter.getInfo();
        assertThat(bloomFilterInfo.getCapacity().intValue()).isEqualTo(100);
    }

    @Test
    public void testScanDump() {
        BloomFilter bloomFilter = getRedisBloomClient().getRBloomFilter("bf_info");
        assertThat(bloomFilter.create(0.1d, 100)).isTrue();
        bloomFilter.add("a1");

        int iter = 0;
        List<ChunksData> chunks = new ArrayList<>();
        while (true) {
            ChunksData chunksData = bloomFilter.scanDump(iter);
            iter = chunksData.getIter();
            if (iter == 0) {
                break;
            }
            chunks.add(chunksData);
        }

        bloomFilter.delete();
        // Load it back
        chunks.forEach(chunksData -> assertThat(bloomFilter.loadChunk(chunksData)).isTrue());

        BloomFilterInfo bloomFilterInfo = bloomFilter.getInfo();
        assertThat(bloomFilterInfo.getCapacity().intValue()).isEqualTo(100);
    }
}
