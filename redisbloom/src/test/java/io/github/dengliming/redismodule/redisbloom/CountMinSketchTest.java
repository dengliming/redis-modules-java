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

import io.github.dengliming.redismodule.redisbloom.model.CountMinSketchInfo;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class CountMinSketchTest extends AbstractTest {

    @Test
    public void testReserve() {
        CountMinSketch countMinSketch = getRedisBloomClient().getCountMinSketch("cms_reserve");
        assertThat(countMinSketch.create(0.1d, 0.2d)).isTrue();

        CountMinSketch countMinSketch2 = getRedisBloomClient().getCountMinSketch("cms_reserve2");
        assertThat(countMinSketch2.create(10, 2)).isTrue();
    }

    @Test
    public void testAdd() {
        CountMinSketch countMinSketch = getRedisBloomClient().getCountMinSketch("cms_add");
        assertThat(countMinSketch.create(10, 10)).isTrue();
        Map<String, Integer> itemIncrement = new HashMap<>();
        List<Integer> results = countMinSketch.incrby(new String[]{"a"}, new int[]{3});
        assertThat(results).isNotNull().hasSize(1);
        assertThat(results.get(0)).isEqualTo(3);
        results = countMinSketch.query("a");
        assertThat(results.get(0)).isEqualTo(3);
    }

    @Test
    public void testInfo() {
        CountMinSketch countMinSketch = getRedisBloomClient().getCountMinSketch("cms_info");
        assertThat(countMinSketch.create(5, 10)).isTrue();
        CountMinSketchInfo countMinSketchInfo = countMinSketch.getInfo();
        assertThat(countMinSketchInfo.getWidth()).isEqualTo(5);
    }

}
