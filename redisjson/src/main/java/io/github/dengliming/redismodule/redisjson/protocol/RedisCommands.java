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

package io.github.dengliming.redismodule.redisjson.protocol;

import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.LongReplayConvertor;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.StringDataDecoder;
import org.redisson.client.protocol.decoder.StringReplayDecoder;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand JSON_DEL = new RedisCommand<>("JSON.DEL", new LongReplayConvertor());
    RedisCommand JSON_SET = new RedisCommand<>("JSON.SET", new StringReplayDecoder());
    RedisCommand JSON_GET = new RedisCommand<>("JSON.GET", new StringDataDecoder());
    RedisCommand JSON_TYPE = new RedisCommand<>("JSON.TYPE", new StringDataDecoder());
    RedisCommand JSON_MGET = new RedisCommand<>("JSON.MGET", new ObjectListReplayDecoder<String>());
    RedisCommand JSON_NUMINCRBY = new RedisCommand<>("JSON.NUMINCRBY", new StringDataDecoder());
    RedisCommand JSON_NUMMULTBY = new RedisCommand<>("JSON.NUMMULTBY", new StringDataDecoder());
    RedisCommand JSON_STRAPPEND = new RedisCommand<>("JSON.STRAPPEND", new LongReplayConvertor());
    RedisCommand JSON_STRLEN = new RedisCommand<>("JSON.STRLEN", new LongReplayConvertor());
    RedisCommand JSON_ARRAPPEND = new RedisCommand<>("JSON.ARRAPPEND", new LongReplayConvertor());
    RedisCommand JSON_ARRLEN = new RedisCommand<>("JSON.ARRLEN", new LongReplayConvertor());
    RedisCommand JSON_ARRTRIM = new RedisCommand<>("JSON.ARRTRIM", new LongReplayConvertor());
    RedisCommand JSON_ARRINSERT = new RedisCommand<>("JSON.ARRINSERT", new LongReplayConvertor());
}
