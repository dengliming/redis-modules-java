/*
 * Copyright 2021 dengliming.
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

package io.github.dengliming.redismodule.redisjson;

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_DEL;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_SET;

public class RedisJSON {

    private final CommandAsyncExecutor commandExecutor;
    private final Codec codec;

    public RedisJSON(CommandAsyncExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        this.codec = commandExecutor.getConnectionManager().getCodec();
    }

    /**
     * Delete a value.
     *
     * JSON.DEL <key> [path]
     *
     * @param key
     * @param path
     * @return the number of paths deleted (0 or 1).
     */
    public long del(String key, String path) {
        return commandExecutor.get(delAsync(key, path));
    }

    public RFuture<Long> delAsync(String key, String path) {
        RAssert.notEmpty(key, "key must not be empty");

        if (path == null) {
            return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_DEL, key);
        }
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_DEL, key, path);
    }

    /**
     * Sets the JSON value at path in key.
     *
     * JSON.SET <key> <path> <json>
     *          [NX | XX]
     *
     * @param key
     * @param setArgs
     * @return Simple String OK if executed correctly.
     */
    public String set(String key, SetArgs setArgs) {
        return commandExecutor.get(setAsync(key, setArgs));
    }

    public RFuture<String> setAsync(String key, SetArgs setArgs) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(setArgs, "setArgs must not be null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_SET, setArgs.build(key).toArray());
    }

    public String getName() {
        return "";
    }
}
