/*
 * Copyright 2024 dengliming.
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

package io.github.dengliming.redismodule.vectorset.protocol;

import io.github.dengliming.redismodule.vectorset.model.RawVector;
import io.github.dengliming.redismodule.vectorset.model.Similarity;
import io.github.dengliming.redismodule.vectorset.protocol.decoder.LinksDecoder;
import io.github.dengliming.redismodule.vectorset.protocol.decoder.RawVectorDecoder;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.convertor.DoubleReplayConvertor;
import org.redisson.client.protocol.convertor.LongReplayConvertor;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder;

import java.util.List;
import java.util.Map;

/**
 * Vector set commands (Redis 8.0+). VSIM is built per call because its decoder depends on the options.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface RedisCommands {

    RedisCommand<Boolean> VADD = new RedisCommand<>("VADD", new BooleanReplayConvertor());
    RedisCommand<Boolean> VREM = new RedisCommand<>("VREM", new BooleanReplayConvertor());
    RedisCommand<Long> VCARD = new RedisCommand<>("VCARD", new LongReplayConvertor());
    RedisCommand<Long> VDIM = new RedisCommand<>("VDIM", new LongReplayConvertor());
    RedisCommand<List<Double>> VEMB = new RedisCommand("VEMB", new ObjectListReplayDecoder<Double>(), new DoubleReplayConvertor());
    RedisCommand<RawVector> VEMB_RAW = new RedisCommand<>("VEMB", new ListMultiDecoder2(new RawVectorDecoder()));
    RedisCommand<String> VGETATTR = new RedisCommand<>("VGETATTR");
    RedisCommand<Boolean> VSETATTR = new RedisCommand<>("VSETATTR", new BooleanReplayConvertor());
    RedisCommand<Map<String, Object>> VINFO = new RedisCommand<>("VINFO", new ObjectMapReplayDecoder());
    RedisCommand<List<List<String>>> VLINKS = new RedisCommand<>("VLINKS", new ListMultiDecoder2(new ObjectListReplayDecoder<>(), new ObjectListReplayDecoder<>()));
    RedisCommand<List<List<Similarity>>> VLINKS_WITHSCORES = new RedisCommand<>("VLINKS", new ListMultiDecoder2(new LinksDecoder(), new ObjectListReplayDecoder<>()));
    RedisCommand<Boolean> VISMEMBER = new RedisCommand<>("VISMEMBER", new BooleanReplayConvertor());
    RedisCommand<String> VRANDMEMBER = new RedisCommand<>("VRANDMEMBER");
    RedisCommand<List<String>> VRANDMEMBER_COUNT = new RedisCommand<>("VRANDMEMBER", new ObjectListReplayDecoder<>());
    RedisCommand<List<String>> VRANGE = new RedisCommand<>("VRANGE", new ObjectListReplayDecoder<>());
}
