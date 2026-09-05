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

package io.github.dengliming.redismodule.redisgraph.protocol;

import io.github.dengliming.redismodule.redisgraph.model.ResultSet;
import io.github.dengliming.redismodule.redisgraph.model.SlowLogItem;
import io.github.dengliming.redismodule.redisgraph.protocol.decoder.GraphInfoDecoder;
import io.github.dengliming.redismodule.redisgraph.protocol.decoder.ResultSetDecoder;
import io.github.dengliming.redismodule.redisgraph.protocol.decoder.SlowLogItemDecoder;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;
import org.redisson.client.protocol.decoder.ObjectMapReplayDecoder;

import java.util.List;
import java.util.Map;

/**
 * RedisGraph commands.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface RedisCommands {

    RedisCommand<Boolean> GRAPH_CONFIG_SET = new RedisCommand<>("GRAPH.CONFIG", "SET", new BooleanReplayConvertor());
    RedisCommand<Map<String, Object>> GRAPH_CONFIG_GET = new RedisCommand<>("GRAPH.CONFIG", "GET", new ObjectMapReplayDecoder<String, Object>());
    RedisCommand<String> GRAPH_DELETE = new RedisCommand<>("GRAPH.DELETE");
    RedisCommand<List<String>> GRAPH_LIST = new RedisCommand<>("GRAPH.LIST", new ObjectListReplayDecoder<>());
    RedisCommand<List<String>> GRAPH_PROFILE = new RedisCommand<>("GRAPH.PROFILE", new ObjectListReplayDecoder<>());
    RedisCommand<List<String>> GRAPH_EXPLAIN = new RedisCommand<>("GRAPH.EXPLAIN", new ObjectListReplayDecoder<>());
    // FalkorDB additions
    RedisCommand<Boolean> GRAPH_CONSTRAINT_CREATE = new RedisCommand<>("GRAPH.CONSTRAINT", "CREATE", new BooleanReplayConvertor());
    RedisCommand<Boolean> GRAPH_CONSTRAINT_DROP = new RedisCommand<>("GRAPH.CONSTRAINT", "DROP", new BooleanReplayConvertor());
    RedisCommand<Boolean> GRAPH_COPY = new RedisCommand<>("GRAPH.COPY", new BooleanReplayConvertor());
    RedisCommand<Map<String, Object>> GRAPH_MEMORY_USAGE = new RedisCommand<>("GRAPH.MEMORY", "USAGE", new ObjectMapReplayDecoder<String, Object>());
    RedisCommand<Map<String, Object>> GRAPH_INFO = new RedisCommand<>("GRAPH.INFO", new ListMultiDecoder2(new GraphInfoDecoder(),
            new ObjectListReplayDecoder<>(), new ObjectListReplayDecoder<>(), new ObjectListReplayDecoder<>()));
    RedisCommand<List<SlowLogItem>> GRAPH_SLOWLOG = new RedisCommand<>("GRAPH.SLOWLOG", new ListMultiDecoder2(new ObjectListReplayDecoder<>(), new SlowLogItemDecoder()));
    RedisCommand<ResultSet> GRAPH_QUERY = new RedisCommand<>("GRAPH.QUERY", new ListMultiDecoder2(
            new ResultSetDecoder(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>()
    ));
    RedisCommand<ResultSet> GRAPH_READ_ONLY_QUERY = new RedisCommand<>("GRAPH.RO_QUERY", new ListMultiDecoder2(
            new ResultSetDecoder(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>(),
            new ObjectListReplayDecoder<>()
    ));
}
