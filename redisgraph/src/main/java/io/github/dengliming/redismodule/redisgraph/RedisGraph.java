/*
 * Copyright 2022-2024 dengliming.
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

package io.github.dengliming.redismodule.redisgraph;

import io.github.dengliming.redismodule.common.AbstractRedisModule;
import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisgraph.enums.ConstraintType;
import io.github.dengliming.redismodule.redisgraph.enums.EntityType;
import io.github.dengliming.redismodule.redisgraph.model.Record;
import io.github.dengliming.redismodule.redisgraph.model.ResultSet;
import io.github.dengliming.redismodule.redisgraph.model.SlowLogItem;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.dengliming.redismodule.redisgraph.protocol.Keywords.PROPERTIES;
import static io.github.dengliming.redismodule.redisgraph.protocol.Keywords.SAMPLES;
import static io.github.dengliming.redismodule.redisgraph.protocol.Keywords.__COMPACT;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_CONFIG_GET;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_CONFIG_SET;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_CONSTRAINT_CREATE;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_CONSTRAINT_DROP;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_COPY;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_DELETE;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_EXPLAIN;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_INFO;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_LIST;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_MEMORY_USAGE;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_PROFILE;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_QUERY;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_READ_ONLY_QUERY;
import static io.github.dengliming.redismodule.redisgraph.protocol.RedisCommands.GRAPH_SLOWLOG;

/**
 * Graph commands for FalkorDB and RedisGraph 2.x (wire compatible). Results of GRAPH.QUERY / GRAPH.RO_QUERY are
 * requested in compact form; property names and relationship types are resolved through a per-graph cache.
 * GRAPH.CONSTRAINT, GRAPH.COPY, GRAPH.MEMORY USAGE and GRAPH.INFO are FalkorDB only.
 */
public class RedisGraph extends AbstractRedisModule {

    private final Map<String, GraphCache> caches = new ConcurrentHashMap<>();

    public RedisGraph(CommandAsyncExecutor commandExecutor) {
        super(commandExecutor);
    }

    public RedisGraph(CommandAsyncExecutor commandExecutor, Codec codec) {
        super(commandExecutor, codec);
    }

    /**
     * Retrieves the current value of a RedisGraph configuration parameter.
     * <p>
     * GRAPH.CONFIG GET name
     *
     * @param parameter
     * @return
     */
    public Map<String, Object> getConfig(String parameter) {
        return get(getConfigAsync(parameter));
    }

    public RFuture<Map<String, Object>> getConfigAsync(String parameter) {
        RAssert.notEmpty(parameter, "parameter must not be empty");

        return read(NO_KEY, StringCodec.INSTANCE, GRAPH_CONFIG_GET, parameter);
    }

    /**
     * Set the value of a RedisGraph configuration parameter.
     * <p>
     * GRAPH.CONFIG SET name value
     *
     * @param name
     * @param value
     * @return
     */
    public Boolean setConfig(String name, Object value) {
        return get(setConfigAsync(name, value));
    }

    public RFuture<Boolean> setConfigAsync(String name, Object value) {
        RAssert.notEmpty(name, "name must not be empty");
        RAssert.notNull(value, "value must not be null");

        return write(NO_KEY, GRAPH_CONFIG_SET, name, value);
    }

    /**
     * Completely removes the graph and all of its entities.
     * <p>
     * GRAPH.DELETE graph
     *
     * @param name
     * @return
     */
    public String delete(String name) {
        return get(deleteAsync(name));
    }

    public RFuture<String> deleteAsync(String name) {
        RAssert.notEmpty(name, "name must not be empty");

        return write(name, StringCodec.INSTANCE, GRAPH_DELETE, name);
    }

    /**
     * Lists all graph keys in the keyspace.
     * <p>
     * GRAPH.LIST
     *
     * @return
     */
    public List<String> list() {
        return get(listAsync());
    }

    public RFuture<List<String>> listAsync() {
        return read(NO_KEY, StringCodec.INSTANCE, GRAPH_LIST);
    }

    /**
     * Executes a query and produces an execution plan augmented with metrics for each operation's execution.
     * <p>
     * GRAPH.PROFILE graph query [TIMEOUT timeout]
     *
     * @return
     */
    public List<String> profile(String graphName, String query, long timeout) {
        return get(profileAsync(graphName, query, timeout));
    }

