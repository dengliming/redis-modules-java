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
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;

/**
 * @author dengliming
 */
public class RedisBloomClient extends Redisson {

    public RedisBloomClient(Config config) {
        super(config);
    }

    public BloomFilter getRBloomFilter(String name) {
        return new BloomFilter(getConnectionManager().getCommandExecutor(), name);
    }

    public CountMinSketch getCountMinSketch(String name) {
        return new CountMinSketch(getConnectionManager().getCommandExecutor(), name);
    }

    public CuckooFilter getCuckooFilter(String name) {
        return new CuckooFilter(getConnectionManager().getCommandExecutor(), name);
    }

    public TopKFilter getTopKFilter(String name) {
        return new TopKFilter(getConnectionManager().getCommandExecutor(), name);
    }

    public Void flushall() {
        CommandAsyncExecutor commandExecutor = getConnectionManager().getCommandExecutor();
        return commandExecutor.get(commandExecutor.writeAllAsync(RedisCommands.FLUSHALL));
    }
}
