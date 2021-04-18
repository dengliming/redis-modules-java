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

package io.github.dengliming.redismodule.redistimeseries;

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redistimeseries.protocol.Keywords;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.dengliming.redismodule.redistimeseries.Sample.Value;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_ADD;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_ALTER;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_CREATE;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_CREATERULE;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_DECRBY;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_DELETERULE;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_GET;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_INCRBY;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_INFO;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_MADD;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_MGET;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_MRANGE;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_QUERYINDEX;
import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_RANGE;

/**
 * @author dengliming
 */
public class RedisTimeSeries {

    private final CommandAsyncExecutor commandExecutor;
    private final Codec codec;

    public RedisTimeSeries(CommandAsyncExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        this.codec = commandExecutor.getConnectionManager().getCodec();
    }

    /**
     * Create a new time-series with name
     *
     * @param key
     * @param options
     * @return
     */
    public boolean create(String key, TimeSeriesOptions options) {
        return commandExecutor.get(createOrAlterAsync(key, options, true));
    }

    /**
     * Update the retention, labels of an existing key.
     *
     * @param options
     * @return
     */
    public boolean alter(String key, TimeSeriesOptions options) {
        return commandExecutor.get(createOrAlterAsync(key, options, false));
    }

    public RFuture<Boolean> createOrAlterAsync(String key, TimeSeriesOptions options, boolean create) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(options, "TimeSeriesOptions must not be null");

