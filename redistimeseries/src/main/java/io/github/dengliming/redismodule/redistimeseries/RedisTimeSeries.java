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
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;

import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.*;

/**
 * @author dengliming
 */
public class RedisTimeSeries {

    protected final CommandAsyncExecutor commandExecutor;
    protected final Codec codec;

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
        return commandExecutor.get(createAsync(key, options));
    }

    public RFuture<Boolean> createAsync(String key, TimeSeriesOptions options) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(options, "TimeSeriesOptions must not be null");

        List<Object> args = new ArrayList<>();
        args.add(key);
        options.build(args);
        return commandExecutor.writeAsync(getName(), codec, TS_CREATE, args.toArray());
    }

    /**
     * Update the retention, labels of an existing key.
     *
     * @param options
     * @return
     */
    public boolean alter(String key, TimeSeriesOptions options) {
        return commandExecutor.get(alterAsync(key, options));
    }

    public RFuture<Boolean> alterAsync(String key, TimeSeriesOptions options) {
        RAssert.notNull(key, "key must not be null");
        RAssert.notNull(options, "TimeSeriesOptions must not be null");

        List<Object> args = new ArrayList<>();
        args.add(key);
        options.build(args);
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
        args.add(sample.getTimestamp());
        args.add(sample.getValue());
        if (options != null) {
            options.build(args);
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
            args.add(sample.getTimestamp() > 0 ? sample.getTimestamp() : "*");
            args.add(sample.getValue());
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
     * @param sourceKey Key name for source time series
     * @param destKey Key name for destination time series
     * @param aggregationType Aggregation type
     * @param timeBucket Time bucket for aggregation in milliseconds
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
     * @param destKey Key name for destination time series
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
     * @param fromTimestamp
     * @param toTimestamp
     * @return
     */
    public Object range(String key, long fromTimestamp, long toTimestamp) {
        return this.range(key, fromTimestamp, toTimestamp, 0);
    }

    public Object range(String key, long fromTimestamp, long toTimestamp, int count) {
        return this.range(key, fromTimestamp, toTimestamp, count, null, 0L);
    }

    public Object range(String key, long fromTimestamp, long toTimestamp, int count, Aggregation aggregation, long timeBucket) {
        return commandExecutor.get(rangeAsync(key, fromTimestamp, toTimestamp, count, aggregation, timeBucket));
    }

    public RFuture<Object> rangeAsync(String key, long fromTimestamp, long toTimestamp, int count, Aggregation aggregation, long timeBucket) {
        List<Object> args = new ArrayList<>();
        args.add(key);
        args.add(fromTimestamp);
        args.add(toTimestamp);
        if (count > 0) {
            args.add(Keywords.COUNT);
            args.add(count);
        }

        if (aggregation != null) {
            args.add(Keywords.AGGREGATION);
            args.add(aggregation.getKey());
            args.add(timeBucket);
        }
        return commandExecutor.readAsync(getName(), codec, TS_RANGE, args.toArray());
    }

    public String getName() {
        return null;
    }
}
