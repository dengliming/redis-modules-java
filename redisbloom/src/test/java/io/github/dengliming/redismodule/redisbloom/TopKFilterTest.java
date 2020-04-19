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

import io.github.dengliming.redismodule.redisbloom.model.TopKFilterInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author dengliming
 */
public class TopKFilterTest extends AbstractTest {

    @Test
    public void testReserve() {
        TopKFilter topKFilter = redisBloomClient.getTopKFilter("topk_reserve");
        Assert.assertTrue(topKFilter.reserve(10, 2000, 7, 0.925d));
    }

    @Test
    public void testAdd() {
        TopKFilter topKFilter = redisBloomClient.getTopKFilter("topk_add");
        Assert.assertTrue(topKFilter.reserve(1, 2000, 7, 0.925d));
        topKFilter.add("test");
        List<Boolean> itemExits = topKFilter.query("test");
        Assert.assertTrue(itemExits != null && itemExits.size() == 1 && itemExits.get(0));
        Map<String, Integer> itemIncrement = new HashMap<>();
        itemIncrement.put("test", 3);
        topKFilter.incrby(itemIncrement);
        List<String> allItems = topKFilter.list();
        Assert.assertNotNull(allItems);
        Assert.assertEquals("test", allItems.get(0));
    }

    @Test
    public void testInfo() {
        TopKFilter topKFilter = redisBloomClient.getTopKFilter("topk_info");
        Assert.assertTrue(topKFilter.reserve(10, 2000, 7, 0.925d));
        TopKFilterInfo topKFilterInfo = topKFilter.getInfo();
        Assert.assertTrue(topKFilterInfo.getDepth().intValue() == 7);
    }
}
