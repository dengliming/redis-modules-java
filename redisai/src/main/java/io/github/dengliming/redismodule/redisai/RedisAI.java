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
package io.github.dengliming.redismodule.redisai;

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisai.protocol.Keywords;
import org.redisson.api.RFuture;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;

import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.*;

/**
 * @author dengliming
 */
public class RedisAI {

    protected final CommandAsyncExecutor commandExecutor;
    protected final Codec codec;

    public RedisAI(CommandAsyncExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        this.codec = commandExecutor.getConnectionManager().getCodec();
    }

    /**
     * stores a tensor as the value of a key
     *
     * @param key
     * @param type
     * @param dimensions
     * @param data
     * @param values
     * @return
     */
    public boolean setTensor(String key, DataType type, int[] dimensions, byte[] data, String[] values) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(type, "type must not be null");
        RAssert.notEmpty(dimensions, "dimensions must not be empty");

        return commandExecutor.get(setTensorAsync(key, type, dimensions, data, values));
    }

    public RFuture<Boolean> setTensorAsync(String key, DataType type, int[] dimensions, byte[] data, String[] values) {
        List<Object> args = new ArrayList<>();
        args.add(key);
        if (dimensions != null) {
            args.add(type);
            for (int dimension : dimensions) {
                args.add(dimension);
            }
        }
        if (data != null) {
            args.add(Keywords.BLOB);
            args.add(data);
        }
        if (values != null) {
            args.add(Keywords.VALUES);
            for (String value : values) {
                args.add(value);
            }
        }
        return commandExecutor.writeAsync(getName(), codec, AI_TENSORSET, args.toArray());
    }

    public boolean setModel(String key, Backend backEnd, Device device, byte[] model) {
        return commandExecutor.get(setModelAsync());
    }

    public RFuture<Boolean> setModelAsync() {
        return commandExecutor.writeAsync(getName(), codec, AI_TENSORSET);
    }

    /**
     * stores a TorchScript as the value of a key
     *
     * @param key
     * @param device
     * @param source
     * @return
     */
    public boolean setScript(String key, Device device, String source) {
        return this.setScript(key, device, source, null);
    }

    public boolean setScript(String key, Device device, String source, String tag) {
        return commandExecutor.get(setScriptAsync(key, device, source, tag));
    }

    public RFuture<Boolean> setScriptAsync(String key, Device device, String script, String tag) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(device, "device must not be null");
        RAssert.notNull(script, "script must not be null");

        if (tag == null) {
            return commandExecutor.writeAsync(getName(), codec, AI_SCRIPTSET, key, device, Keywords.SOURCE, script);
        }
        return commandExecutor.writeAsync(getName(), codec, AI_SCRIPTSET, key, device, Keywords.TAG, tag, Keywords.SOURCE, script);
    }

    /**
     * deletes a model stored as a key's value
     *
     * @param key the model's key name
     * @return
     */
    public boolean deleteModel(String key) {
        return commandExecutor.get(deleteModelAsync(key));
    }

    public RFuture<Boolean> deleteModelAsync(String key) {
        return commandExecutor.writeAsync(getName(), codec, AI_MODELDEL, key);
    }

    /**
     * deletes a script stored as a key's value
     *
     * @param key the script's key name
     * @return
     */
    public boolean deleteScript(String key) {
        return commandExecutor.get(deleteScriptAsync(key));
    }

    public RFuture<Boolean> deleteScriptAsync(String key) {
        return commandExecutor.writeAsync(getName(), codec, AI_SCRIPTDEL, key);
    }

    /**
     * Loads the DL/ML backend
     *
     * @param backEnd
     * @param path
     * @return
     */
    public boolean loadBackend(Backend backEnd, String path) {
        return commandExecutor.get(loadBackendAsync(backEnd, path));
    }

    public RFuture<Boolean> loadBackendAsync(Backend backEnd, String path) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, AI_CONFIG, Keywords.LOADBACKEND, backEnd, path);
    }

    /**
     * Sets the default backends path
     *
     * @param path
     * @return
     */
    public boolean setBackendPath(String path) {
        return commandExecutor.get(setBackendPathAsync(path));
    }

    public RFuture<Boolean> setBackendPathAsync(String path) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, AI_CONFIG, Keywords.BACKENDSPATH, path);
    }

    public String getName() {
        return null;
    }
}
