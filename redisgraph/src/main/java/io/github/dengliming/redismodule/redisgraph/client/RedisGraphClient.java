/*
 * Copyright 2022 dengliming.
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

package io.github.dengliming.redismodule.redisgraph.client;

import io.github.dengliming.redismodule.redisgraph.RedisGraph;
import io.github.dengliming.redismodule.redisgraph.RedisGraphBatch;
import org.redisson.Redisson;
import org.redisson.api.BatchOptions;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;

public class RedisGraphClient extends Redisson {

    public RedisGraphClient(Config config) {
        super(config);
    }

    public RedisGraph getRedisGraph() {
        return new RedisGraph(getCommandExecutor());
    }

    public RedisGraphBatch createRedisGraphBatch() {
        return this.createRedisGraphBatch(BatchOptions.defaults());
    }

    public RedisGraphBatch createRedisGraphBatch(BatchOptions options) {
        return new RedisGraphBatch(getCommandExecutor(), options);
    }

    public Void flushall() {
        CommandAsyncExecutor commandExecutor = getCommandExecutor();
        return commandExecutor.get(commandExecutor.writeAllAsync(RedisCommands.FLUSHALL));
    }
}
