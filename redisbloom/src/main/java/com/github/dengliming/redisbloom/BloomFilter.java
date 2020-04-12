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

import com.github.dengliming.redisbloom.model.BloomFilterInfo;
import com.github.dengliming.redisbloom.model.InsertArgs;
import com.github.dengliming.redisbloom.util.ArgsUtil;
import com.github.dengliming.redisbloom.util.RAssert;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;

import static com.github.dengliming.redisbloom.protocol.RedisCommands.*;

/**
 * @author dengliming
 */
public class BloomFilter extends RedissonObject {

    public BloomFilter(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public BloomFilter(CommandAsyncExecutor commandExecutor, String name) {
        this(commandExecutor.getConnectionManager().getCodec(), commandExecutor, name);
    }

    /**
     * Creates an empty Bloom Filter with a given desired error ratio and initial capacity
     *
     * @param errorRate The desired probability for false positives. This should be a decimal value between 0 and 1.
     * @param capacity The number of entries you intend to add to the filter
     * @return
     */
    public boolean create(double errorRate, long capacity) {
        return get(createAsync(errorRate, capacity));
    }

    public RFuture<Boolean> createAsync(double errorRate, long capacity) {
        return commandExecutor.writeAsync(getName(), codec, BF_RESERVE, getName(), errorRate, capacity);
    }

    /**
     * Adds an item to the Bloom Filter, creating the filter if it does not yet exist
     *
     * @param item
     * @return
     */
    public boolean add(String item) {
        return get(addAsync(item));
    }

    public RFuture<Boolean> addAsync(String item) {
        RAssert.notNull(item, "Item must not be null");

        return commandExecutor.writeAsync(getName(), codec, BF_ADD, getName(), item);
    }

     /**
     * Determines whether an item may exist in the Bloom Filter or not
     *
     * @param item
     * @return
     */
    public boolean exists(String item) {
        return get(existsAsync(item));
    }

    public RFuture<Boolean> existsAsync(String item) {
        RAssert.notNull(item, "Item must not be null");

        return commandExecutor.readAsync(getName(), codec, BF_EXISTS, getName(), item);
    }

    /**
     * Determines if one or more items may exist in the filter or not.
     *
     * @param items
     * @return
     */
    public List<Boolean> existsMulti(String... items) {
        return get(existsMultiAsync(items));
    }

    public RFuture<List<Boolean>> existsMultiAsync(String... items) {
        RAssert.notNull(items, "Items must not be null");

        return commandExecutor.readAsync(getName(), codec, BF_MEXISTS,  ArgsUtil.append(getName(), items));
    }

    /**
     * add one or more items to the bloom filter
     *
     * @param insertArgs {@link InsertArgs}
     * @param items
     * @return
     */
    public List<Boolean> insert(InsertArgs insertArgs, String... items) {
        return get(insertAsync(insertArgs, items));
    }

    public RFuture<List<Boolean>> insertAsync(InsertArgs insertArgs, String... items) {
        RAssert.notNull(insertArgs, "insertArgs must not be null");
        RAssert.notNull(items, "Items must not be null");

        List<Object> params = new ArrayList<>();
        params.add(getName());
        if (insertArgs.getCapacity() > 0) {
            params.add("CAPACITY");
            params.add(insertArgs.getCapacity());
        }
        if (insertArgs.getErrorRatio() > 0) {
            params.add("ERROR");
            params.add(insertArgs.getErrorRatio());
        }
        if (insertArgs.getExpansion() > 0) {
            params.add("EXPANSION");
            params.add(insertArgs.getExpansion());
        }
        if (insertArgs.getNoCreate()) {
            params.add("NOCREATE");
        }
        if (insertArgs.getNonScaling()) {
            params.add("NONSCALING");
        }
        params.add("ITEMS");
        for (String item : items) {
            params.add(item);
        }
        return commandExecutor.writeAsync(getName(), codec, BF_INSERT, params.toArray());
    }

    /**
     * Adds one or more items to the Bloom Filter
     *
     * @param items One or more items to add
     * @return
     */
    public List<Boolean> madd(String... items) {
        return get(maddAsync(items));
    }

    public RFuture<List<Boolean>> maddAsync(String... items) {
        RAssert.notEmpty(items, "Items must not be empty");

        return commandExecutor.writeAsync(getName(), codec, BF_MADD, ArgsUtil.append(getName(), items));
    }

    /**
     * Return information about key
     *
     * @return
     */
    public BloomFilterInfo getInfo() {
        return get(getInfoAsync());
    }

    public RFuture<BloomFilterInfo> getInfoAsync() {
        return commandExecutor.readAsync(getName(), codec, BF_INFO, getName());
    }
}