/*
 * Copyright 2020-2024 dengliming.
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

import io.github.dengliming.redismodule.common.AbstractRedisModule;
import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisai.args.SetModelArgs;
import io.github.dengliming.redismodule.redisai.args.StoreScriptArgs;
import io.github.dengliming.redismodule.redisai.model.Model;
import io.github.dengliming.redismodule.redisai.model.Script;
import io.github.dengliming.redismodule.redisai.model.Tensor;
import io.github.dengliming.redismodule.redisai.protocol.Keywords;
import org.redisson.api.RFuture;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_CONFIG;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_INFO;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_INFO_RESETSTAT;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_MODELDEL;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_MODELGET;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_MODELSET;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_SCRIPTDEL;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_SCRIPTGET;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_SCRIPTRUN;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_SCRIPTSET;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_SCRIPTSTORE;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_TENSORGET;
import static io.github.dengliming.redismodule.redisai.protocol.RedisCommands.AI_TENSORSET;

/**
 * @author dengliming
 */
public class RedisAI extends AbstractRedisModule {

    public RedisAI(CommandAsyncExecutor commandExecutor) {
        super(commandExecutor);
    }

    public RedisAI(CommandAsyncExecutor commandExecutor, Codec codec) {
        super(commandExecutor, codec);
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
        return get(setTensorAsync(key, type, dimensions, data, values));
    }

    public RFuture<Boolean> setTensorAsync(String key, DataType type, int[] dimensions, byte[] data, String[] values) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(type, "type must not be null");
        RAssert.notEmpty(dimensions, "dimensions must not be empty");

        List<Object> args = new ArrayList<>();
        args.add(key);
        args.add(type);
        for (int dimension : dimensions) {
            args.add(dimension);
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
        return write(key, AI_TENSORSET, args.toArray());
    }

    /**
     * stores a model as the value of a key.
     *
     * @param key
     * @param args
     * @return
     */
    public boolean setModel(String key, SetModelArgs args) {
        return get(setModelAsync(key, args));
    }

    public RFuture<Boolean> setModelAsync(String key, SetModelArgs modelArgs) {
        RAssert.notNull(key, "key must not be null");

        List<Object> args = new ArrayList<>();
        args.add(key);
        modelArgs.build(args);
        return write(key, ByteArrayCodec.INSTANCE, AI_MODELSET, args.toArray());
    }

    /**
     * stores a TorchScript as the value of a key
     *
     * @param key
     * @param device
     * @param source
     * @deprecated This command is deprecated and will not be available in future versions.
     * @return
     */
    @Deprecated
    public boolean setScript(String key, Device device, String source) {
        return this.setScript(key, device, source, null);
    }

    public boolean setScript(String key, Device device, String source, String tag) {
        return get(setScriptAsync(key, device, source, tag));
    }

    public RFuture<Boolean> setScriptAsync(String key, Device device, String script, String tag) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(device, "device must not be null");
        RAssert.notNull(script, "script must not be null");

