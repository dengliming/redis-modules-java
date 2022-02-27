/*
 * Copyright 2021-2022 dengliming.
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
import io.github.dengliming.redismodule.redisjson.args.GetArgs;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.misc.RPromise;
import org.redisson.misc.RedissonPromise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_ARRAPPEND;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_ARRINDEX;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_ARRINSERT;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_ARRLEN;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_ARRPOP;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_ARRTRIM;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_DEL;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_GET;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_MGET;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_NUMINCRBY;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_NUMMULTBY;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_OBJKEYS;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_OBJLEN;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_SET;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_STRAPPEND;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_STRLEN;
import static io.github.dengliming.redismodule.redisjson.protocol.RedisCommands.JSON_TYPE;

public class RedisJSON {

    private static final Map<String, Class> CLASS_TYPE_MAPPING;
    private final CommandAsyncExecutor commandExecutor;
    private final Codec codec;

    static {
        CLASS_TYPE_MAPPING = new HashMap<>();
        CLASS_TYPE_MAPPING.put("null", null);
        CLASS_TYPE_MAPPING.put("boolean", boolean.class);
        CLASS_TYPE_MAPPING.put("integer", int.class);
        CLASS_TYPE_MAPPING.put("number", float.class);
        CLASS_TYPE_MAPPING.put("string", String.class);
        CLASS_TYPE_MAPPING.put("object", Object.class);
        CLASS_TYPE_MAPPING.put("array", List.class);
    }

    public RedisJSON(CommandAsyncExecutor commandExecutor) {
        this(commandExecutor, commandExecutor.getConnectionManager().getCodec());
    }

    public RedisJSON(CommandAsyncExecutor commandExecutor, Codec codec) {
        this.commandExecutor = commandExecutor;
        this.codec = codec;
    }

    /**
     * Delete a value.
     * <p>
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
     * <p>
     * JSON.SET <key> <path> <json>
     * [NX | XX]
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

        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, JSON_SET, setArgs.build(key).toArray());
    }

    /**
     * Return the value at path in JSON serialized form.
     * <p>
     * JSON.GET <key>
     * [INDENT indentation-string]
     * [NEWLINE line-break-string]
     * [SPACE space-string]
     * [NOESCAPE]
     * [path ...]
     *
     * @param key
     * @param clazz
     * @param getArgs
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz, GetArgs getArgs) {
        return commandExecutor.get(getAsync(key, clazz, getArgs));
    }

    public <T> RFuture<T> getAsync(String key, Class<T> clazz, GetArgs getArgs) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(clazz, "clazz must not be null");
        RAssert.notNull(getArgs, "getArgs must not be null");

        RFuture<String> getFuture = commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_GET, getArgs.build(key).toArray());
        return transformRPromiseResult(getFuture, clazz);
    }

    /**
     * Returns the values at path from multiple key s. Non-existing keys and non-existing paths are reported as null.
     */
    public <T> List<T> mget(String path, Class<T> clazz, String... keys) {
        return commandExecutor.get(mgetAsync(path, clazz, keys));
    }

    public <T> RFuture<List<T>> mgetAsync(String path, Class<T> clazz, String... keys) {
        RAssert.notEmpty(keys, "keys must not be empty");
        RAssert.notEmpty(path, "path must not be empty");
        RAssert.notNull(clazz, "clazz must not be null");

        RPromise result = new RedissonPromise<T>();
        List<String> args = new ArrayList<>(keys.length + 1);
        for (String key : keys) {
            args.add(key);
        }
        args.add(path);
        RFuture<List<String>> getFuture = commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_MGET, args.toArray());
        getFuture.onComplete((res, e) -> {
            if (e != null) {
                result.tryFailure(e);
                return;
            }

            try {
                result.trySuccess(res.stream().map(it -> GsonUtils.fromJson(it, clazz)).collect(Collectors.toList()));
            } catch (Throwable t) {
                result.tryFailure(t);
            }
        });
        return result;
    }

    /**
     * Report the type of JSON value at path.
     * <p>
     * JSON.TYPE <key> [path]
     *
     * @param key
     * @param path
     * @return
     */
    public Class getType(String key, String path) {
        return commandExecutor.get(getTypeAsync(key, path));
    }

    public RFuture<Class> getTypeAsync(String key, String path) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        RPromise result = new RedissonPromise<Class>();
        RFuture<String> getFuture = commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_TYPE, key, path);
        getFuture.onComplete((res, e) -> {
            if (e != null) {
                result.tryFailure(e);
                return;
            }

            try {
                result.trySuccess(Optional.ofNullable(CLASS_TYPE_MAPPING.get(res)).orElseThrow(() -> new RuntimeException("Unknown type " + res)));
            } catch (Throwable t) {
                result.tryFailure(t);
            }
        });
        return result;
    }

    /**
     * Increments the number value stored at path by number.
     * <p>
     * JSON.NUMINCRBY <key> <path> <number>
     *
     * @param key
     * @param path
     * @param num
     * @return Bulk String, specifically the stringified new value.
     */
    public String incrBy(String key, String path, long num) {
        return commandExecutor.get(incrByAsync(key, path, num));
    }

    public RFuture<String> incrByAsync(String key, String path, long num) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, JSON_NUMINCRBY, key, path, num);
    }

    /**
     * Multiplies the number value stored at path by number.
     * <p>
     * JSON.NUMMULTBY <key> <path> <number>
     *
     * @param key
     * @param path
     * @param num
     * @return Bulk String, specifically the stringified new value.
     */
    public String multBy(String key, String path, long num) {
        return commandExecutor.get(multByAsync(key, path, num));
    }

    public RFuture<String> multByAsync(String key, String path, long num) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, JSON_NUMMULTBY, key, path, num);
    }

    /**
     * Append the json-string value(s) the string at path.
     * <p>
     * JSON.STRAPPEND <key> [path] <json-string>
     *
     * @param key
     * @param path
     * @param object
     * @return
     */
    public long strAppend(String key, String path, Object object) {
        return commandExecutor.get(strAppendAsync(key, path, object));
    }

    public RFuture<Long> strAppendAsync(String key, String path, Object object) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");
        RAssert.notNull(object, "object must not be null");

        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, JSON_STRAPPEND, key, path, GsonUtils.toJson(object));
    }

    /**
     * Return the length of the JSON String at path in key.
     * <p>
     * JSON.STRLEN <key> [path]
     *
     * @param key
     * @param path
     * @return
     */
    public long strLen(String key, String path) {
        return commandExecutor.get(strLenAsync(key, path));
    }

    public RFuture<Long> strLenAsync(String key, String path) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_STRLEN, key, path);
    }

    /**
     * Append the json value(s) into the array at path after the last element in it.
     * <p>
     * JSON.ARRAPPEND <key> <path> <json> [json ...]
     *
     * @param key
     * @param path
     * @return
     */
    public long arrAppend(String key, String path, Object... objects) {
        return commandExecutor.get(arrAppendAsync(key, path, objects));
    }

    public RFuture<Long> arrAppendAsync(String key, String path, Object... objects) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");
        RAssert.notEmpty(objects, "objects must not be empty");

        List<Object> args = new ArrayList<>();
        args.add(key);
        args.add(path);
        for (Object object : objects) {
            args.add(GsonUtils.toJson(object));
        }
        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, JSON_ARRAPPEND, args.toArray());
    }

    /**
     * Insert the json value(s) into the array at path before the index (shifts to the right).
     * <p>
     * JSON.ARRINSERT <key> <path> <index> <json> [json ...]
     *
     * @param key
     * @param path
     * @return
     */
    public long arrInsert(String key, String path, long index, Object... objects) {
        return commandExecutor.get(arrInsertAsync(key, path, index, objects));
    }

    public RFuture<Long> arrInsertAsync(String key, String path, long index, Object... objects) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");
        RAssert.notEmpty(objects, "objects must not be empty");

        List<Object> args = new ArrayList<>();
        args.add(key);
        args.add(path);
        args.add(index);
        for (Object object : objects) {
            args.add(GsonUtils.toJson(object));
        }
        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, JSON_ARRINSERT, args.toArray());
    }

    /**
     * Return the length of the JSON Array at path in key.
     * <p>
     * JSON.ARRLEN <key> [path]
     *
     * @param key
     * @param path
     * @return
     */
    public long arrLen(String key, String path) {
        return commandExecutor.get(arrLenAsync(key, path));
    }

    public RFuture<Long> arrLenAsync(String key, String path) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_ARRLEN, key, path);
    }

    /**
     * Trim an array so that it contains only the specified inclusive range of elements.
     * <p>
     * JSON.ARRTRIM <key> <path> <start> <stop>
     *
     * @param key
     * @param path
     * @param start
     * @param stop
     * @return
     */
    public long arrTrim(String key, String path, long start, long stop) {
        return commandExecutor.get(arrTrimAsync(key, path, start, stop));
    }

    public RFuture<Long> arrTrimAsync(String key, String path, long start, long stop) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_ARRTRIM, key, path, start, stop);
    }

    /**
     * Finds the index of the first occurrence of a scalar JSON value in the array at the given path.
     *
     * JSON.ARRINDEX <key> <path> <json-scalar> [start [stop]]
     *
     * @param key
     * @param path
     * @param scalar
     * @param start
     * @param stop
     * @return
     */
    public long arrIndex(String key, String path, Object scalar, long start, long stop) {
        return commandExecutor.get(arrIndexAsync(key, path, scalar, start, stop));
    }

    public RFuture<Long> arrIndexAsync(String key, String path, Object scalar, long start, long stop) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");
        RAssert.notNull(scalar, "scalar must not be null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_ARRINDEX, key, path,
                GsonUtils.toJson(scalar), start, stop);
    }

    /**
     * Remove and return element from the index in the array.
     *
     * JSON.ARRPOP <key> [path [index]]
     *
     * @param key
     * @param path
     * @param clazz
     * @param index
     * @param <T>
     * @return
     */
    public <T> T arrPop(String key, String path, Class<T> clazz, long index) {
        return commandExecutor.get(arrPopAsync(key, path, clazz, index));
    }

    public <T> RFuture<T> arrPopAsync(String key, String path, Class<T> clazz, long index) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");
        RAssert.notNull(clazz, "clazz must not be null");

        RFuture<String> getFuture = commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_ARRPOP, key, path, index);
        return transformRPromiseResult(getFuture, clazz);
    }

    private <T> RPromise transformRPromiseResult(RFuture<String> getFuture, Class<T> clazz) {
        RPromise result = new RedissonPromise<Class<T>>();
        getFuture.onComplete((res, e) -> {
            if (e != null) {
                result.tryFailure(e);
                return;
            }

            try {
                result.trySuccess(GsonUtils.fromJson(res, clazz));
            } catch (Throwable t) {
                result.tryFailure(t);
            }
        });
        return result;
    }

    /**
     * Report the number of keys in the JSON Object at path in key.
     *
     * JSON.OBJLEN <key> [path]
     *
     * @param key
     * @param path
     * @return
     */
    public Long objLen(String key, String path) {
        return commandExecutor.get(objLenAsync(key, path));
    }

    public RFuture<Long> objLenAsync(String key, String path) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_OBJLEN, key, path);
    }

    /**
     * Return the keys in the object that's referenced by path.
     *
     * JSON.OBJKEYS <key> [path]
     *
     * @param key
     * @param path
     * @return
     */
    public List<Object> objKeys(String key, String path) {
        return commandExecutor.get(objKeysAsync(key, path));
    }

    public RFuture<List<Object>> objKeysAsync(String key, String path) {
        RAssert.notEmpty(key, "key must not be empty");
        RAssert.notNull(path, "path must not be null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, JSON_OBJKEYS, key, path);
    }

    public String getName() {
        return "";
    }
}
