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

package io.github.dengliming.redismodule.examples;

import io.github.dengliming.redismodule.redisbloom.BloomFilter;
import io.github.dengliming.redismodule.redisbloom.client.RedisBloomClient;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.client.RedisJSONClient;
import io.github.dengliming.redismodule.redisearch.RediSearch;
import io.github.dengliming.redismodule.redisearch.client.RediSearchClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Several module clients can share one Redisson instance (and therefore one connection pool)
 * instead of each opening their own.
 */
public class SharedRedissonExamples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);

        // Clients built from an existing RedissonClient do not shut it down; the caller owns it
        RedisJSONClient redisJSONClient = new RedisJSONClient(redisson);
        RediSearchClient rediSearchClient = new RediSearchClient(redisson);
        RedisBloomClient redisBloomClient = new RedisBloomClient(redisson);

        RedisJSON redisJSON = redisJSONClient.getRedisJSON();
        redisJSON.set("doc:1", SetArgs.Builder.create(".", "{\"title\":\"hello\"}"));

        RediSearch rediSearch = rediSearchClient.getRediSearch("idx");
        System.out.println(rediSearch.listIndexes());

        BloomFilter bloomFilter = redisBloomClient.getRBloomFilter("bf");
        bloomFilter.add("doc:1");

        // No-ops for shared instances
        redisJSONClient.shutdown();
        rediSearchClient.shutdown();
        redisBloomClient.shutdown();

        redisson.shutdown();
    }
}
