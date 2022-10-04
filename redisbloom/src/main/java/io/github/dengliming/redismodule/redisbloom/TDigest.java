/*
 * Copyright 2021 dengliming.
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
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_CDF;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_CREATE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_INFO;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_MAX;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_MERGE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_MIN;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_QUANTILE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.TDIGEST_RESET;

public class TDigest extends RedissonObject {

    public TDigest(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public TDigest(CommandAsyncExecutor commandExecutor, String name) {
        this(commandExecutor.getConnectionManager().getCodec(), commandExecutor, name);
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
     * TDIGEST.QUANTILE {key} {quantile}
     *
     * @return
     */
    public Double getQuantile(double quantile) {
        return get(getQuantileAsync(quantile));
    }

    public RFuture<Double> getQuantileAsync(double quantile) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_QUANTILE, getName(), quantile);
    }

    /**
     * Returns the fraction of all points added which are <= value.
     * <p>
     * TDIGEST.CDF {key} {value}
     *
     * @param value
     * @return
     */
    public Double getCdf(double value) {
        return get(getCdfAsync(value));
    }

    public RFuture<Double> getCdfAsync(double value) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_CDF, getName(), value);
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
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, TDIGEST_MERGE, toKey, getName());
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
}