        if (tag == null) {
            return write(key, AI_SCRIPTSET, key, device, Keywords.SOURCE, script);
        }
        return write(key, AI_SCRIPTSET, key, device, Keywords.TAG, tag, Keywords.SOURCE, script);
    }

    /**
     * runs a script stored as a key's value on its specified device
     *
     * @return
     */
    public boolean runScript(String key, String function, String[] inputs, String[] outputs) {
        return get(runScriptAsync(key, function, inputs, outputs));
    }

    public RFuture<Boolean> runScriptAsync(String key, String function, String[] inputs, String[] outputs) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(function, "function must not be null");
        RAssert.notEmpty(inputs, "inputs must not be empty");
        RAssert.notEmpty(outputs, "outputs must not be empty");

        String[] args = new String[4 + inputs.length + outputs.length];
        args[0] = key;
        args[1] = function;
        args[2] = Keywords.INPUTS.name();
        for (int i = 0; i < inputs.length; i++) {
            args[3 + i] = inputs[i];
        }
        args[3 + inputs.length] = Keywords.OUTPUTS.name();
        for (int i = 0; i < outputs.length; i++) {
            args[4 + inputs.length + i] = outputs[i];
        }
        return write(key, AI_SCRIPTRUN, args);
    }

    /**
     * deletes a model stored as a key's value
     *
     * @param key the model's key name
     * @return
     */
    public boolean deleteModel(String key) {
        return get(deleteModelAsync(key));
    }

    public RFuture<Boolean> deleteModelAsync(String key) {
        return write(key, AI_MODELDEL, key);
    }

    /**
     * deletes a script stored as a key's value
     *
     * @param key the script's key name
     * @return
     */
    public boolean deleteScript(String key) {
        return get(deleteScriptAsync(key));
    }

    public RFuture<Boolean> deleteScriptAsync(String key) {
        return write(key, AI_SCRIPTDEL, key);
    }

    /**
     * Loads the DL/ML backend
     *
     * @param backEnd
     * @param path
     * @return
     */
    public boolean loadBackend(Backend backEnd, String path) {
        return get(loadBackendAsync(backEnd, path));
    }

    public RFuture<Boolean> loadBackendAsync(Backend backEnd, String path) {
        return write(NO_KEY, StringCodec.INSTANCE, AI_CONFIG, Keywords.LOADBACKEND, backEnd, path);
    }

    /**
     * Sets the default backends path
     *
     * @param path
     * @return
     */
    public boolean setBackendPath(String path) {
        return get(setBackendPathAsync(path));
    }

    public RFuture<Boolean> setBackendPathAsync(String path) {
        return write(NO_KEY, StringCodec.INSTANCE, AI_CONFIG, Keywords.BACKENDSPATH, path);
    }

    /**
     * returns information about the execution a model or a script.
     *
     * @param key the key name of a model or script
     * @return
     */
    public Map<String, Object> getInfo(String key) {
        return get(getInfoAsync(key));
    }

    public RFuture<Map<String, Object>> getInfoAsync(String key) {
        return read(key, StringCodec.INSTANCE, AI_INFO, key);
    }

    /**
     * resets all statistics associated with the key
     *
     * @param key the key name of a model or script
     * @return
     */
    public boolean resetStat(String key) {
        return get(resetStatAsync(key));
    }

    public RFuture<Boolean> resetStatAsync(String key) {
        return write(key, StringCodec.INSTANCE, AI_INFO_RESETSTAT, key, Keywords.RESETSTAT);
    }

    /**
     * Returns a tensor stored as key's value.
     *
     * @param key the tensor's key name
     * @return
     */
    public Tensor getTensor(String key) {
        return get(getTensorAsync(key));
    }

    public RFuture<Tensor> getTensorAsync(String key) {
        RAssert.notNull(key, "key must not be null");

        return read(key, StringCodec.INSTANCE, AI_TENSORGET, key, Keywords.META, Keywords.BLOB);
    }

    /**
     * Returns a model's metadata and blob stored as a key's value.
     *
     * @param key the tensor's key name
     * @return
     */
    public Model getModel(String key) {
        return get(getModelAsync(key));
    }

    public RFuture<Model> getModelAsync(String key) {
        RAssert.notNull(key, "key must not be null");

        return read(key, StringCodec.INSTANCE, AI_MODELGET, key, Keywords.META, Keywords.BLOB);
    }

    /**
     * Returns the TorchScript stored as a key's value.
     * <p>
     * AI.SCRIPTGET <key> [META] [SOURCE]
     *
     * @param key name of key to get the Script from RedisAI server
     * @return
     */
    public Script getScript(String key) {
        return get(getScriptAsync(key));
    }

    public RFuture<Script> getScriptAsync(String key) {
        RAssert.notNull(key, "key must not be null");

        return read(key, StringCodec.INSTANCE, AI_SCRIPTGET, key, Keywords.META, Keywords.SOURCE);
    }

    /**
     * Stores a TorchScript as the value of a key
     *
     * @param key
     * @param args
     * @return
     */
    public boolean storeScript(String key, StoreScriptArgs args) {
        return get(storeScriptAsync(key, args));
    }

    public RFuture<Boolean> storeScriptAsync(String key, StoreScriptArgs storeScriptArgs) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(storeScriptArgs, "storeScriptArgs must not be null");
        RAssert.notNull(storeScriptArgs.getDevice(), "device must not be null");

        List<Object> args = new ArrayList<>();
        args.add(key);
        storeScriptArgs.build(args);
        return write(key, StringCodec.INSTANCE, AI_SCRIPTSTORE, args.toArray());
    }

}
