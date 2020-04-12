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

import com.github.dengliming.redisbloom.model.CountMinSketchInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 */
public class CountMinSketchTest extends AbstractTest {

    @Test
    public void testReserve() {
        CountMinSketch countMinSketch = redisBloomClient.getCountMinSketch("cms_reserve");
        // drop if exits
        countMinSketch.delete();
        Assert.assertTrue(countMinSketch.create(0.1d, 0.2d));

        CountMinSketch countMinSketch2 = redisBloomClient.getCountMinSketch("cms_reserve2");
        // drop if exits
        countMinSketch2.delete();
        Assert.assertTrue(countMinSketch2.create(10, 2));
    }

    @Test
    public void testAdd() {
        CountMinSketch countMinSketch = redisBloomClient.getCountMinSketch("cms_add");
        // drop if exits
        countMinSketch.delete();
        Assert.assertTrue(countMinSketch.create(10, 10));
        Map<String, Integer> itemIncrement = new HashMap<>();
        itemIncrement.put("a", 3);
        List<Integer> results = countMinSketch.incrby(itemIncrement);
        Assert.assertTrue(results != null && results.size() == 1);
        Assert.assertTrue(results.get(0) == 3);
        results = countMinSketch.query("a");
        Assert.assertTrue(results.get(0) == 3);
    }

    @Test
    public void testInfo() {
        CountMinSketch countMinSketch = redisBloomClient.getCountMinSketch("cms_info");
        // drop if exits
        countMinSketch.delete();
        Assert.assertTrue(countMinSketch.create(5, 10));
        CountMinSketchInfo countMinSketchInfo = countMinSketch.getInfo();
        Assert.assertTrue(countMinSketchInfo.getWidth() == 5);
    }

}
