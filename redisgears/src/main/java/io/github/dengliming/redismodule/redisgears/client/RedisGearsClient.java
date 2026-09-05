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

package io.github.dengliming.redismodule.redisgears.client;

import io.github.dengliming.redismodule.common.BaseRedissonClient;
import io.github.dengliming.redismodule.redisgears.RedisGears;
import io.github.dengliming.redismodule.redisgears.RedisGearsBatch;
import org.redisson.api.BatchOptions;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisGearsClient extends BaseRedissonClient {

    public RedisGearsClient(Config config) {
        super(config);
    }

    /**
     * Wraps an existing Redisson instance so that it can be shared with other clients.
     * The caller stays responsible for shutting it down.
     */
    public RedisGearsClient(RedissonClient redisson) {
        super(redisson);
    }

    public RedisGears getRedisGears() {
        return new RedisGears(getCommandExecutor());
    }

    public RedisGearsBatch createRedisGearsBatch() {
        return createRedisGearsBatch(BatchOptions.defaults());
    }

    public RedisGearsBatch createRedisGearsBatch(BatchOptions options) {
        return new RedisGearsBatch(getCommandExecutor(), options);
    }
}
