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

import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.command.CommandAsyncExecutor;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Base class for module facades whose commands address keys passed per call (JSON, TimeSeries, AI, Gears, Graph).
 * <p>
 * Subclasses send commands through {@link #read} / {@link #write}, which take care of the routing key,
 * the codec and the read/write distinction that Redisson uses for replica routing.
 */
public abstract class AbstractRedisModule {

    /**
     * Routing key for commands that do not operate on a key (module configuration, listings, ...).
     * Redisson routes such commands to an arbitrary node.
     */
    protected static final String NO_KEY = null;

    private final CommandAsyncExecutor commandExecutor;
    private final Codec codec;

    protected AbstractRedisModule(CommandAsyncExecutor commandExecutor) {
        this(commandExecutor, RedissonAdapter.defaultCodec(commandExecutor));
    }

    protected AbstractRedisModule(CommandAsyncExecutor commandExecutor, Codec codec) {
        this.commandExecutor = commandExecutor;
        this.codec = codec;
    }

    public CommandAsyncExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public Codec getCodec() {
        return codec;
    }

    /**
     * Waits for the result of an asynchronous call, unwrapping Redis errors the way Redisson does.
     */
    protected <T> T get(RFuture<T> future) {
        return commandExecutor.get(future);
    }

    /**
     * Sends a read-only command with the module's default codec. Eligible for replica routing.
     *
     * @param key the key the command operates on, or {@link #NO_KEY}
     */
    protected <R> RFuture<R> read(String key, RedisCommand<?> command, Object... params) {
        return read(key, codec, command, params);
    }

    protected <R> RFuture<R> read(String key, Codec codec, RedisCommand<?> command, Object... params) {
        return commandExecutor.readAsync(key, codec, command, params);
    }

    /**
     * Sends a command that mutates data with the module's default codec. Always routed to a master.
     *
     * @param key the key the command operates on, or {@link #NO_KEY}
     */
    protected <R> RFuture<R> write(String key, RedisCommand<?> command, Object... params) {
        return write(key, codec, command, params);
    }

    protected <R> RFuture<R> write(String key, Codec codec, RedisCommand<?> command, Object... params) {
        return commandExecutor.writeAsync(key, codec, command, params);
    }

    protected <T, R> RFuture<R> transform(RFuture<T> future, Function<? super T, ? extends R> mapper) {
        return RedissonAdapter.transform(future, mapper);
    }

    protected <T, R> RFuture<R> compose(RFuture<T> future, Function<? super T, ? extends CompletionStage<R>> mapper) {
        return RedissonAdapter.compose(future, mapper);
    }

    /**
     * @see RedissonAdapter#isBatch(CommandAsyncExecutor)
     */
    protected boolean isBatch() {
        return RedissonAdapter.isBatch(commandExecutor);
    }
}