    public RFuture<List<String>> profileAsync(String graphName, String query, long timeout) {
        if (timeout > 0) {
            return read(graphName, StringCodec.INSTANCE, GRAPH_PROFILE, graphName, query, timeout);
        }
        return read(graphName, StringCodec.INSTANCE, GRAPH_PROFILE, graphName, query);
    }


    /**
     * Constructs a query execution plan but does not run it. Inspect this execution plan to better understand how
     * your query will get executed.
     * <p>
     * GRAPH.EXPLAIN graph query
     *
     * @return
     */
    public List<String> explain(String graphName, String query) {
        return get(explainAsync(graphName, query));
    }

    public RFuture<List<String>> explainAsync(String graphName, String query) {
        return read(graphName, StringCodec.INSTANCE, GRAPH_EXPLAIN, graphName, query);
    }

    /**
     * Returns a list containing up to 10 of the slowest queries issued against the given graph ID.
     * <p>
     * GRAPH.SLOWLOG graph
     *
     * @return
     */
    public List<SlowLogItem> slowLog(String graphName) {
        return get(slowLogAsync(graphName));
    }

    public RFuture<List<SlowLogItem>> slowLogAsync(String graphName) {
        return read(graphName, StringCodec.INSTANCE, GRAPH_SLOWLOG, graphName);
    }

    /**
     * Executes the given query against a specified graph.
     * <p>
     * GRAPH.QUERY graph query [TIMEOUT timeout]
     *
     * @return
     */
    public ResultSet query(String graphName, String query, long timeout) {
        return get(queryAsync(graphName, query, timeout));
    }

    public RFuture<ResultSet> queryAsync(String graphName, String query, long timeout) {
        return resolveNames(graphName, rawQueryAsync(GRAPH_QUERY, graphName, query, timeout));
    }

    /**
     * Executes a given read only query against a specified graph.
     * <p>
     * GRAPH.RO_QUERY graph query [TIMEOUT timeout]
     *
     * @return
     */
    public ResultSet readOnlyQuery(String graphName, String query, long timeout) {
        return get(readOnlyQueryAsync(graphName, query, timeout));
    }

    public RFuture<ResultSet> readOnlyQueryAsync(String graphName, String query, long timeout) {
        return resolveNames(graphName, rawQueryAsync(GRAPH_READ_ONLY_QUERY, graphName, query, timeout));
    }


    /**
     * Creates a constraint (FalkorDB). Constraints are enforced asynchronously once the server has validated the
     * existing data; a UNIQUE constraint needs an exact-match index on the same properties first.
     * <p>
     * GRAPH.CONSTRAINT CREATE key MANDATORY|UNIQUE NODE label|RELATIONSHIP type PROPERTIES count prop...
     */
    public boolean createConstraint(String graphName, ConstraintType type, EntityType entity, String labelOrType, String... properties) {
        return get(createConstraintAsync(graphName, type, entity, labelOrType, properties));
    }

    public RFuture<Boolean> createConstraintAsync(String graphName, ConstraintType type, EntityType entity, String labelOrType,
                                                  String... properties) {
        return write(graphName, StringCodec.INSTANCE, GRAPH_CONSTRAINT_CREATE, constraintArgs(graphName, type, entity, labelOrType, properties));
    }

    /**
     * Drops a constraint (FalkorDB).
     * <p>
     * GRAPH.CONSTRAINT DROP key MANDATORY|UNIQUE NODE label|RELATIONSHIP type PROPERTIES count prop...
     */
    public boolean dropConstraint(String graphName, ConstraintType type, EntityType entity, String labelOrType, String... properties) {
        return get(dropConstraintAsync(graphName, type, entity, labelOrType, properties));
    }

    public RFuture<Boolean> dropConstraintAsync(String graphName, ConstraintType type, EntityType entity, String labelOrType,
                                                String... properties) {
        return write(graphName, StringCodec.INSTANCE, GRAPH_CONSTRAINT_DROP, constraintArgs(graphName, type, entity, labelOrType, properties));
    }

