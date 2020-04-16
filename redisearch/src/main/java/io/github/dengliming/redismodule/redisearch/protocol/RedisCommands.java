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

import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.VoidReplayConvertor;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand FT_CREATE = new RedisCommand<>("FT.CREATE", new VoidReplayConvertor());
    RedisCommand FT_ADD = new RedisCommand<>("FT.ADD", new VoidReplayConvertor());
    RedisCommand FT_ADDHASH = new RedisCommand<>("FT.ADDHASH", new VoidReplayConvertor());
    RedisCommand FT_ALTER = new RedisCommand<>("FT.ALTER", new VoidReplayConvertor());
    RedisCommand FT_ALIASADD = new RedisCommand<>("FT.ALIASADD", new VoidReplayConvertor());
    RedisCommand FT_ALIASUPDATE = new RedisCommand<>("FT.ALIASUPDATE", new VoidReplayConvertor());
    RedisCommand FT_ALIASDEL = new RedisCommand<>("FT.ALIASDEL", new VoidReplayConvertor());

    RedisCommand FT_INFO = new RedisCommand<>("FT.INFO", new VoidReplayConvertor());
    RedisCommand FT_SEARCH = new RedisCommand<>("FT.SEARCH", new VoidReplayConvertor());
    RedisCommand FT_AGGREGATE = new RedisCommand<>("FT.AGGREGATE", new VoidReplayConvertor());
    RedisCommand FT_EXPLAIN = new RedisCommand<>("FT.EXPLAIN", new VoidReplayConvertor());
    RedisCommand FT_EXPLAINCLI = new RedisCommand<>("FT.EXPLAINCLI", new VoidReplayConvertor());
    RedisCommand FT_DEL = new RedisCommand<>("FT.DEL", new VoidReplayConvertor());
    RedisCommand FT_GET = new RedisCommand<>("FT.GET", new VoidReplayConvertor());
    RedisCommand FT_MGET = new RedisCommand<>("FT.MGET", new VoidReplayConvertor());
    RedisCommand FT_DROP = new RedisCommand<>("FT.DROP", new VoidReplayConvertor());
    RedisCommand FT_TAGVALS = new RedisCommand<>("FT.TAGVALS", new VoidReplayConvertor());

    RedisCommand FT_SUGADD = new RedisCommand<>("FT.SUGADD", new VoidReplayConvertor());
    RedisCommand FT_SUGGET = new RedisCommand<>("FT.SUGGET", new VoidReplayConvertor());
    RedisCommand FT_SUGDEL = new RedisCommand<>("FT.SUGDEL", new VoidReplayConvertor());
    RedisCommand FT_SUGLEN = new RedisCommand<>("FT.SUGLEN", new VoidReplayConvertor());

    RedisCommand FT_OPTIMIZE = new RedisCommand<>("FT.OPTIMIZE", new VoidReplayConvertor());

    RedisCommand FT_SYNADD = new RedisCommand<>("FT.SYNADD", new VoidReplayConvertor());
    RedisCommand FT_SYNUPDATE = new RedisCommand<>("FT.SYNUPDATE", new VoidReplayConvertor());
    RedisCommand FT_SYNDUMP = new RedisCommand<>("FT.SYNDUMP", new VoidReplayConvertor());

    RedisCommand FT_SPELLCHECK = new RedisCommand<>("FT.SPELLCHECK", new VoidReplayConvertor());

    RedisCommand FT_DICTADD = new RedisCommand<>("FT.DICTADD", new VoidReplayConvertor());
    RedisCommand FT_DICTDEL = new RedisCommand<>("FT.DICTDEL", new VoidReplayConvertor());
    RedisCommand FT_DICTDUMP = new RedisCommand<>("FT.DICTDUMP", new VoidReplayConvertor());

    RedisCommand FT_CONFIG = new RedisCommand<>("FT.CONFIG", new VoidReplayConvertor());
}
