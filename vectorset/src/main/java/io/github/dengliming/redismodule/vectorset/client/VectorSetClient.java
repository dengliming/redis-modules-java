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

package io.github.dengliming.redismodule.vectorset.client;

import io.github.dengliming.redismodule.common.BaseRedissonClient;
import io.github.dengliming.redismodule.vectorset.VectorSet;
import io.github.dengliming.redismodule.vectorset.VectorSetBatch;
import org.redisson.api.BatchOptions;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class VectorSetClient extends BaseRedissonClient {

    public VectorSetClient(Config config) {
        super(config);
    }

    /**
     * Wraps an existing Redisson instance so that it can be shared with other clients.
     * The caller stays responsible for shutting it down.
     */
    public VectorSetClient(RedissonClient redisson) {
        super(redisson);
    }

    public VectorSet getVectorSet(String name) {
        return new VectorSet(getCommandExecutor(), name);
    }

    public VectorSetBatch createVectorSetBatch() {
        return createVectorSetBatch(BatchOptions.defaults());
    }

    public VectorSetBatch createVectorSetBatch(BatchOptions options) {
        return new VectorSetBatch(getCommandExecutor(), options);
    }
}
