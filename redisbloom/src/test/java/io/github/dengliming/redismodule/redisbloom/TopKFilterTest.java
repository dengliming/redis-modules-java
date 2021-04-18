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
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author dengliming
 */
public class TopKFilterTest extends AbstractTest {

    @Test
    public void testReserve() {
        TopKFilter topKFilter = getRedisBloomClient().getTopKFilter("topk_reserve");
        assertThat(topKFilter.reserve(10, 2000, 7, 0.925d)).isTrue();
    }

    @Test
    public void testAdd() {
        TopKFilter topKFilter = getRedisBloomClient().getTopKFilter("topk_add");
        assertThat(topKFilter.reserve(1, 2000, 7, 0.925d)).isTrue();
        topKFilter.add("test");
        List<Boolean> itemExits = topKFilter.query("test");
        assertThat(itemExits).isNotEmpty().hasSize(1);
        assertThat(itemExits.get(0)).isTrue();
        Map<String, Integer> itemIncrement = new HashMap<>();
        itemIncrement.put("test", 3);
        topKFilter.incrby(itemIncrement);
        List<String> allItems = topKFilter.list();
        assertThat(allItems).isNotEmpty();
        assertThat(allItems.get(0)).isEqualTo("test");
    }

    @Test
    public void testInfo() {
        TopKFilter topKFilter = getRedisBloomClient().getTopKFilter("topk_info");
        assertThat(topKFilter.reserve(10, 2000, 7, 0.925d)).isTrue();
        TopKFilterInfo topKFilterInfo = topKFilter.getInfo();
        assertThat(topKFilterInfo.getDepth()).isEqualTo(7);
    }
}
