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

package io.github.dengliming.redismodule.redistimeseries.protocol;

import io.github.dengliming.redismodule.redistimeseries.protocol.decoder.TimeSeriesDecoder;
import io.github.dengliming.redismodule.redistimeseries.protocol.decoder.ValueDecoder;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.convertor.LongReplayConvertor;
import org.redisson.client.protocol.decoder.CodecDecoder;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder;
import org.redisson.client.protocol.decoder.StringListReplayDecoder;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand TS_CREATE = new RedisCommand<>("TS.CREATE", new BooleanReplayConvertor());
    RedisCommand TS_ALTER = new RedisCommand<>("TS.ALTER", new BooleanReplayConvertor());
    RedisCommand TS_ADD = new RedisCommand<>("TS.ADD", new LongReplayConvertor());
    RedisCommand TS_MADD = new RedisCommand<>("TS.MADD", new ObjectListReplayDecoder<>());
    RedisCommand TS_INCRBY = new RedisCommand<>("TS.INCRBY", new LongReplayConvertor());
    RedisCommand TS_DECRBY = new RedisCommand<>("TS.DECRBY", new LongReplayConvertor());
    RedisCommand TS_CREATERULE = new RedisCommand<>("TS.CREATERULE", new BooleanReplayConvertor());
    RedisCommand TS_DELETERULE = new RedisCommand<>("TS.DELETERULE", new BooleanReplayConvertor());
    RedisCommand TS_RANGE = new RedisCommand<>("TS.RANGE", new ListMultiDecoder2(new ObjectListReplayDecoder<>(), new ValueDecoder()));
    RedisCommand TS_MRANGE = new RedisCommand<>("TS.MRANGE", new ListMultiDecoder2(new TimeSeriesDecoder(), new CodecDecoder(), new CodecDecoder(), new CodecDecoder()));
    RedisCommand TS_GET = new RedisCommand<>("TS.GET", new ValueDecoder());
    RedisCommand TS_MGET = new RedisCommand<>("TS.MGET", new ListMultiDecoder2(new TimeSeriesDecoder(), new CodecDecoder(), new CodecDecoder(), new CodecDecoder()));
    RedisCommand TS_INFO = new RedisCommand<>("TS.INFO", new ListMultiDecoder2(new ObjectMapReplayDecoder(), new ObjectListReplayDecoder<>(), new ObjectListReplayDecoder<>()));
    RedisCommand TS_QUERYINDEX = new RedisCommand<>("TS.QUERYINDEX", new StringListReplayDecoder());

}
