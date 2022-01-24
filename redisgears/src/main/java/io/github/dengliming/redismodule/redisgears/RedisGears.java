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

package io.github.dengliming.redismodule.redisgears;

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisgears.model.ClusterInfo;
import io.github.dengliming.redismodule.redisgears.protocol.Keywords;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_ABORTEXECUTION;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_CONFIGGET;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_CONFIGSET;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_DROPEXECUTION;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_INFOCLUSTER;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_PYEXECUTE;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_PYSTATS;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_REFRESHCLUSTER;
import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_UNREGISTER;

public class RedisGears {

    private final CommandAsyncExecutor commandExecutor;
    private final Codec codec;

    public RedisGears(CommandAsyncExecutor commandExecutor) {
        this(commandExecutor, StringCodec.INSTANCE);
    }

    public RedisGears(CommandAsyncExecutor commandExecutor, Codec codec) {
        this.commandExecutor = commandExecutor;
        this.codec = codec;
    }

    /**
     * The RG.PYEXECUTE command executes a Python function.
     *
     * @param function the Python function.
     * @return
     */
    public Object pyExecute(String function, boolean unBlocking, String... requirements) {
        return commandExecutor.get(pyExecuteAsync(function, unBlocking, requirements));
    }

    public RFuture<Object> pyExecuteAsync(String function, boolean unBlocking, String... requirements) {
        RAssert.notEmpty(function, "function must not be empty");

        List<Object> args = new ArrayList<>();
        args.add("" + function + "");
        if (unBlocking) {
            args.add(Keywords.UNBLOCKING);
        }
        if (requirements.length > 0) {
            args.add(Keywords.REQUIREMENTS);
            args.add("\"" + String.join(" ", requirements) + "\"");
        }
        return commandExecutor.readAsync(getName(), codec, RG_PYEXECUTE, args.toArray());
    }

    /**
     * The RG.CONFIGGET command returns the value of one or more built-in configuration or a user-defined options.
     *
     * RG.CONFIGGET <key> [...]
     *
     * @param keys the configuration option to fetch
     * @return
     */
    public List<String> getConfig(String... keys) {
        return commandExecutor.get(getConfigAsync(keys));
    }

    public RFuture<List<String>> getConfigAsync(String... keys) {
        RAssert.notEmpty(keys, "keys must not be empty");

        return commandExecutor.readAsync(getName(), codec, RG_CONFIGGET, keys);
    }

    /**
     * The RG.CONFIGGET command sets the value of one ore more built-in configuration or a user-defined options.
     *
     * RG.CONFIGSET <key> <value> [...]
     *
     * @param kvs the configuration option to set
     * @return
     */
    public List<String> setConfig(Map<String, String> kvs) {
        return commandExecutor.get(setConfigAsync(kvs));
    }

    public RFuture<List<String>> setConfigAsync(Map<String, String> kvs) {
        RAssert.notEmpty(kvs, "kvs must not be empty");

        List<Object> args = new ArrayList<>(kvs.size());
        kvs.forEach((k, v) -> {
            args.add(k);
            args.add(v);
        });
        return commandExecutor.readAsync(getName(), codec, RG_CONFIGSET, args.toArray());
    }

    /**
     * The RG.PYSTATS command returns memory usage statistics from the Python interpreter.
     *
     * RG.PYSTATS
     *
     * @return
     */
    public Map<String, Object> pyStats() {
        return commandExecutor.get(pyStatsAsync());
    }

    public RFuture<Map<String, Object>> pyStatsAsync() {
        return commandExecutor.readAsync(getName(), codec, RG_PYSTATS);
    }

    /**
     * The RG.UNREGISTER command removes the registration of a function.
     *
     * RG.UNREGISTER <id>
     *
     * @param id the registration ID for removal
     * @return
     */
    public Boolean unRegister(String id) {
        return commandExecutor.get(unRegisterAsync(id));
    }

    public RFuture<Boolean> unRegisterAsync(String id) {
        RAssert.notEmpty(id, "id must not be empty");

        return commandExecutor.readAsync(getName(), codec, RG_UNREGISTER, id);
    }

    /**
     * The RG.REFRESHCLUSTER command refreshes the node's view of the cluster's topology.
     *
     * RG.REFRESHCLUSTER
     *
     * @return
     */
    public Boolean refreshCluster() {
        return commandExecutor.get(refreshClusterAsync());
    }

    public RFuture<Boolean> refreshClusterAsync() {
        return commandExecutor.readAsync(getName(), codec, RG_REFRESHCLUSTER);
    }

    /**
     * The RG.ABORTEXECUTION command aborts the execution of a function in mid-flight.
     *
     * RG.ABORTEXECUTION <id>
     *
     * @param id the execution ID to abort
     * @return
     */
    public Boolean abortExecution(String id) {
        return commandExecutor.get(abortExecutionAsync(id));
    }

    public RFuture<Boolean> abortExecutionAsync(String id) {
        return commandExecutor.readAsync(getName(), codec, RG_ABORTEXECUTION, id);
    }

    /**
     * The RG.DROPEXECUTION command removes the execution of a function from the executions list.
     *
     * RG.DROPEXECUTION <id>
     *
     * @param id the execution ID to remove
     * @return
     */
    public Boolean dropExecution(String id) {
        return commandExecutor.get(dropExecutionAsync(id));
    }

    public RFuture<Boolean> dropExecutionAsync(String id) {
        return commandExecutor.readAsync(getName(), codec, RG_DROPEXECUTION, id);
    }

    /**
     * The RG.INFOCLUSTER command outputs information about the cluster.
     *
     * RG.INFOCLUSTER
     *
     * @return
     */
    public ClusterInfo clusterInfo() {
        return commandExecutor.get(clusterInfoAsync());
    }

    public RFuture<ClusterInfo> clusterInfoAsync() {
        return commandExecutor.readAsync(getName(), codec, RG_INFOCLUSTER);
    }

    public String getName() {
        return "";
    }
}
