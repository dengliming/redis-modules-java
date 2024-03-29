/*
 * Copyright 2020-2022 dengliming.
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

import java.util.List;

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
        assertThat(countMinSketchInfo.getDepth()).isEqualTo(10);
        assertThat(countMinSketchInfo.getCount()).isEqualTo(0);
    }

    @Test
    public void testMerge() {
        CountMinSketch a = getRedisBloomClient().getCountMinSketch("cms_a");
        CountMinSketch b = getRedisBloomClient().getCountMinSketch("cms_b");
        CountMinSketch c = getRedisBloomClient().getCountMinSketch("cms_c");
        c.create(1000, 5);
        a.create(1000, 5);
        b.create(1000, 5);
        a.incrby(new String[]{"foo", "bar"}, new int[]{5, 3});
        b.incrby(new String[]{"foo", "bar"}, new int[]{2, 4});
        c.incrby(new String[]{"foo", "bar"}, new int[]{3, 6});
        assertThat(a.query("foo", "bar")).containsExactly(5, 3);
        assertThat(b.query("foo", "bar")).containsExactly(2, 4);
        assertThat(c.query("foo", "bar")).containsExactly(3, 6);

        assertThat(a.merge(new String[]{"cms_b", "cms_c"}, new int[]{})).isTrue();
        assertThat(a.query("foo", "bar")).containsExactly(5, 10);
    }
}
