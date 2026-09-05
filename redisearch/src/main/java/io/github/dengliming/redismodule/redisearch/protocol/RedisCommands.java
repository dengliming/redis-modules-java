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

package io.github.dengliming.redismodule.redisearch.protocol;

import io.github.dengliming.redismodule.redisearch.aggregate.AggregateResult;
import io.github.dengliming.redismodule.redisearch.protocol.decoder.AggregateDecoder;
import io.github.dengliming.redismodule.redisearch.protocol.decoder.MisspelledTermDecoder;
import io.github.dengliming.redismodule.redisearch.protocol.decoder.StringMapInfoDecoder;
import io.github.dengliming.redismodule.redisearch.search.MisspelledTerm;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.convertor.IntegerReplayConvertor;
import org.redisson.client.protocol.convertor.LongReplayConvertor;
import org.redisson.client.protocol.decoder.CodecDecoder;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ListResultReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder2;
import org.redisson.client.protocol.decoder.StringListReplayDecoder;

import java.util.List;
import java.util.Map;

/**
 * RediSearch commands. FT.SEARCH and FT.SUGGET are built per call because their decoder depends on the
 * options sent with the command.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface RedisCommands {

    RedisCommand<Boolean> FT_CREATE = new RedisCommand<>("FT.CREATE", new BooleanReplayConvertor());
    RedisCommand<Boolean> FT_ADD = new RedisCommand<>("FT.ADD", new BooleanReplayConvertor());
    RedisCommand<Boolean> FT_ADDHASH = new RedisCommand<>("FT.ADDHASH", new BooleanReplayConvertor());
    RedisCommand<Boolean> FT_ALTER = new RedisCommand<>("FT.ALTER", new BooleanReplayConvertor());
    RedisCommand<Boolean> FT_ALIASADD = new RedisCommand<>("FT.ALIASADD", new BooleanReplayConvertor());
    RedisCommand<Boolean> FT_ALIASUPDATE = new RedisCommand<>("FT.ALIASUPDATE", new BooleanReplayConvertor());
    RedisCommand<Boolean> FT_ALIASDEL = new RedisCommand<>("FT.ALIASDEL", new BooleanReplayConvertor());
    // Redis 8 replies nest attributes and index options several levels deep; one CodecDecoder per level
    RedisCommand<Map<String, Object>> FT_INFO = new RedisCommand<>("FT.INFO", new ListMultiDecoder2(new StringMapInfoDecoder(),
            new CodecDecoder(), new CodecDecoder(), new CodecDecoder(), new CodecDecoder(), new CodecDecoder(), new CodecDecoder(), new CodecDecoder()));
    RedisCommand<AggregateResult> FT_AGGREGATE = new RedisCommand<>("FT.AGGREGATE", new ListMultiDecoder2(new AggregateDecoder(), new ObjectMapReplayDecoder()));
    RedisCommand<String> FT_EXPLAIN = new RedisCommand<>("FT.EXPLAIN");
    RedisCommand<List<String>> FT_EXPLAINCLI = new RedisCommand<>("FT.EXPLAINCLI", new ObjectListReplayDecoder<>());
    RedisCommand<Boolean> FT_DEL = new RedisCommand<>("FT.DEL", new BooleanReplayConvertor());
    RedisCommand<Map<String, Object>> FT_GET = new RedisCommand<>("FT.GET", new ObjectMapReplayDecoder());
    RedisCommand<List<Map<String, Object>>> FT_MGET = new RedisCommand<>("FT.MGET", new ListMultiDecoder2(new ListResultReplayDecoder(), new ObjectMapReplayDecoder()));
    RedisCommand<Boolean> FT_DROP = new RedisCommand<>("FT.DROP", new BooleanReplayConvertor());
    RedisCommand<List<String>> FT_TAGVALS = new RedisCommand<>("FT.TAGVALS", new ObjectListReplayDecoder<>());
    RedisCommand<Integer> FT_SUGADD = new RedisCommand<>("FT.SUGADD", new IntegerReplayConvertor());
    RedisCommand<Boolean> FT_SUGDEL = new RedisCommand<>("FT.SUGDEL", new BooleanReplayConvertor());
    RedisCommand<Integer> FT_SUGLEN = new RedisCommand<>("FT.SUGLEN", new IntegerReplayConvertor());
    RedisCommand<Long> FT_SYNADD = new RedisCommand<>("FT.SYNADD", new LongReplayConvertor());
    RedisCommand<Boolean> FT_SYNUPDATE = new RedisCommand<>("FT.SYNUPDATE", new BooleanReplayConvertor());
    RedisCommand<Map<String, List<Long>>> FT_SYNDUMP = new RedisCommand<>("FT.SYNDUMP", new ListMultiDecoder2(new ObjectMapReplayDecoder(), new ObjectListReplayDecoder()));
    RedisCommand<List<MisspelledTerm>> FT_SPELLCHECK = new RedisCommand<>("FT.SPELLCHECK",
            new ListMultiDecoder2(new ObjectListReplayDecoder<>(), new MisspelledTermDecoder(), new CodecDecoder(), new CodecDecoder()));
    RedisCommand<Integer> FT_DICTADD = new RedisCommand<>("FT.DICTADD", new IntegerReplayConvertor());
    RedisCommand<Integer> FT_DICTDEL = new RedisCommand<>("FT.DICTDEL", new IntegerReplayConvertor());
    RedisCommand<List<String>> FT_DICTDUMP = new RedisCommand<>("FT.DICTDUMP", new ObjectListReplayDecoder<>());
    RedisCommand<Boolean> FT_CONFIG_SET = new RedisCommand<>("FT.CONFIG", "SET", new BooleanReplayConvertor());
    RedisCommand<Map<String, String>> FT_CONFIG_GET = new RedisCommand<>("FT.CONFIG", "GET", new ListMultiDecoder2(new ObjectMapReplayDecoder2(), new CodecDecoder()));
    RedisCommand<Map<String, String>> FT_CONFIG_HELP = new RedisCommand<>("FT.CONFIG", "HELP", new ListMultiDecoder2(new ObjectMapReplayDecoder2(), new CodecDecoder()));
    RedisCommand<List<String>> FT_LIST = new RedisCommand<>("FT._LIST", new StringListReplayDecoder());
}
