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

import io.github.dengliming.redismodule.redisbloom.model.ChunksData;
import io.github.dengliming.redismodule.redisbloom.model.CuckooFilterInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class CuckooFilterTest extends AbstractTest {

    @Test
    public void testReserve() {
        CuckooFilter cuckooFilter = getRedisBloomClient().getCuckooFilter("cf_reserve");
        assertThat(cuckooFilter.reserve(100)).isTrue();
    }

    @Test
    public void testAdd() {
        CuckooFilter cuckooFilter = getRedisBloomClient().getCuckooFilter("cf_add");
        assertThat(cuckooFilter.reserve(100)).isTrue();
        assertThat(cuckooFilter.add("a")).isTrue();
        assertThat(cuckooFilter.exists("a")).isTrue();
        assertThat(cuckooFilter.count("a")).isEqualTo(1);
        assertThat(cuckooFilter.addNx("a")).isFalse();
        assertThat(cuckooFilter.delete("a")).isTrue();
    }

    @Test
    public void testInsert() {
        CuckooFilter cuckooFilter = getRedisBloomClient().getCuckooFilter("cf_insert");
        List<Boolean> result = cuckooFilter.insert(-1L, false, "a");
        assertThat(result).isNotNull();
        assertThat(result.get(0)).isTrue();
    }

    @Test
    public void testInfo() {
        CuckooFilter cuckooFilter = getRedisBloomClient().getCuckooFilter("cf_info");
        assertThat(cuckooFilter.reserve(100, 50)).isTrue();
        CuckooFilterInfo cuckooFilterInfo = cuckooFilter.getInfo();
        assertThat(cuckooFilterInfo.getBucketSize()).isEqualTo(50);
    }

    @Test
    public void testScanDump() {
        CuckooFilter cuckooFilter = getRedisBloomClient().getCuckooFilter("bf_info");
        assertThat(cuckooFilter.reserve(100, 50)).isTrue();
        assertThat(cuckooFilter.add("a")).isTrue();

        int iter = 0;
        List<ChunksData> chunks = new ArrayList<>();
        while (true) {
            ChunksData chunksData = cuckooFilter.scanDump(iter);
            iter = chunksData.getIter();
            if (iter == 0) {
                break;
            }
            chunks.add(chunksData);
        }

        cuckooFilter.delete();
        // Load it back
        chunks.forEach(chunksData -> assertThat(cuckooFilter.loadChunk(chunksData)).isTrue());

        CuckooFilterInfo cuckooFilterInfo = cuckooFilter.getInfo();
        assertThat(cuckooFilterInfo.getBucketSize().intValue()).isEqualTo(50);
    }
}
