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

package io.github.dengliming.redismodule.redisearch;

import io.github.dengliming.redismodule.common.util.TestSettings;
import io.github.dengliming.redismodule.redisearch.client.RediSearchClient;
import io.github.dengliming.redismodule.redisjson.client.RedisJSONClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.redisson.config.Config;

/**
 * @author dengliming
 */
public abstract class AbstractTest {

    private RediSearchClient rediSearchClient;
    private RedisJSONClient redisJSONClient;

    @BeforeEach
    public void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + TestSettings.host() + ":" + TestSettings.port());
        rediSearchClient = new RediSearchClient(config);
        redisJSONClient = new RedisJSONClient(config);
        rediSearchClient.flushall();
        redisJSONClient.flushall();
    }

    @AfterEach
    public void destroy() {
        if (rediSearchClient != null) {
            rediSearchClient.shutdown();
        }
    }

    protected RediSearchClient getRediSearchClient() {
        return rediSearchClient;
    }

    protected RedisJSONClient getRedisJSONClient() {
        return redisJSONClient;
    }
}

