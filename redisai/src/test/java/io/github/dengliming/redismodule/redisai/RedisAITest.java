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
package io.github.dengliming.redismodule.redisai;

import org.junit.Test;
import org.redisson.client.RedisException;

import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author dengliming
 */
public class RedisAITest extends AbstractTest {

    @Test
    public void testSetTensor() {
        RedisAI redisAI = redisAIClient.getRedisAI();
        assertTrue(redisAI.setTensor("tensor1", DataType.FLOAT, new int[]{2, 2}, null, new String[]{"1", "2", "3", "4"}));
    }

    @Test
    public void testConfig() {
        RedisAI redisAI = redisAIClient.getRedisAI();
        assertTrue(redisAI.setBackendPath("/usr/lib/redis/modules/backends/"));
        assertThrows(RedisException.class, () -> redisAI.loadBackend(Backend.TF, "notexist/redisai_tensorflow.so"));
        assertTrue(redisAI.loadBackend(Backend.TF, "redisai_tensorflow/redisai_tensorflow.so"));
    }

    @Test
    public void testInfo() {
        RedisAI redisAI = redisAIClient.getRedisAI();
        String key = "tensor:info";
        String script = "def bar(a, b):\n" + "    return a + b\n";
        assertTrue(redisAI.setScript(key, Device.CPU, script));
        // not exist
        Map<String, Object> infoMap;
        assertThrows(RedisException.class, () -> {
            // ERR cannot find run info for key
            redisAI.getInfo("not:exist");
        });

        // first inited info
        infoMap = redisAI.getInfo(key);
        assertNotNull(infoMap);
        assertEquals(key, infoMap.get("key"));
        assertEquals(Device.CPU.name(), infoMap.get("device"));
        assertEquals(0L, infoMap.get("calls"));

        redisAI.setTensor("a1", DataType.FLOAT, new int[] {2}, null, new String[] {"2", "3"});
        redisAI.setTensor("b1", DataType.FLOAT, new int[] {2}, null, new String[] {"2", "3"});
        assertTrue(redisAI.runScript(key, "bar", new String[] {"a1", "b1"}, new String[] {"c1"}));

        // one model runs
        infoMap = redisAI.getInfo(key);
        assertEquals(1L, infoMap.get("calls"));

        // reset
        assertTrue(redisAI.resetStat(key));
        infoMap = redisAI.getInfo(key);
        assertEquals(0L, infoMap.get("calls"));

        assertThrows(RedisException.class, () -> {
            // ERR cannot find run info for key
            redisAI.resetStat("not:exist");
        });
    }
}
