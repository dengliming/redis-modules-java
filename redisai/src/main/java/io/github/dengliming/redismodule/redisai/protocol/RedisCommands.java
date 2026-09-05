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

package io.github.dengliming.redismodule.redisai.protocol;

import io.github.dengliming.redismodule.redisai.model.Model;
import io.github.dengliming.redismodule.redisai.model.Script;
import io.github.dengliming.redismodule.redisai.model.Tensor;
import io.github.dengliming.redismodule.redisai.protocol.decoder.ModelDecoder;
import io.github.dengliming.redismodule.redisai.protocol.decoder.ScriptDecoder;
import io.github.dengliming.redismodule.redisai.protocol.decoder.TensorDecoder;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.convertor.VoidReplayConvertor;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder;

import java.util.Map;

/**
 * RedisAI commands.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface RedisCommands {

    RedisCommand<Boolean> AI_TENSORSET = new RedisCommand<>("AI.TENSORSET", new BooleanReplayConvertor());
    RedisCommand<Tensor> AI_TENSORGET = new RedisCommand<>("AI.TENSORGET", new ListMultiDecoder2(new TensorDecoder(), new ObjectListReplayDecoder<>()));
    RedisCommand<Boolean> AI_MODELSET = new RedisCommand<>("AI.MODELSET", new BooleanReplayConvertor());
    RedisCommand<Model> AI_MODELGET = new RedisCommand<>("AI.MODELGET", new ListMultiDecoder2(new ModelDecoder(), new ObjectListReplayDecoder<>()));
    RedisCommand<Boolean> AI_MODELDEL = new RedisCommand<>("AI.MODELDEL", new BooleanReplayConvertor());
    RedisCommand<Void> AI_MODELRUN = new RedisCommand<>("AI.MODELRUN", new VoidReplayConvertor());
    RedisCommand<Void> AI_MODELSCAN = new RedisCommand<>("AI._MODELSCAN", new VoidReplayConvertor());
    RedisCommand<Boolean> AI_SCRIPTSET = new RedisCommand<>("AI.SCRIPTSET", new BooleanReplayConvertor());
    RedisCommand<Script> AI_SCRIPTGET = new RedisCommand<>("AI.SCRIPTGET", new ListMultiDecoder2(new ScriptDecoder(), new ObjectListReplayDecoder<>()));
    RedisCommand<Boolean> AI_SCRIPTDEL = new RedisCommand<>("AI.SCRIPTDEL", new BooleanReplayConvertor());
    RedisCommand<Boolean> AI_SCRIPTRUN = new RedisCommand<>("AI.SCRIPTRUN", new BooleanReplayConvertor());
    RedisCommand<Void> AI_SCRIPTSCAN = new RedisCommand<>("AI._SCRIPTSCAN", new VoidReplayConvertor());
    RedisCommand<Void> AI_DAGRUN = new RedisCommand<>("AI.DAGRUN", new VoidReplayConvertor());
    RedisCommand<Void> AI_DAGRUN_RO = new RedisCommand<>("AI.DAGRUN_RO", new VoidReplayConvertor());
    RedisCommand<Map<String, Object>> AI_INFO = new RedisCommand<>("AI.INFO", new ObjectMapReplayDecoder());
    RedisCommand<Boolean> AI_INFO_RESETSTAT = new RedisCommand<>("AI.INFO", new BooleanReplayConvertor());
    RedisCommand<Boolean> AI_CONFIG = new RedisCommand<>("AI.CONFIG", new BooleanReplayConvertor());
    RedisCommand<Boolean> AI_SCRIPTSTORE = new RedisCommand<>("AI.SCRIPTSTORE", new BooleanReplayConvertor());
}
