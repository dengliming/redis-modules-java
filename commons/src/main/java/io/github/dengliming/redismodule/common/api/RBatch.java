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

package io.github.dengliming.redismodule.common.api;

import org.redisson.api.BatchResult;
import org.redisson.api.RFuture;
import org.redisson.client.RedisException;

public interface RBatch {

    /**
     * Executes all operations accumulated during async methods invocations.
     * <p>
     * If cluster configuration used then operations are grouped by slot ids
     * and may be executed on different servers. Thus command execution order could be changed
     *
     * @return List with result object for each command
     * @throws RedisException in case of any error
     *
     */
    BatchResult<?> execute() throws RedisException;

    /**
     * Executes all operations accumulated during async methods invocations asynchronously.
     * <p>
     * In cluster configurations operations grouped by slot ids
     * so may be executed on different servers. Thus command execution order could be changed
     *
     * @return List with result object for each command
     */
    RFuture<BatchResult<?>> executeAsync();

    /**
     * Discard batched commands and release allocated buffers used for parameters encoding.
     */
    void discard();

    /**
     * Discard batched commands and release allocated buffers used for parameters encoding.
     *
     * @return void
     */
    RFuture<Void> discardAsync();
}
