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

import io.github.dengliming.redismodule.redistimeseries.protocol.Keywords;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.dengliming.redismodule.redistimeseries.protocol.RedisCommands.TS_CREATE;

/**
 * @author dengliming
 */
public class RedisTimeSeries extends RedissonObject {

    public RedisTimeSeries(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public RedisTimeSeries(CommandAsyncExecutor commandExecutor, String name) {
        this(commandExecutor.getConnectionManager().getCodec(), commandExecutor, name);
    }

    /**
     * Create a new time-series with name
     *
     * @param retentionTime
     * @param unCompressed
     * @param labels
     * @return
     */
    public boolean create(long retentionTime, boolean unCompressed, String... labels) {
        return get(createAsync(retentionTime, unCompressed, labels));
    }

    public RFuture<Boolean> createAsync(long retentionTime, boolean unCompressed, String... labels) {
        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(Keywords.RETENTION);
        args.add(retentionTime);
        if (unCompressed) {
            args.add(Keywords.UNCOMPRESSED);
        }
        if (labels != null) {
            args.add(Keywords.LABELS);
            Arrays.stream(labels).forEach(args::add);
        }
        return commandExecutor.writeAsync(getName(), codec, TS_CREATE, args.toArray());
    }
}