        List<Object> args = new ArrayList<>();
        args.add(key);
        options.build(args);
        if (create) {
            return commandExecutor.writeAsync(getName(), codec, TS_CREATE, args.toArray());
        }
        return commandExecutor.writeAsync(getName(), codec, TS_ALTER, args.toArray());
    }

    /**
     * Append (or create and append) a new sample to the series.
     *
     * @param sample
     * @param options
     * @return
     */
    public Long add(Sample sample, TimeSeriesOptions options) {
        return commandExecutor.get(addAsync(sample, options));
    }

    public RFuture<Long> addAsync(Sample sample, TimeSeriesOptions options) {
        RAssert.notNull(sample, "Sample must not be null");

        List<Object> args = new ArrayList<>();
        args.add(sample.getKey());
        args.add(sample.getValue().getTimestamp());
        args.add(sample.getValue().getValue());
        if (options != null) {
            options.isAdd(true).build(args);
        }
        return commandExecutor.writeAsync(getName(), codec, TS_ADD, args.toArray());
    }

    /**
     * Append new samples to a list of series.
     *
     * @param samples
     * @return
     */
    public List<Long> add(Sample... samples) {
        return commandExecutor.get(addAsync(samples));
    }

    public RFuture<List<Long>> addAsync(Sample... samples) {
        RAssert.notEmpty(samples, "samples must not be empty");
        List<Object> args = new ArrayList<>(samples.length * 3);
        for (Sample sample : samples) {
            args.add(sample.getKey());
            args.add(sample.getValue().getTimestamp() > 0 ? sample.getValue().getTimestamp() : "*");
            args.add(sample.getValue().getValue());
        }
        return commandExecutor.writeAsync(getName(), codec, TS_MADD, args.toArray());
    }

    /**
     * Creates a new sample that increments latest sample's value.
     *
     * @param key
     * @param value
     * @return
     */
    public Long incrBy(String key, double value) {
        return this.incrBy(key, value, 0L, null);
    }

    public Long incrBy(String key, double value, long timestamp) {
        return this.incrBy(key, value, timestamp, null);
    }

    public Long incrBy(String key, double value, long timestamp, TimeSeriesOptions options) {
        return commandExecutor.get(incrByAsync(key, value, timestamp, options));
    }

    public RFuture<Long> incrByAsync(String key, double value, long timestamp, TimeSeriesOptions options) {
        RAssert.notNull(key, "key must not be empty");

        return commandExecutor.writeAsync(getName(), codec, TS_INCRBY, buildCounterArgs(key, value, timestamp, options).toArray());
    }

    /**
     * Creates a new sample that decrements the latest sample's value.
     *
     * @param key
     * @param value
     * @return
     */
    public Long decrBy(String key, double value) {
        return this.decrBy(key, value, 0L, null);
    }

    public Long decrBy(String key, double value, long timestamp) {
        return this.decrBy(key, value, timestamp, null);
    }

    public Long decrBy(String key, double value, long timestamp, TimeSeriesOptions options) {
        return commandExecutor.get(decrByAsync(key, value, timestamp, options));
    }

    public RFuture<Long> decrByAsync(String key, double value, long timestamp, TimeSeriesOptions options) {
        RAssert.notNull(key, "key must not be null");

        return commandExecutor.writeAsync(getName(), codec, TS_DECRBY, buildCounterArgs(key, value, timestamp, options).toArray());
    }

    private List<Object> buildCounterArgs(String key, double value, long timestamp, TimeSeriesOptions options) {
        List<Object> args = new ArrayList<>();
        args.add(key);
        args.add(value);
        args.add(Keywords.TIMESTAMP);
        args.add(timestamp > 0 ? timestamp : "*");
        if (options != null) {
            options.build(args);
        }
        return args;
    }

    /**
     * Create a compaction rule.
     *
     * @param sourceKey       Key name for source time series
     * @param destKey         Key name for destination time series
     * @param aggregationType Aggregation type
     * @param timeBucket      Time bucket for aggregation in milliseconds
     * @return
     */
    public boolean createRule(String sourceKey, String destKey, Aggregation aggregationType, long timeBucket) {
        return commandExecutor.get(createRuleAsync(sourceKey, destKey, aggregationType, timeBucket));
    }

    public RFuture<Boolean> createRuleAsync(String sourceKey, String destKey, Aggregation aggregationType, long timeBucket) {
        RAssert.notNull(sourceKey, "sourceKey must not be null");
        RAssert.notNull(destKey, "destKey must not be null");
        RAssert.notNull(aggregationType, "aggregationType must not be null");

        return commandExecutor.writeAsync(getName(), codec, TS_CREATERULE, sourceKey, destKey, Keywords.AGGREGATION, aggregationType.getKey(), timeBucket);
    }

    /**
     * Delete a compaction rule.
     *
     * @param sourceKey Key name for source time series
     * @param destKey   Key name for destination time series
     * @return
     */
    public boolean deleteRule(String sourceKey, String destKey) {
        return commandExecutor.get(deleteRuleAsync(sourceKey, destKey));
    }

    public RFuture<Boolean> deleteRuleAsync(String sourceKey, String destKey) {
        RAssert.notNull(sourceKey, "sourceKey must not be null");
        RAssert.notNull(destKey, "destKey must not be null");

        return commandExecutor.writeAsync(getName(), codec, TS_DELETERULE, sourceKey, destKey);
    }

    /**
     * Query a range.
     *
     * @param key
     * @param from
     * @param to
     * @return
     */
    public List<Value> range(String key, long from, long to) {
        return this.range(key, from, to, null);
    }

    public List<Value> range(String key, long from, long to, RangeOptions rangeOptions) {
        return commandExecutor.get(rangeAsync(key, from, to, rangeOptions));
    }

    public RFuture<List<Value>> rangeAsync(String key, long from, long to, RangeOptions rangeOptions) {
        List<Object> args = new ArrayList<>();
        args.add(key);
        args.add(from);
        args.add(to);
        if (rangeOptions != null) {
            rangeOptions.build(args);
        }
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TS_RANGE, args.toArray());
    }

    /**
     * Query a timestamp range across multiple time-series by filters.
     *
     * @param from
     * @param to
     * @param rangeOptions Optional args
     * @param filters
     * @return
     */
    public List<TimeSeries> mrange(long from, long to, RangeOptions rangeOptions, String... filters) {
        return commandExecutor.get(mrangeAsync(from, to, rangeOptions, filters));
    }

    public RFuture<List<TimeSeries>> mrangeAsync(long from, long to, RangeOptions rangeOptions, String... filters) {
        RAssert.notEmpty(filters, "filters must not be empty");

        List<Object> args = new ArrayList<>();
        args.add(from);
        args.add(to);
        if (rangeOptions != null) {
            rangeOptions.build(args);
        }
        args.add(Keywords.FILTER);
        for (String filter : filters) {
            args.add(filter);
        }
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TS_MRANGE, args.toArray());
    }

    /**
     * Get the last sample.
     *
     * @param key Key name for timeseries
     * @return
     */
    public Value get(String key) {
        return commandExecutor.get(getAsync(key));
    }

    public RFuture<Value> getAsync(String key) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TS_GET, key);
    }

    /**
     * Get the last samples matching the specific filter.
     *
     * @param withLabels
     * @param filters
     * @return
     */
    public List<TimeSeries> mget(boolean withLabels, String... filters) {
        return commandExecutor.get(mgetAsync(withLabels, filters));
    }

    public RFuture<List<TimeSeries>> mgetAsync(boolean withLabels, String... filters) {
        List<Object> args = new ArrayList<>(filters.length + (withLabels ? 2 : 1));
        if (withLabels) {
            args.add(Keywords.WITHLABELS);
        }
        args.add(Keywords.FILTER);
        for (String filter : filters) {
            args.add(filter);
        }

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TS_MGET, args.toArray());
    }

    /**
     * Returns information and statistics on the time-series.
     *
     * @param key Key name of the time-series.
     * @return
     */
    public Map<String, Object> info(String key) {
        return commandExecutor.get(infoAsync(key));
    }

    public RFuture<Map<String, Object>> infoAsync(String key) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TS_INFO, key);
    }

    /**
     * Get all the keys matching the filter list.
     *
     * @param filters
     * @return
     */
    public List<String> queryIndex(String... filters) {
        return commandExecutor.get(queryIndexAsync(filters));
    }

    public RFuture<List<String>> queryIndexAsync(String... filters) {
        RAssert.notEmpty(filters, "filters must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TS_QUERYINDEX, filters);
    }

    public String getName() {
        return null;
    }
}
