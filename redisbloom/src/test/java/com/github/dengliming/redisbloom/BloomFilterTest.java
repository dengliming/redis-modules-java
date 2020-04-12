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
package com.github.dengliming.redisbloom;

import com.github.dengliming.redisbloom.model.BloomFilterInfo;
import com.github.dengliming.redisbloom.model.InsertArgs;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author dengliming
 */
public class BloomFilterTest extends AbstractTest {

    @Test
    public void testReserve() {
        BloomFilter bloomFilter = redisBloomClient.getBloomFilter("bf_reserve");
        // drop if exits
        bloomFilter.delete();
        boolean result = bloomFilter.create(0.1d, 100);
        Assert.assertTrue(result);
    }

    @Test
    public void testAdd() {
        BloomFilter bloomFilter = redisBloomClient.getBloomFilter("bf_add");
        // drop if exits
        bloomFilter.delete();
        Assert.assertTrue(bloomFilter.create(0.1d, 100));
        List<Boolean> results = bloomFilter.madd(new String[] {"a", "b", "c"});
        Assert.assertTrue(results != null && results.size() == 3);
        Assert.assertTrue(bloomFilter.exists("a"));
    }

    @Test
    public void testInsert() {
        BloomFilter bloomFilter = redisBloomClient.getBloomFilter("bf_insert");
        // drop if exits
        bloomFilter.delete();
        Assert.assertTrue(bloomFilter.create(0.1d, 100));
        List<Boolean> result = bloomFilter.insert(new InsertArgs(), "a");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.get(0));
    }

    @Test
    public void testInfo() {
        BloomFilter bloomFilter = redisBloomClient.getBloomFilter("bf_info");
        // drop if exits
        bloomFilter.delete();
        boolean result = bloomFilter.create(0.1d, 100);
        Assert.assertTrue(result);
        BloomFilterInfo bloomFilterInfo = bloomFilter.getInfo();
        Assert.assertTrue(bloomFilterInfo.getCapacity().intValue() == 100);
    }
}
