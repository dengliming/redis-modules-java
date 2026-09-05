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
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.command.CommandBatchService;
import org.redisson.misc.CompletableFutureWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Single point of contact with Redisson internals that are not part of its public API.
 * Everything that reaches into {@code org.redisson.command}, {@code org.redisson.misc} or the
 * service manager goes through here so a Redisson upgrade only has one place to fix.
 */
public final class RedissonAdapter {

    private RedissonAdapter() {
    }

    /**
     * The command executor of a Redisson instance. Only the concrete {@link Redisson} class exposes it.
     */
    public static CommandAsyncExecutor commandExecutor(RedissonClient redisson) {
        if (!(redisson instanceof Redisson)) {
            throw new IllegalArgumentException("Unsupported RedissonClient implementation: " + redisson.getClass().getName());
        }
        return ((Redisson) redisson).getCommandExecutor();
    }

    /**
     * The codec configured on the Redisson instance behind the executor.
     */
    public static Codec defaultCodec(CommandAsyncExecutor commandExecutor) {
        return commandExecutor.getServiceManager().getCfg().getCodec();
    }

    /**
     * True when the executor accumulates commands for a pipeline/batch instead of sending them immediately.
     * Commands issued through such an executor only complete once the batch is executed, so no follow-up
     * round trip can be chained onto their result.
     */
    public static boolean isBatch(CommandAsyncExecutor commandExecutor) {
        return commandExecutor instanceof CommandBatchService;
    }

    public static <T> RFuture<T> toRFuture(CompletableFuture<T> future) {
        return new CompletableFutureWrapper<>(future);
    }

    /**
     * Maps the result of a future. A mapper failure completes the returned future exceptionally.
     */
    public static <T, R> RFuture<R> transform(RFuture<T> future, Function<? super T, ? extends R> mapper) {
        CompletableFuture<T> source = future.toCompletableFuture();
        return toRFuture(source.thenApply(mapper));
    }

    /**
     * Chains an asynchronous continuation onto the result of a future.
     */
    public static <T, R> RFuture<R> compose(RFuture<T> future, Function<? super T, ? extends CompletionStage<R>> mapper) {
        CompletableFuture<T> source = future.toCompletableFuture();
        return toRFuture(source.thenCompose(mapper));
    }
}
