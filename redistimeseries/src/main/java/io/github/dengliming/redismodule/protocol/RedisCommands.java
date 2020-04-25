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
package io.github.dengliming.redismodule.protocol;

import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.VoidReplayConvertor;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand TS_CREATE = new RedisCommand<>("TS.CREATE", new VoidReplayConvertor());
    RedisCommand TS_ALTER = new RedisCommand<>("TS.ALTER", new VoidReplayConvertor());
    RedisCommand TS_ADD = new RedisCommand<>("TS.ADD", new VoidReplayConvertor());
    RedisCommand TS_MADD = new RedisCommand<>("TS.MADD", new VoidReplayConvertor());
    RedisCommand TS_INCRBY = new RedisCommand<>("TS.INCRBY", new VoidReplayConvertor());
    RedisCommand TS_DECRBY = new RedisCommand<>("TS.DECRBY", new VoidReplayConvertor());
    RedisCommand TS_CREATERULE = new RedisCommand<>("TS.CREATERULE", new VoidReplayConvertor());
    RedisCommand TS_DELETERULE = new RedisCommand<>("TS.DELETERULE", new VoidReplayConvertor());
    RedisCommand TS_RANGE = new RedisCommand<>("TS.RANGE", new VoidReplayConvertor());
    RedisCommand TS_MRANGE = new RedisCommand<>("TS.MRANGE", new VoidReplayConvertor());
    RedisCommand TS_GET = new RedisCommand<>("TS.GET", new VoidReplayConvertor());
    RedisCommand TS_MGET = new RedisCommand<>("TS.MGET", new VoidReplayConvertor());
    RedisCommand TS_INFO = new RedisCommand<>("TS.INFO", new VoidReplayConvertor());
    RedisCommand TS_QUERYINDEX = new RedisCommand<>("TS.QUERYINDEX", new VoidReplayConvertor());

}
