/*
 * Copyright 2024 dengliming.
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

import io.github.dengliming.redismodule.common.BaseRedissonClient;
import io.github.dengliming.redismodule.redisbloom.BloomFilter;
import io.github.dengliming.redismodule.redisbloom.CountMinSketch;
import io.github.dengliming.redismodule.redisbloom.CuckooFilter;
import io.github.dengliming.redismodule.redisbloom.RedisBloomBatch;
import io.github.dengliming.redismodule.redisbloom.TDigest;
import io.github.dengliming.redismodule.redisbloom.TopKFilter;
import org.redisson.api.BatchOptions;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisBloomClient extends BaseRedissonClient {

    public RedisBloomClient(Config config) {
        super(config);
    }

    /**
     * Wraps an existing Redisson instance so that it can be shared with other clients.
     * The caller stays responsible for shutting it down.
     */
    public RedisBloomClient(RedissonClient redisson) {
        super(redisson);
    }

    public BloomFilter getRBloomFilter(String name) {
        return new BloomFilter(getCommandExecutor(), name);
    }

    public CountMinSketch getCountMinSketch(String name) {
        return new CountMinSketch(getCommandExecutor(), name);
    }

    public CuckooFilter getCuckooFilter(String name) {
        return new CuckooFilter(getCommandExecutor(), name);
    }

    public TopKFilter getTopKFilter(String name) {
        return new TopKFilter(getCommandExecutor(), name);
    }

    public TDigest getTDigest(String name) {
        return new TDigest(getCommandExecutor(), name);
    }

    public RedisBloomBatch createRedisBloomBatch() {
        return createRedisBloomBatch(BatchOptions.defaults());
    }

    public RedisBloomBatch createRedisBloomBatch(BatchOptions options) {
        return new RedisBloomBatch(getCommandExecutor(), options);
    }
}
