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

package io.github.dengliming.redismodule.redisgears;

import io.github.dengliming.redismodule.common.api.RCommonBatch;
import org.redisson.api.BatchOptions;
import org.redisson.command.CommandAsyncExecutor;

/**
 * Pipelines RedisGears commands: call the *Async methods of {@link #getRedisGears()}, then {@link #execute()}.
 */
public class RedisGearsBatch extends RCommonBatch {

    public RedisGearsBatch(CommandAsyncExecutor executor, BatchOptions options) {
        super(executor, options);
    }

    public RedisGears getRedisGears() {
        return new RedisGears(getExecutorService());
    }
}
