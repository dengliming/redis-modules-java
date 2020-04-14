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
package io.github.dengliming.redismodule.redisbloom.client;

import io.github.dengliming.redismodule.redisbloom.BloomFilter;
import io.github.dengliming.redismodule.redisbloom.CountMinSketch;
import io.github.dengliming.redismodule.redisbloom.CuckooFilter;
import io.github.dengliming.redismodule.redisbloom.TopKFilter;
import org.redisson.Redisson;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 */
public class RedisBloomClient {

    private Redisson client;

    public RedisBloomClient() {
        this("redis://127.0.0.1:6379");
    }

    public RedisBloomClient(String address) {
        Config config = new Config();
        config.useSingleServer().setAddress(address);
        this.client = (Redisson) Redisson.create(config);
    }

    public BloomFilter getBloomFilter(String name) {
        return new BloomFilter(client.getConnectionManager().getCommandExecutor(), name);
    }

    public CountMinSketch getCountMinSketch(String name) {
        return new CountMinSketch(client.getConnectionManager().getCommandExecutor(), name);
    }

    public CuckooFilter getCuckooFilter(String name) {
        return new CuckooFilter(client.getConnectionManager().getCommandExecutor(), name);
    }

    public TopKFilter getTopKFilter(String name) {
        return new TopKFilter(client.getConnectionManager().getCommandExecutor(), name);
    }

    public void shutdown() {
        client.shutdown();
    }

    public void shutdown(long quietPeriod, long timeout, TimeUnit unit) {
        client.shutdown(quietPeriod, timeout, unit);
    }
}
