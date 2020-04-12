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
package com.github.dengliming.redisbloom;

import com.github.dengliming.redisbloom.model.TopKFilterInfo;
import com.github.dengliming.redisbloom.util.ArgsUtil;
import com.github.dengliming.redisbloom.util.RAssert;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.dengliming.redisbloom.protocol.RedisCommands.*;

/**
 * @author dengliming
 */
public class TopKFilter extends RedissonObject {

    public TopKFilter(CommandAsyncExecutor commandExecutor, String name) {
       this(commandExecutor.getConnectionManager().getCodec(), commandExecutor, name);
    }

    public TopKFilter(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    /**
     * Initializes a TopK with specified parameters
     *
     * @param topk Number of top occurring items to keep
     * @param width Number of counters kept in each array
     * @param depth Number of arrays
     * @param decay The probability of reducing a counter in an occupied bucket
     * @return
     */
    public boolean reserve(int topk, int width, int depth, double decay) {
        return get(reserveAsync(topk, width, depth, decay));
    }

    public RFuture<Boolean> reserveAsync(int topk, int width, int depth, double decay) {
        return commandExecutor.writeAsync(getName(), codec, TOPK_RESERVE, getName(), topk, width, depth, decay);
    }

    /**
     * Adds an item to the data structure
     *
     * @param items
     * @return
     */
    public List<String> add(String... items) {
        return get(addAsync(items));
    }

    public RFuture<List<String>> addAsync(String... items) {
        RAssert.notEmpty(items, "Items must not be empty");

        return commandExecutor.writeAsync(getName(), codec, TOPK_ADD, ArgsUtil.append(getName(), items));
    }

    /**
     * Increase the score of an item in the data structure by increment
     *
     * @param itemIncrement
     * @return
     */
    public List<String> incrby(Map<String, Integer> itemIncrement) {
        return get(incrbyAsync(itemIncrement));
    }

    public RFuture<List<String>> incrbyAsync(Map<String, Integer> itemIncrement) {
        RAssert.notEmpty(itemIncrement, "ItemIncrement must not be empty");

        return commandExecutor.writeAsync(getName(), codec, TOPK_INCRBY,  ArgsUtil.append(getName(), itemIncrement));
    }

    /**
     * Checks whether an item is one of Top-K items
     *
     * @param items
     * @return
     */
    public List<Boolean> query(String... items) {
        return get(queryAsync(items));
    }

    public RFuture<List<Boolean>> queryAsync(String... items) {
        RAssert.notEmpty(items, "Items must not be empty");

        return commandExecutor.readAsync(getName(), codec, TOPK_QUERY, ArgsUtil.append(getName(), items));
    }

    /**
     * Returns count for an item
     *
     * @param items
     * @return
     */
    public List<Integer> count(String... items) {
        return get(countAsync(items));
    }

    public RFuture<List<Integer>> countAsync(String... items) {
        RAssert.notEmpty(items, "Items must not be empty");

        return commandExecutor.readAsync(getName(), codec, TOPK_COUNT, ArgsUtil.append(getName(), items));
    }

    /**
     * Return full list of items in Top K list.
     *
     * @return
     */
    public List<String> list() {
        return get(listAsync());
    }

    public RFuture<List<String>> listAsync() {
        return commandExecutor.readAsync(getName(), codec, TOPK_LIST, getName());
    }

    /**
     *
     * @return Returns number of required items (k), width, depth and decay values
     */
    public TopKFilterInfo getInfo() {
        return get(getInfoAsync());
    }

    public RFuture<TopKFilterInfo> getInfoAsync() {
        return commandExecutor.readAsync(getName(), codec, TOPK_INFO, getName());
    }
}