    private Object[] constraintArgs(String graphName, ConstraintType type, EntityType entity, String labelOrType, String... properties) {
        RAssert.notEmpty(graphName, "graphName must not be empty");
        RAssert.notNull(type, "type must not be null");
        RAssert.notNull(entity, "entity must not be null");
        RAssert.notEmpty(labelOrType, "labelOrType must not be empty");
        RAssert.notEmpty(properties, "properties must not be empty");

        List<Object> args = new ArrayList<>(6 + properties.length);
        args.add(graphName);
        args.add(type.name());
        args.add(entity.name());
        args.add(labelOrType);
        args.add(PROPERTIES);
        args.add(properties.length);
        args.addAll(java.util.Arrays.asList(properties));
        return args.toArray();
    }

    /**
     * Copies a graph to a new key while the source stays available (FalkorDB).
     * <p>
     * GRAPH.COPY src dest
     */
    public boolean copy(String sourceGraph, String destinationGraph) {
        return get(copyAsync(sourceGraph, destinationGraph));
    }

    public RFuture<Boolean> copyAsync(String sourceGraph, String destinationGraph) {
        RAssert.notEmpty(sourceGraph, "sourceGraph must not be empty");
        RAssert.notEmpty(destinationGraph, "destinationGraph must not be empty");

        return write(sourceGraph, StringCodec.INSTANCE, GRAPH_COPY, sourceGraph, destinationGraph);
    }

    /**
     * Memory consumption of a graph in megabytes, broken down by component (FalkorDB).
     * <p>
     * GRAPH.MEMORY USAGE key [SAMPLES count]
     */
    public Map<String, Object> memoryUsage(String graphName) {
        return get(memoryUsageAsync(graphName, 0));
    }

    public Map<String, Object> memoryUsage(String graphName, int samples) {
        return get(memoryUsageAsync(graphName, samples));
    }

    public RFuture<Map<String, Object>> memoryUsageAsync(String graphName, int samples) {
        RAssert.notEmpty(graphName, "graphName must not be empty");

        if (samples > 0) {
            return read(graphName, StringCodec.INSTANCE, GRAPH_MEMORY_USAGE, graphName, SAMPLES, samples);
        }
        return read(graphName, StringCodec.INSTANCE, GRAPH_MEMORY_USAGE, graphName);
    }

    /**
     * Running and waiting queries plus object pool statistics (FalkorDB), keyed by section name.
     * <p>
     * GRAPH.INFO [RunningQueries] [WaitingQueries] [ObjectPool]
     *
     * @param sections sections to return; none means all
     */
    public Map<String, Object> info(String... sections) {
        return get(infoAsync(sections));
    }

    public RFuture<Map<String, Object>> infoAsync(String... sections) {
        return read(NO_KEY, StringCodec.INSTANCE, GRAPH_INFO, (Object[]) sections);
    }

    private RFuture<ResultSet> rawQueryAsync(RedisCommand<ResultSet> command, String graphName, String query, long timeout) {
        RAssert.notEmpty(graphName, "graphName must not be empty");
        RAssert.notNull(query, "query must not be null");

        if (timeout > 0) {
            return read(graphName, StringCodec.INSTANCE, command, graphName, query, timeout, __COMPACT);
        }
        return read(graphName, StringCodec.INSTANCE, command, graphName, query, __COMPACT);
    }

    /**
     * The compact protocol returns property keys and relationship types as indices. Look the names up
     * (cached per graph, refreshed on demand through the db.* procedures) and fill them into the entities.
     * <p>
     * Inside a batch the follow-up procedure call cannot be issued, so names stay unresolved there.
     */
    private RFuture<ResultSet> resolveNames(String graphName, RFuture<ResultSet> future) {
        if (isBatch()) {
            return future;
        }
        GraphCache cache = caches.computeIfAbsent(graphName, k -> new GraphCache());
        return compose(future, resultSet -> cache.resolve(resultSet, procedure -> callProcedure(graphName, procedure)));
    }

    private CompletableFuture<List<String>> callProcedure(String graphName, String procedure) {
        RFuture<ResultSet> raw = rawQueryAsync(GRAPH_QUERY, graphName, procedure, 0L);
        RFuture<List<String>> names = transform(raw, resultSet -> {
            List<String> result = new ArrayList<>();
            if (resultSet.getResults() != null) {
                for (Record record : resultSet.getResults()) {
                    result.add(String.valueOf(record.getValue(0)));
                }
            }
            return result;
        });
        return names.toCompletableFuture();
    }

}
