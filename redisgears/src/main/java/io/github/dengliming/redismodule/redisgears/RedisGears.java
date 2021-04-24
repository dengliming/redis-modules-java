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
import io.github.dengliming.redismodule.redisgears.protocol.Keywords;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;

import static io.github.dengliming.redismodule.redisgears.protocol.RedisCommands.RG_PYEXECUTE;

public class RedisGears {

    private final CommandAsyncExecutor commandExecutor;
    private final Codec codec;

    public RedisGears(CommandAsyncExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        this.codec = commandExecutor.getConnectionManager().getCodec();
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
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, RG_PYEXECUTE, args.toArray());
    }

    public String getName() {
        return "";
    }
}
