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

package io.github.dengliming.redismodule.redisgears.protocol;

import io.github.dengliming.redismodule.redisgears.protocol.decoder.ClusterInfoDecoder;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder;
import org.redisson.client.protocol.decoder.StringListReplayDecoder;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand RG_PYEXECUTE = new RedisCommand<>("RG.PYEXECUTE", new ObjectListReplayDecoder<>());
    RedisCommand RG_CONFIGGET = new RedisCommand<>("RG.CONFIGGET", new StringListReplayDecoder());
    RedisCommand RG_CONFIGSET = new RedisCommand<>("RG.CONFIGSET", new StringListReplayDecoder());
    RedisCommand RG_PYSTATS = new RedisCommand<>("RG.PYSTATS", new ObjectMapReplayDecoder<String, Object>());
    RedisCommand RG_UNREGISTER = new RedisCommand("RG.UNREGISTER", new BooleanReplayConvertor());
    RedisCommand RG_REFRESHCLUSTER = new RedisCommand("RG.REFRESHCLUSTER", new BooleanReplayConvertor());
    RedisCommand RG_DROPEXECUTION = new RedisCommand("RG.DROPEXECUTION", new BooleanReplayConvertor());
    RedisCommand RG_ABORTEXECUTION = new RedisCommand("RG.ABORTEXECUTION", new BooleanReplayConvertor());
    RedisCommand RG_INFOCLUSTER = new RedisCommand("RG.INFOCLUSTER", new ListMultiDecoder2(new ClusterInfoDecoder(), new ObjectListReplayDecoder<>(), new ObjectListReplayDecoder<>()));
}
