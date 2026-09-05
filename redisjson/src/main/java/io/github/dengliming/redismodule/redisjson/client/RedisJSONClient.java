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

package io.github.dengliming.redismodule.redisjson.client;

import io.github.dengliming.redismodule.common.BaseRedissonClient;
import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.RedisJSONBatch;
import io.github.dengliming.redismodule.redisjson.codec.GsonJsonCodec;
import io.github.dengliming.redismodule.redisjson.codec.JsonCodec;
import org.redisson.api.BatchOptions;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisJSONClient extends BaseRedissonClient {

    private final JsonCodec jsonCodec;

    public RedisJSONClient(Config config) {
        this(config, GsonJsonCodec.INSTANCE);
    }

    /**
     * @param jsonCodec converts between Java objects and JSON documents for every RedisJSON obtained from this client
     */
    public RedisJSONClient(Config config, JsonCodec jsonCodec) {
        super(config);
        RAssert.notNull(jsonCodec, "jsonCodec must not be null");
        this.jsonCodec = jsonCodec;
    }

    /**
     * Wraps an existing Redisson instance so that it can be shared with other clients.
     * The caller stays responsible for shutting it down.
     */
    public RedisJSONClient(RedissonClient redisson) {
        this(redisson, GsonJsonCodec.INSTANCE);
    }

    public RedisJSONClient(RedissonClient redisson, JsonCodec jsonCodec) {
        super(redisson);
        RAssert.notNull(jsonCodec, "jsonCodec must not be null");
        this.jsonCodec = jsonCodec;
    }

    public JsonCodec getJsonCodec() {
        return jsonCodec;
    }

    public RedisJSON getRedisJSON() {
        return new RedisJSON(getCommandExecutor(), jsonCodec);
    }

    public RedisJSONBatch createRedisJSONBatch() {
        return createRedisJSONBatch(BatchOptions.defaults());
    }

    public RedisJSONBatch createRedisJSONBatch(BatchOptions options) {
        return new RedisJSONBatch(getCommandExecutor(), options, jsonCodec);
    }
}
