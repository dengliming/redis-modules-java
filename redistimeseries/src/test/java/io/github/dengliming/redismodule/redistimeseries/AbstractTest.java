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

package io.github.dengliming.redismodule.redistimeseries;

import io.github.dengliming.redismodule.redistimeseries.client.RedisTimeSeriesClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.redisson.config.Config;

/**
 * @author dengliming
 */
public abstract class AbstractTest {

    public static final String DEFAULT_HOST = System.getProperty("REDIS_HOST", "192.168.50.16");
    public static final int DEFAULT_PORT = Integer.valueOf(System.getProperty("REDIS_PORT", "6381"));
    public static final String DEFAULT_PASSWORD = System.getProperty("REDIS_PASSWORD", "");

    private RedisTimeSeriesClient redisTimeSeriesClient;

    @BeforeEach
    public void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + DEFAULT_HOST + ":" + DEFAULT_PORT);
        redisTimeSeriesClient = new RedisTimeSeriesClient(config);
        redisTimeSeriesClient.flushall();
    }

    @AfterEach
    public void destroy() {
        if (redisTimeSeriesClient != null) {
            redisTimeSeriesClient.shutdown();
        }
    }

    public RedisTimeSeries getRedisTimeSeries() {
        return redisTimeSeriesClient == null ? null : redisTimeSeriesClient.getRedisTimeSeries();
    }
}
