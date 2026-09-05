/*
 * Copyright 2020-2024 dengliming.
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

package io.github.dengliming.redismodule.redisbloom.protocol.decoder;

import io.github.dengliming.redismodule.redisbloom.model.BloomFilterInfo;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.List;

public class BloomFilterDecoder implements MultiDecoder<BloomFilterInfo> {

    /**
     * Field names arrive as bulk strings; decode them as plain strings whatever codec the caller uses.
     */
    @Override
    public Decoder<Object> getDecoder(Codec codec, int paramNum, State state, long size) {
        return StringCodec.INSTANCE.getValueDecoder();
    }

    @Override
    public BloomFilterInfo decode(List<Object> parts, State state) {
        InfoReply info = new InfoReply(parts);
        return new BloomFilterInfo(info.getInteger("Capacity"), info.getInteger("Size"), info.getInteger("Number of filters"),
                info.getInteger("Number of items inserted"), info.getInteger("Expansion rate"));
    }
}
