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

package io.github.dengliming.redismodule.redisearch.protocol;

import io.github.dengliming.redismodule.redisearch.protocol.decoder.AggregateDecoder;
import io.github.dengliming.redismodule.redisearch.protocol.decoder.MisspelledTermDecoder;
import io.github.dengliming.redismodule.redisearch.protocol.decoder.SearchResultDecoder;
import io.github.dengliming.redismodule.redisearch.protocol.decoder.StringMapInfoDecoder;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.convertor.IntegerReplayConvertor;
import org.redisson.client.protocol.convertor.LongReplayConvertor;
import org.redisson.client.protocol.convertor.VoidReplayConvertor;
import org.redisson.client.protocol.decoder.CodecDecoder;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ListResultReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder2;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand FT_CREATE = new RedisCommand<>("FT.CREATE", new BooleanReplayConvertor());
    RedisCommand FT_ADD = new RedisCommand<>("FT.ADD", new BooleanReplayConvertor());
    RedisCommand FT_ADDHASH = new RedisCommand<>("FT.ADDHASH", new BooleanReplayConvertor());
    RedisCommand FT_ALTER = new RedisCommand<>("FT.ALTER", new BooleanReplayConvertor());
    RedisCommand FT_ALIASADD = new RedisCommand<>("FT.ALIASADD", new BooleanReplayConvertor());
    RedisCommand FT_ALIASUPDATE = new RedisCommand<>("FT.ALIASUPDATE", new BooleanReplayConvertor());
    RedisCommand FT_ALIASDEL = new RedisCommand<>("FT.ALIASDEL", new BooleanReplayConvertor());

    RedisCommand FT_INFO = new RedisCommand<>("FT.INFO", new ListMultiDecoder2(new StringMapInfoDecoder(), new CodecDecoder(), new CodecDecoder()));
    RedisCommand FT_SEARCH = new RedisCommand<>("FT.SEARCH", new ListMultiDecoder2(new SearchResultDecoder(), new StringMapInfoDecoder()));
    RedisCommand FT_SEARCH_WITH_SCORES = new RedisCommand<>("FT.SEARCH", new ListMultiDecoder2(new SearchResultDecoder(true), new StringMapInfoDecoder()));
    RedisCommand FT_AGGREGATE = new RedisCommand<>("FT.AGGREGATE", new ListMultiDecoder2(new AggregateDecoder(), new ObjectMapReplayDecoder()));

    RedisCommand FT_EXPLAIN = new RedisCommand<>("FT.EXPLAIN");
    RedisCommand FT_EXPLAINCLI = new RedisCommand<>("FT.EXPLAINCLI");
    RedisCommand FT_DEL = new RedisCommand<>("FT.DEL", new BooleanReplayConvertor());
    RedisCommand FT_GET = new RedisCommand<>("FT.GET", new ObjectMapReplayDecoder());
    RedisCommand FT_MGET = new RedisCommand<>("FT.MGET", new ListMultiDecoder2(new ListResultReplayDecoder(), new ObjectMapReplayDecoder()));
    RedisCommand FT_DROP = new RedisCommand<>("FT.DROP", new BooleanReplayConvertor());
    RedisCommand FT_TAGVALS = new RedisCommand<>("FT.TAGVALS", new ObjectListReplayDecoder<>());

    RedisCommand FT_SUGADD = new RedisCommand<>("FT.SUGADD", new IntegerReplayConvertor());
    RedisCommand FT_SUGGET = new RedisCommand<>("FT.SUGGET", new VoidReplayConvertor());
    RedisCommand FT_SUGDEL = new RedisCommand<>("FT.SUGDEL", new BooleanReplayConvertor());
    RedisCommand FT_SUGLEN = new RedisCommand<>("FT.SUGLEN", new IntegerReplayConvertor());

    RedisCommand FT_SYNADD = new RedisCommand<>("FT.SYNADD", new LongReplayConvertor());
    RedisCommand FT_SYNUPDATE = new RedisCommand<>("FT.SYNUPDATE", new BooleanReplayConvertor());
    RedisCommand FT_SYNDUMP = new RedisCommand<>("FT.SYNDUMP", new ListMultiDecoder2(new ObjectMapReplayDecoder(), new ObjectListReplayDecoder()));

    RedisCommand FT_SPELLCHECK = new RedisCommand<>("FT.SPELLCHECK", new ListMultiDecoder2<>(new ObjectListReplayDecoder<>(), new MisspelledTermDecoder(), new CodecDecoder(), new CodecDecoder()));

    RedisCommand FT_DICTADD = new RedisCommand<>("FT.DICTADD", new IntegerReplayConvertor());
    RedisCommand FT_DICTDEL = new RedisCommand<>("FT.DICTDEL", new IntegerReplayConvertor());
    RedisCommand FT_DICTDUMP = new RedisCommand<>("FT.DICTDUMP", new ObjectListReplayDecoder<>());

    RedisCommand FT_CONFIG_SET = new RedisCommand<>("FT.CONFIG", "SET", new BooleanReplayConvertor());
    RedisCommand FT_CONFIG_GET = new RedisCommand<>("FT.CONFIG", "GET", new ListMultiDecoder2<>(new ObjectMapReplayDecoder2(), new CodecDecoder()));
    RedisCommand FT_CONFIG_HELP = new RedisCommand<>("FT.CONFIG", "HELP", new ListMultiDecoder2<>(new ObjectMapReplayDecoder2(), new CodecDecoder()));
}
