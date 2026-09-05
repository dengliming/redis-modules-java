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

package io.github.dengliming.redismodule.common;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;

/**
 * Base class of the per-module clients. A client either owns its Redisson instance (created from a
 * {@link Config}) or wraps one supplied by the caller, in which case several module clients can share
 * a single connection pool and {@link #shutdown()} leaves the shared instance untouched.
 */
public abstract class BaseRedissonClient {
    private final RedissonClient redisson;
    private final boolean ownsRedisson;

    protected BaseRedissonClient(Config config) {
        this(Redisson.create(config), true);
    }

    /**
     * Wraps an existing Redisson instance. The caller stays responsible for shutting it down.
     */
    protected BaseRedissonClient(RedissonClient redisson) {
        this(redisson, false);
    }

    private BaseRedissonClient(RedissonClient redisson, boolean ownsRedisson) {
        this.redisson = redisson;
        this.ownsRedisson = ownsRedisson;
    }

    /**
     * Flushes every database on every node. Intended for tests.
     */
    public Void flushall() {
        CommandAsyncExecutor commandExecutor = getCommandExecutor();
        return commandExecutor.get(commandExecutor.writeAllVoidAsync(RedisCommands.FLUSHALL));
    }

    /**
     * Shuts down the underlying Redisson instance if this client created it. A shared instance passed in
     * through {@link #BaseRedissonClient(RedissonClient)} is left running.
     */
    public void shutdown() {
        if (ownsRedisson) {
            redisson.shutdown();
        }
    }

    public RedissonClient getRedisson() {
        return redisson;
    }

    public CommandAsyncExecutor getCommandExecutor() {
        return RedissonAdapter.commandExecutor(redisson);
    }
}
