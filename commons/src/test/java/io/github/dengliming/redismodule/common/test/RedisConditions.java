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

package io.github.dengliming.redismodule.common.test;

import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.StringListReplayDecoder;
import org.redisson.command.CommandAsyncExecutor;

import java.util.Map;
import java.util.Set;

public class RedisConditions {

    private static final RedisCommand COMMAND = new RedisCommand<>("COMMAND",
            new ListMultiDecoder2<>(new RedisConditionsDecoder(), new ObjectListReplayDecoder<>(),
                    new StringListReplayDecoder()));

    private static final RedisCommand MODULE_LIST = new RedisCommand<>("MODULE", "LIST",
            new ListMultiDecoder2<>(new ModuleListDecoder(), new ObjectListReplayDecoder<>()));

    private final Set<String> commands;

    private final Map<String, Long> redisModuleMap;

    public RedisConditions(CommandAsyncExecutor commandExecutor, String name) {
        commands = (Set<String>) commandExecutor.get(commandExecutor.readAsync(name, StringCodec.INSTANCE, COMMAND));

        redisModuleMap = (Map<String, Long>) commandExecutor.get(commandExecutor.readAsync(name, StringCodec.INSTANCE, MODULE_LIST));
    }

    public boolean hasCommand(String command) {
        return commands != null && commands.contains(command.toLowerCase());
    }

    public Long getModuleVersion(String name) {
        if (redisModuleMap != null) {
            return redisModuleMap.get(name);
        }
        return null;
    }

    public boolean includeModuleVersion(String name, long[] versions) {
        if (versions == null) {
            return false;
        }

        Long version = getModuleVersion(name);
        if (version == null) {
            return false;
        }

        for (long v : versions) {
            if (version.longValue() == v) {
                return true;
            }
        }
        return false;
    }
}
