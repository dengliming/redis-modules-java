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

package io.github.dengliming.redismodule.redisbloom.protocol;

import io.github.dengliming.redismodule.redisbloom.model.BloomFilterInfo;
import io.github.dengliming.redismodule.redisbloom.model.ChunksData;
import io.github.dengliming.redismodule.redisbloom.model.CountMinSketchInfo;
import io.github.dengliming.redismodule.redisbloom.model.CuckooFilterInfo;
import io.github.dengliming.redismodule.redisbloom.model.TopKFilterInfo;
import io.github.dengliming.redismodule.redisbloom.protocol.decoder.BloomFilterDecoder;
import io.github.dengliming.redismodule.redisbloom.protocol.decoder.ChunksDecoder;
import io.github.dengliming.redismodule.redisbloom.protocol.decoder.CountMinSketchDecoder;
import io.github.dengliming.redismodule.redisbloom.protocol.decoder.CuckooFilterDecoder;
import io.github.dengliming.redismodule.redisbloom.protocol.decoder.TopKFilterDecoder;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.convertor.IntegerReplayConvertor;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;

import java.util.List;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand<Boolean> BF_RESERVE = new RedisCommand<>("BF.RESERVE", new BooleanReplayConvertor());
    RedisCommand<Boolean> BF_ADD = new RedisCommand<>("BF.ADD", new BooleanReplayConvertor());
    RedisCommand<List<Boolean>> BF_MADD = new RedisCommand("BF.MADD", new ObjectListReplayDecoder<Boolean>(), new BooleanReplayConvertor());
    RedisCommand<List<Boolean>> BF_INSERT = new RedisCommand("BF.INSERT", new ObjectListReplayDecoder<Boolean>(), new BooleanReplayConvertor());
    RedisCommand<Boolean> BF_EXISTS = new RedisCommand<>("BF.EXISTS", new BooleanReplayConvertor());
    RedisCommand<List<Boolean>> BF_MEXISTS = new RedisCommand("BF.MEXISTS", new ObjectListReplayDecoder<Boolean>(), new BooleanReplayConvertor());
    RedisCommand<ChunksData> BF_SCANDUMP = new RedisCommand<>("BF.SCANDUMP", new ListMultiDecoder2(new ChunksDecoder()));
    RedisCommand<Boolean> BF_LOADCHUNK = new RedisCommand<>("BF.LOADCHUNK", new BooleanReplayConvertor());
    RedisCommand<BloomFilterInfo> BF_INFO = new RedisCommand<>("BF.INFO", new ListMultiDecoder2(new BloomFilterDecoder()));

    RedisCommand<Boolean> CF_RESERVE = new RedisCommand<>("CF.RESERVE", new BooleanReplayConvertor());
    RedisCommand<Boolean> CF_ADD = new RedisCommand<>("CF.ADD", new BooleanReplayConvertor());
    RedisCommand<Boolean> CF_ADDNX = new RedisCommand<>("CF.ADDNX", new BooleanReplayConvertor());
    RedisCommand<List<Boolean>> CF_INSERT = new RedisCommand("CF.INSERT", new ObjectListReplayDecoder<Boolean>(), new BooleanReplayConvertor());
    RedisCommand<List<Boolean>> CF_INSERTNX = new RedisCommand("CF.INSERTNX", new ObjectListReplayDecoder<Boolean>(), new BooleanReplayConvertor());
    RedisCommand<Boolean> CF_EXISTS = new RedisCommand<>("CF.EXISTS", new BooleanReplayConvertor());
    RedisCommand<Boolean> CF_DEL = new RedisCommand<>("CF.DEL", new BooleanReplayConvertor());
    RedisCommand<Integer> CF_COUNT = new RedisCommand<>("CF.COUNT", new IntegerReplayConvertor());
    RedisCommand<ChunksData> CF_SCANDUMP = new RedisCommand<>("CF.SCANDUMP", new ListMultiDecoder2(new ChunksDecoder()));
    RedisCommand<Boolean> CF_LOADCHUNK = new RedisCommand("CF.LOADCHUNK", new BooleanReplayConvertor());
    RedisCommand<CuckooFilterInfo> CF_INFO = new RedisCommand<>("CF.INFO", new ListMultiDecoder2(new CuckooFilterDecoder()));

    RedisCommand<Boolean> CMS_INITBYDIM = new RedisCommand<>("CMS.INITBYDIM", new BooleanReplayConvertor());
    RedisCommand<Boolean> CMS_INITBYPROB = new RedisCommand<>("CMS.INITBYPROB", new BooleanReplayConvertor());
    RedisCommand<List<Integer>> CMS_INCRBY = new RedisCommand("CMS.INCRBY", new ObjectListReplayDecoder(), new IntegerReplayConvertor());
    RedisCommand<List<Integer>> CMS_QUERY = new RedisCommand("CMS.QUERY", new ObjectListReplayDecoder(), new IntegerReplayConvertor());
    RedisCommand<Boolean> CMS_MERGE = new RedisCommand<>("CMS.MERGE", new BooleanReplayConvertor());
    RedisCommand<CountMinSketchInfo> CMS_INFO = new RedisCommand("CMS.INFO", new ListMultiDecoder2(new CountMinSketchDecoder()));

    RedisCommand<Boolean> TOPK_RESERVE = new RedisCommand<>("TOPK.RESERVE", new BooleanReplayConvertor());
    RedisCommand<List<String>> TOPK_ADD = new RedisCommand<>("TOPK.ADD", new ObjectListReplayDecoder<>());
    RedisCommand<List<String>> TOPK_INCRBY = new RedisCommand<>("TOPK.INCRBY", new ObjectListReplayDecoder());
    RedisCommand<List<Boolean>> TOPK_QUERY = new RedisCommand<>("TOPK.QUERY", new ObjectListReplayDecoder(), new BooleanReplayConvertor());
    RedisCommand<List<Integer>> TOPK_COUNT = new RedisCommand<>("TOPK.COUNT", new ObjectListReplayDecoder(), new IntegerReplayConvertor());
    RedisCommand<List<String>> TOPK_LIST = new RedisCommand("TOPK.LIST", new ObjectListReplayDecoder());
    RedisCommand<TopKFilterInfo> TOPK_INFO = new RedisCommand<>("TOPK.INFO", new ListMultiDecoder2(new TopKFilterDecoder()));
}
