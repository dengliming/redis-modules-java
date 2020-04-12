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
package com.github.dengliming.redisbloom;

import com.github.dengliming.redisbloom.client.RedisBloomClient;
import org.junit.After;
import org.junit.Before;

/**
 * @author dengliming
 */
public abstract class AbstractTest {

    public static final String DEFAULT_HOST = System.getProperty("redis.host", "localhost");
    public static final int DEFAULT_PORT = Integer.valueOf(System.getProperty("redis.port", "6379"));
    public static final String DEFAULT_PASSWORD = System.getProperty("redis.password", "");

    protected RedisBloomClient redisBloomClient;

    @Before
    public void init() {
        redisBloomClient = new RedisBloomClient("redis://" + DEFAULT_HOST + ":" + DEFAULT_PORT);
    }

    @After
    public void destory() {
        redisBloomClient.shutdown();
    }
}
