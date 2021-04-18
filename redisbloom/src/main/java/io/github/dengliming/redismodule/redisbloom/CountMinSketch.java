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

package io.github.dengliming.redismodule.redisbloom;

import io.github.dengliming.redismodule.common.util.ArgsUtil;
import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisbloom.model.CountMinSketchInfo;
import io.github.dengliming.redismodule.redisbloom.protocol.Keywords;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;

import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CMS_INCRBY;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CMS_INFO;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CMS_INITBYDIM;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CMS_INITBYPROB;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CMS_MERGE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CMS_QUERY;

/**
 * @author dengliming
 */
public class CountMinSketch extends RedissonObject {

    public CountMinSketch(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public CountMinSketch(CommandAsyncExecutor commandExecutor, String name) {
        this(commandExecutor.getConnectionManager().getCodec(), commandExecutor, name);
    }

    /**
     * Initializes a Count-Min Sketch
     *
     * @param width Number of counter in each array
     * @param depth Number of counter-arrays
     * @return
     */
    public boolean create(int width, int depth) {
        return get(createAsync(width, depth));
    }

    public RFuture<Boolean> createAsync(int width, int depth) {
        return commandExecutor.writeAsync(getName(), codec, CMS_INITBYDIM, getName(), width, depth);
    }

    /**
     * Initializes a Count-Min Sketch.
     *
     * @param errorRadio Estimate size of error. The error is a percent of total counted items
     * @param probability The desired probability for inflated count. This should be a decimal value between 0 and 1
     * @return
     */
    public boolean create(double errorRadio, double probability) {
        return get(createAsync(errorRadio, probability));
    }

    public RFuture<Boolean> createAsync(double errorRadio, double probability) {
        return commandExecutor.writeAsync(getName(), codec, CMS_INITBYPROB, getName(), errorRadio, probability);
    }

    /**
     * Increases the count of item by increment.
     *
     * @param items
     * @param increments
     * @return
     */
    public List<Integer> incrby(String[] items, int[] increments) {
        return get(incrbyAsync(items, increments));
    }

    public RFuture<List<Integer>> incrbyAsync(String[] items, int[] increments) {
        RAssert.notEmpty(items, "Items must not be empty");
        RAssert.notEmpty(increments, "Increments must not be empty");
        RAssert.isTrue(items.length == increments.length, "Items.length must be equal increments.length");

        List<Object> args = new ArrayList<>(items.length * 2 + 1);
        args.add(getName());
        for (int i = 0; i < items.length; i++) {
            args.add(items[i]);
            args.add(increments[i]);
        }
        return commandExecutor.writeAsync(getName(), codec, CMS_INCRBY, args.toArray());
    }

    /**
     * query count for item
     *
     * @param items The items which counter to be increased
     * @return Count of one or more items
     */
    public List<Integer> query(String... items) {
        return get(queryAsync(items));
    }

    public RFuture<List<Integer>> queryAsync(String... items) {
        RAssert.notEmpty(items, "Items must not be empty");

        return commandExecutor.readAsync(getName(), codec, CMS_QUERY, ArgsUtil.append(getName(), items));
    }

    /**
     * Merges several sketches into one sketch.
     *
     * @param keyNum Number of sketches to be merged
     * @param srcs Names of source sketches to be merged
     * @param weights Multiple of each sketch. Default =1
     * @return
     */
    public boolean merge(int keyNum, String[] srcs, Integer[] weights) {
        RAssert.notEmpty(srcs, "Srcs must not be empty");

        return get(mergeAsync(keyNum, srcs, weights));
    }

    public RFuture<Boolean> mergeAsync(int keyNum, String[] srcs, Integer[] weights) {
        List<Object> params = new ArrayList<>();
        params.add(getName());
        params.add(keyNum);
        for (String src : srcs) {
            params.add(src);
        }
        if (weights.length > 0) {
            params.add(Keywords.WEIGHTS);
            for (Integer weight : weights) {
                params.add(weight);
            }
        }
        return commandExecutor.writeAsync(getName(), codec, CMS_MERGE, params.toArray());
    }

    /**
     *
     * @return width, depth and total count of the sketch
     */
    public CountMinSketchInfo getInfo() {
        return get(getInfoAsync());
    }

    public RFuture<CountMinSketchInfo> getInfoAsync() {
        return commandExecutor.readAsync(getName(), codec, CMS_INFO, getName());
    }
}
