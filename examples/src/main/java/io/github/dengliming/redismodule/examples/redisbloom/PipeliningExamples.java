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

package io.github.dengliming.redismodule.examples.redisbloom;

import io.github.dengliming.redismodule.redisbloom.BloomFilter;
import io.github.dengliming.redismodule.redisbloom.RedisBloomBatch;
import io.github.dengliming.redismodule.redisbloom.client.RedisBloomClient;
import org.redisson.api.BatchResult;
import org.redisson.config.Config;

public class PipeliningExamples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedisBloomClient redisBloomClient = new RedisBloomClient(config);

        // Every module client exposes createXxxBatch(); objects obtained from the batch queue their *Async calls
        RedisBloomBatch batch = redisBloomClient.createRedisBloomBatch();
        BloomFilter bloomFilter = batch.getRBloomFilter("bf");
        bloomFilter.createAsync(0.01d, 1000);
        bloomFilter.maddAsync("a", "b", "c");
        bloomFilter.existsAsync("a");
        bloomFilter.existsAsync("z");

        // One round trip for all four commands; responses come back in call order
        BatchResult<?> result = batch.execute();
        // true
        System.out.println(result.getResponses().get(0));
        // [true, true, true]
        System.out.println(result.getResponses().get(1));
        // true
        System.out.println(result.getResponses().get(2));
        // false
        System.out.println(result.getResponses().get(3));

        redisBloomClient.shutdown();
    }
}
