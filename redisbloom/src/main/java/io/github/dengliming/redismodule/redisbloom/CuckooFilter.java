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

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisbloom.model.ChunksData;
import io.github.dengliming.redismodule.redisbloom.model.CuckooFilterInfo;
import io.github.dengliming.redismodule.redisbloom.protocol.Keywords;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;

import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_ADD;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_ADDNX;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_COUNT;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_DEL;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_EXISTS;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_INFO;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_INSERT;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_INSERTNX;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_LOADCHUNK;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_RESERVE;
import static io.github.dengliming.redismodule.redisbloom.protocol.RedisCommands.CF_SCANDUMP;

/**
 * @author dengliming
 */
public class CuckooFilter extends RedissonObject {

    public CuckooFilter(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public CuckooFilter(CommandAsyncExecutor commandExecutor, String name) {
        this(commandExecutor.getConnectionManager().getCodec(), commandExecutor, name);
    }

    /**
     * Create an empty cuckoo filter with an initial capacity of {capacity} items
     *
     * @param capacity
     * @return
     */
    public boolean reserve(long capacity) {
        return get(reserveAsync(capacity, -1L, -1L, -1L));
    }

    public boolean reserve(long capacity, long bucketSize) {
        return get(reserveAsync(capacity, bucketSize, -1L, -1L));
    }

    public boolean reserve(long capacity, long bucketSize, long maxIterations, long expansion) {
        return get(reserveAsync(capacity, bucketSize, maxIterations, expansion));
    }

    public RFuture<Boolean> reserveAsync(long capacity, long bucketSize, long maxIterations, long expansion) {
        List<Object> params = new ArrayList<>();
        params.add(getName());
        params.add(capacity);
        if (bucketSize > 0) {
            params.add(Keywords.BUCKETSIZE);
            params.add(bucketSize);
        }
        if (maxIterations > 0) {
            params.add(Keywords.MAXITERATIONS);
            params.add(maxIterations);
        }
        if (expansion > 0) {
            params.add(Keywords.EXPANSION);
            params.add(expansion);
        }
        return commandExecutor.writeAsync(getName(), codec, CF_RESERVE, params.toArray());
    }

    /**
     * Adds an item to the cuckoo filter
     *
     * @param item
     * @return
     */
    public boolean add(String item) {
        return get(addAsync(item));
    }

    public RFuture<Boolean> addAsync(String item) {
        RAssert.notEmpty(item, "Item must not be empty");

        return commandExecutor.writeAsync(getName(), codec, CF_ADD, getName(), item);
    }

    /**
     * Adds an item to a cuckoo filter if the item did not exist previously
     *
     * @param item
     * @return
     */
    public boolean addNx(String item) {
        return get(addNxAsync(item));
    }

    public RFuture<Boolean> addNxAsync(String item) {
        RAssert.notEmpty(item, "Item must not be empty");

        return commandExecutor.writeAsync(getName(), codec, CF_ADDNX, getName(), item);
    }

    /**
     * Adds one or more items to a cuckoo filter
     *
     * @param capacity
     * @param noCreate
     * @param items
     * @return
     */
    public List<Boolean> insert(long capacity, boolean noCreate, String... items) {
        return get(insertAsync(capacity, noCreate, items));
    }

    public RFuture<List<Boolean>> insertAsync(long capacity, boolean noCreate, String... items) {
        return commandExecutor.writeAsync(getName(), codec, CF_INSERT, buildInsertArgs(getName(), capacity, noCreate, items).toArray());
    }

    public List<Boolean> insertNx(long capacity, boolean noCreate, String... items) {
        return get(insertNxAsync(capacity, noCreate, items));
    }

    public RFuture<List<Boolean>> insertNxAsync(long capacity, boolean noCreate, String... items) {
        return commandExecutor.writeAsync(getName(), codec, CF_INSERTNX, buildInsertArgs(getName(), capacity, noCreate, items).toArray());
    }

    private List<Object> buildInsertArgs(String key, long capacity, boolean noCreate, String... items) {
        RAssert.notEmpty(items, "Items must not be empty");

        List<Object> params = new ArrayList<>();
        params.add(key);
        if (capacity > 0) {
            params.add(Keywords.CAPACITY);
            params.add(capacity);
        }
        if (noCreate) {
            params.add(Keywords.NOCREATE);
        }
        params.add(Keywords.ITEMS);
        for (String item : items) {
            params.add(item);
        }
        return params;
    }

    /**
     * Check if an item exists in a Cuckoo Filter
     *
     * @param item
     * @return
     */
    public boolean exists(String item) {
        return get(existsAsync(item));
    }

    public RFuture<Boolean> existsAsync(String item) {
        RAssert.notEmpty(item, "Item must not be empty");

        return commandExecutor.readAsync(getName(), codec, CF_EXISTS, getName(), item);
    }

    /**
     * Deletes an item once from the filter.
     *
     * @param item
     * @return
     */
    public Boolean delete(String item) {
        return get(deleteAsync(item));
    }

    public RFuture<Boolean> deleteAsync(String item) {
        RAssert.notEmpty(item, "Item must not be empty");

        return commandExecutor.writeAsync(getName(), codec, CF_DEL, getName(), item);
    }

    /**
     * Get the number of times an item may be in the filter.
     *
     * @param item
     * @return
     */
    public int count(String item) {
        return get(countAsync(item));
    }

    public RFuture<Integer> countAsync(String item) {
        return commandExecutor.readAsync(getName(), codec, CF_COUNT, getName(), item);
    }

    /**
     * Get information about key
     *
     * @return
     */
    public CuckooFilterInfo getInfo() {
        return get(getInfoAsync());
    }

    public RFuture<CuckooFilterInfo> getInfoAsync() {
        return commandExecutor.readAsync(getName(), codec, CF_INFO, getName());
    }

    /**
     * Begins an incremental save of the cuckoo filter.
     *
     * @param iter Iterator value. This is either 0, or the iterator from a previous invocation of this command
     * @return
     */
    public ChunksData scanDump(int iter) {
        return get(scanDumpAsync(iter));
    }

    public RFuture<ChunksData> scanDumpAsync(int iter) {
        return commandExecutor.readAsync(getName(), ByteArrayCodec.INSTANCE, CF_SCANDUMP, getName(), iter);
    }

    /**
     * Restores a filter previously saved using SCANDUMP.
     *
     * @param chunk
     * @return
     */
    public boolean loadChunk(ChunksData chunk) {
        return get(loadChunkAsync(chunk));
    }

    public RFuture<Boolean> loadChunkAsync(ChunksData chunk) {
        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, CF_LOADCHUNK, getName(), chunk.getIter(), chunk.getData());
    }
}
