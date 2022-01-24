/*
 * Copyright 2021-2022 dengliming.
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

package io.github.dengliming.redismodule.redisgears;

import org.junit.jupiter.api.Test;
import org.redisson.client.RedisException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author dengliming
 */
public class RedisGearsTest extends AbstractTest {

    @Test
    public void testPyExecute() {
        RedisGears redisGears = getRedisGears();
        // [[],[]]
        assertThat((List<List<Object>>) redisGears.pyExecute("GB().run()", false)).hasSize(2);

        // execution ID
        assertThat((String) redisGears.pyExecute("GB().run()", true)).isNotBlank();

        // (error) [... 'spam.error: Can not run more then 1 executions in a single script']
        assertThatThrownBy(() -> redisGears.pyExecute("GB().run()\nGB().run()", false))
                .isInstanceOf(RedisException.class)
                .hasMessageContaining("Can not run more then 1 executions in a single script");
    }

    @Test
    public void testConfig() {
        RedisGears redisGears = getRedisGears();

        Map<String, String> configMap = new HashMap<>();
        configMap.put("a", "1");
        configMap.put("foo", "bar");
        List<String> resultList = redisGears.setConfig(configMap);
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.size()).isEqualTo(2);
        assertThat(resultList.get(0)).contains("OK");

        List<String> getResults = redisGears.getConfig("a", "foo");
        assertThat(getResults).containsExactly("1", "bar");
    }

    @Test
    public void testPyStats() {
        RedisGears redisGears = getRedisGears();
        Map<String, Object> result = redisGears.pyStats();
        assertThat(result).containsKeys("TotalAllocated", "PeakAllocated", "CurrAllocated");
    }

    @Test
    public void testUnRegister() {
        RedisGears redisGears = getRedisGears();
        assertThatThrownBy(() -> redisGears.unRegister("not_exist"))
                .isInstanceOf(RedisException.class)
                .hasMessageContaining("execution is not registered.");
    }

    @Test
    public void testRefreshCluster() {
        RedisGears redisGears = getRedisGears();
        assertThat(redisGears.refreshCluster()).isTrue();
    }

    @Test
    public void testAbortExecution() {
        RedisGears redisGears = getRedisGears();
        assertThatThrownBy(() -> redisGears.abortExecution("not_exist"))
                .isInstanceOf(RedisException.class)
                .hasMessageContaining("execution does not exist.");
    }

    @Test
    public void testDropExecution() {
        RedisGears redisGears = getRedisGears();
        assertThatThrownBy(() -> redisGears.dropExecution("not_exist"))
                .isInstanceOf(RedisException.class)
                .hasMessageContaining("execution plan does not exist.");
    }

    @Test
    public void testClusterInfo() {
        RedisGears redisGears = getRedisGears();
        // Throw Exception when is not in cluster.
        assertThatThrownBy(() -> redisGears.clusterInfo())
                .isInstanceOf(RedisException.class);
    }
}
