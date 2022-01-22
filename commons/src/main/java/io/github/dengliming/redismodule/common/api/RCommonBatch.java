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

import org.redisson.api.BatchOptions;
import org.redisson.api.BatchResult;
import org.redisson.api.RFuture;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.command.CommandBatchService;

public abstract class RCommonBatch implements RBatch {

    private final CommandBatchService executorService;

    public RCommonBatch(CommandAsyncExecutor executor, BatchOptions options) {
        this.executorService = new CommandBatchService(executor, options);
    }

    public CommandBatchService getExecutorService() {
        return executorService;
    }

    @Override
    public BatchResult<?> execute() {
        return executorService.execute();
    }

    @Override
    public RFuture<BatchResult<?>> executeAsync() {
        return executorService.executeAsync();
    }

    @Override
    public void discard() {
        executorService.discard();
    }

    @Override
    public RFuture<Void> discardAsync() {
        return executorService.discardAsync();
    }
}
