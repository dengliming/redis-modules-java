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
package io.github.dengliming.redismodule.redisearch.client;

import io.github.dengliming.redismodule.redisearch.RediSearch;
import org.redisson.Redisson;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 */
public class RediSearchClient {

    private Redisson client;

    public RediSearchClient() {
        this("redis://127.0.0.1:6379");
    }

    public RediSearchClient(String address) {
        Config config = new Config();
        config.useSingleServer().setAddress(address).setConnectionMinimumIdleSize(1);
        this.client = (Redisson) Redisson.create(config);
    }

    public RediSearch getRediSearch(String name) {
        return new RediSearch(client.getConnectionManager().getCommandExecutor(), name);
    }

    public void shutdown() {
        client.shutdown();
    }

    public void shutdown(long quietPeriod, long timeout, TimeUnit unit) {
        client.shutdown(quietPeriod, timeout, unit);
    }

    public Void flushall() {
        CommandAsyncExecutor commandExecutor = client.getConnectionManager().getCommandExecutor();
        return commandExecutor.get(commandExecutor.writeAllAsync(RedisCommands.FLUSHALL));
    }

    public Redisson getClient() {
        return client;
    }
}