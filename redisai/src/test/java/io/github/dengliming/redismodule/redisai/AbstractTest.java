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

import io.github.dengliming.redismodule.common.util.TestSettings;
import io.github.dengliming.redismodule.redisai.client.RedisAIClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.redisson.config.Config;

/**
 * @author dengliming
 */
public abstract class AbstractTest {

    private RedisAIClient redisAIClient;

    @BeforeEach
    public void init() {
        Config config = new Config();
        config.useSingleServer()
                .setConnectTimeout(10000)
                .setTimeout(10000)
                .setAddress("redis://" + TestSettings.host() + ":" + TestSettings.port());
        redisAIClient = new RedisAIClient(config);
        redisAIClient.flushall();
    }

    @AfterEach
    public void destroy() {
        if (redisAIClient != null) {
            redisAIClient.shutdown();
        }
    }

    public RedisAI getRedisAI() {
        if (redisAIClient != null) {
            return redisAIClient.getRedisAI();
        }
        return null;
    }
}
