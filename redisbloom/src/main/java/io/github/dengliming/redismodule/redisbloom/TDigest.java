/*
 * Copyright 2021-2024 dengliming.
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

package io.github.dengliming.redismodule.redisbloom;

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisbloom.model.TDigestInfo;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static io.github.dengliming.redismodule.redisbloom.protocol.Keywords.COMPRESSION;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_ADD;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_BYRANK;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_BYREVRANK;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_CDF;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_CREATE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_INFO;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_MAX;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_MERGE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_MIN;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_QUANTILE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_RANK;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_RESET;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_REVRANK;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_TRIMMED_MEAN;

public class TDigest extends RedissonObject {

    public TDigest(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public TDigest(CommandAsyncExecutor commandExecutor, String name) {
        this(commandExecutor.getServiceManager().getCfg().getCodec(), commandExecutor, name);
    }

    /**
     * Initializes a t-digest with specified parameters
     * <p>
     * TDIGEST.CREATE t-digest 100
     *
     * @param compression The compression parameter. 100 is a common value for normal uses. 1000 is extremely large.
     * @return
     */
    public boolean create(long compression) {
        return get(createAsync(compression));
    }

    public RFuture<Boolean> createAsync(long compression) {
        return commandExecutor.writeAsync(getName(), codec, TDIGEST_CREATE, getName(), COMPRESSION, compression);
    }

    /**
     * Reset the sketch to zero - empty out the sketch and re-initialize it.
     * <p>
     * TDIGEST.RESET {key}
     *
     * @return
     */
    public boolean reset() {
        return get(resetAsync());
    }

    public RFuture<Boolean> resetAsync() {
        return commandExecutor.writeAsync(getName(), codec, TDIGEST_RESET, getName());
    }

    /**
     * Adds one or more samples to a sketch.
     * <p>
     * TDIGEST.ADD {key} {val} {weight} [ {val} {weight} ] ...
     * <p>
     * key : The name of the sketch.
     * val : The value to add.
     * weight : The weight of this point.
     *
     * @param vals
     * @return
     */
    public boolean add(List<AbstractMap.SimpleEntry<Double, Double>> vals) {
        return get(addAsync(vals));
    }

    public RFuture<Boolean> addAsync(List<AbstractMap.SimpleEntry<Double, Double>> vals) {
        RAssert.notNull(vals, "vals must not be null");

        List<Object> args = new ArrayList<>(vals.size() * 2 + 1);
        args.add(getName());
        vals.forEach(val -> {
            args.add(val.getKey());
            args.add(val.getValue());
        });
        return commandExecutor.writeAsync(getName(), codec, TDIGEST_ADD, args.toArray());
    }

    /**
     * Get minimum value from the sketch. Will return DBL_MAX if the sketch is empty.
     * <p>
     * TDIGEST.MIN {key}
     *
     * @return
     */
    public Double getMin() {
        return get(getMinAsync());
    }

    public RFuture<Double> getMinAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_MIN, getName());
    }

    /**
     * Get maximum value from the sketch. Will return DBL_MIN if the sketch is empty.
     * <p>
     * TDIGEST.MAX {key}
     *
     * @return
     */
    public Double getMax() {
        return get(getMaxAsync());
    }

    public RFuture<Double> getMaxAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_MAX, getName());
    }

    /**
     * Returns an estimate of the cutoff such that a specified fraction of the data added to this TDigest would be less than or equal to the cutoff.
     * <p>
     * TDIGEST.QUANTILE {key} quantile [quantile ...]
     *
     * @return
     */
    public List<Double> getQuantile(double... quantiles) {
        return get(getQuantileAsync(quantiles));
    }

    public RFuture<List<Double>> getQuantileAsync(double... quantiles) {
        RAssert.notEmpty(quantiles, "quantiles must not be empty");
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_QUANTILE, buildParamsWithDoubleValues(quantiles));
    }

    /**
     * Returns the fraction of all points added which are <= value.
     * <p>
     * TDIGEST.CDF {key} value [value ...]
     *
     * @param values
     * @return
     */
    public List<Double> getCdf(double... values) {
        return get(getCdfAsync(values));
    }

    public RFuture<List<Double>> getCdfAsync(double... values) {
        RAssert.notEmpty(values, "values must not be empty");
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_CDF, buildParamsWithDoubleValues(values));
    }

    /**
     * Merges all of the values from 'this' to 'toKey' sketch.
     *
     * @param toKey
     * @return
     */
    public boolean mergeTo(String toKey) {
        return get(mergeToAsync(toKey));
    }

    public RFuture<Boolean> mergeToAsync(String toKey) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_MERGE, toKey, 1, getName());
    }

    /**
     * TDIGEST.INFO {key}
     *
     * @return Returns compression, capacity, total merged and unmerged nodes, the total compressions made up to date on that key,
     * and merged and unmerged weight.
     */
    public TDigestInfo getInfo() {
        return get(getInfoAsync());
    }

    public RFuture<TDigestInfo> getInfoAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_INFO, getName());
    }

    /**
     * TDIGEST.RANK key value [value ...]
     *
     * @since Bloom 2.4.0
     * @return An array of results populated with rank_1, rank_2, ..., rank_N.
     */
    public List<Integer> rank(double... values) {
        return get(rankAsync(values));
    }

    public RFuture<List<Integer>> rankAsync(double... values) {
        RAssert.notEmpty(values, "values must not be empty");
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_RANK, buildParamsWithDoubleValues(values));
    }

    /**
     * TDIGEST.REVRANK key value [value ...]
     *
     * @since Bloom 2.4.0
     * @return An array of results populated with rank_1, rank_2, ..., rank_N.
     */
    public List<Integer> revRank(double... values) {
        return get(revRankAsync(values));
    }

    public RFuture<List<Integer>> revRankAsync(double... values) {
        RAssert.notEmpty(values, "values must not be empty");
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_REVRANK, buildParamsWithDoubleValues(values));
    }

    /**
     * TDIGEST.BYRANK key rank [rank ...]
     *
     * @since Bloom 2.4.0
     * @return An array of results populated with rank_1, rank_2, ..., rank_N.
     */
    public List<Double> byRank(int... ranks) {
        return get(byRankAsync(ranks));
    }

    public RFuture<List<Double>> byRankAsync(int... ranks) {
        RAssert.notEmpty(ranks, "ranks must not be empty");
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_BYRANK, buildParamsWithIntValues(ranks));
    }

    /**
     * TDIGEST.BYREVRANK key reverse_rank [reverse_rank ...]
     *
     * @since Bloom 2.4.0
     * @return An array of results populated with rank_1, rank_2, ..., rank_N.
     */
    public List<Double> byRevRank(int... reverseRanks) {
        return get(byRevRankAsync(reverseRanks));
    }

    public RFuture<List<Double>> byRevRankAsync(int... reverseRanks) {
        RAssert.notEmpty(reverseRanks, "reverseRanks must not be empty");
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_BYREVRANK, buildParamsWithIntValues(reverseRanks));
    }

    /**
     * TDIGEST.TRIMMED_MEAN key low_cut_quantile high_cut_quantile
     *
     * @param lowCutQuantile Exclude observation values lower than this quantile
     * @param highCutQuantile Exclude observation values higher than this quantile
     * @since Bloom 2.4.0
     * @return The estimation of the mean value.
     */
    public String trimmedMean(double lowCutQuantile, double highCutQuantile) {
        return get(trimmedMeanAsync(lowCutQuantile, highCutQuantile));
    }

    public RFuture<String> trimmedMeanAsync(double lowCutQuantile, double highCutQuantile) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_TRIMMED_MEAN, getName(),
                lowCutQuantile, highCutQuantile);
    }

    private Object[] buildParamsWithDoubleValues(double... values) {
        List<Object> params = new ArrayList<>(values.length + 1);
        params.add(getName());
        for (double value : values) {
            params.add(value);
        }
        return params.toArray();
    }

    private Object[] buildParamsWithIntValues(int... values) {
        List<Object> params = new ArrayList<>(values.length + 1);
        params.add(getName());
        for (int value : values) {
            params.add(value);
        }
        return params.toArray();
    }
}
