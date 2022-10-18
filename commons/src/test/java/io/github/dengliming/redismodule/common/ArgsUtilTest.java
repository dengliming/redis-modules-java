/*
 * Copyright 2022 dengliming.
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

package io.github.dengliming.redismodule.common;

import io.github.dengliming.redismodule.common.util.ArgsUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgsUtilTest {

    @Test
    public void testAppend() {
        assertThat(ArgsUtil.append("key1", "a", "b", "c")).containsExactly("key1", "a", "b", "c");
        Map<String, Object> params = new HashMap<>();
        params.put("k1", "v1");
        params.put("k2", "v2");
        Object[] result = ArgsUtil.append("key2", params);
        assertThat(result).hasSize(5);
        assertThat(result).contains("key2");
        assertThat(result).contains("k1", "v1");
        assertThat(result).contains("k2", "v2");
    }
}
