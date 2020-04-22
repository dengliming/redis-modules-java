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
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 */
public class CuckooFilterTest extends AbstractTest {

    @Test
    public void testReserve() {
        CuckooFilter cuckooFilter = redisBloomClient.getCuckooFilter("cf_reserve");
        Assert.assertTrue(cuckooFilter.reserve(100));
    }

    @Test
    public void testAdd() {
        CuckooFilter cuckooFilter = redisBloomClient.getCuckooFilter("cf_add");
        Assert.assertTrue(cuckooFilter.reserve(100));
        Assert.assertTrue(cuckooFilter.add("a"));
        Assert.assertTrue(cuckooFilter.exists("a"));
        Assert.assertTrue(cuckooFilter.count("a") == 1);
        Assert.assertFalse(cuckooFilter.addNx("a"));
        Assert.assertTrue(cuckooFilter.delete("a"));
    }

    @Test
    public void testInsert() {
        CuckooFilter cuckooFilter = redisBloomClient.getCuckooFilter("cf_insert");
        List<Boolean> result = cuckooFilter.insert(-1L, false, "a");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.get(0));
    }

    @Test
    public void testInfo() {
        CuckooFilter cuckooFilter = redisBloomClient.getCuckooFilter("cf_info");
        Assert.assertTrue(cuckooFilter.reserve(100, 50));
        CuckooFilterInfo cuckooFilterInfo = cuckooFilter.getInfo();
        Assert.assertTrue(cuckooFilterInfo.getBucketSize().intValue() == 50);
    }

    @Test
    public void testScanDump() {
        CuckooFilter cuckooFilter = redisBloomClient.getCuckooFilter("bf_info");
        Assert.assertTrue(cuckooFilter.reserve(100, 50));
        Assert.assertTrue(cuckooFilter.add("a"));

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
        chunks.forEach(chunksData -> Assert.assertTrue(cuckooFilter.loadChunk(chunksData)));

        CuckooFilterInfo cuckooFilterInfo = cuckooFilter.getInfo();
        Assert.assertTrue(cuckooFilterInfo.getBucketSize().intValue() == 50);
    }
}
