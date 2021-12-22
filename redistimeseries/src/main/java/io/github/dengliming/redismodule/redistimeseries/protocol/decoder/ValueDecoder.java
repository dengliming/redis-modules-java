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

package io.github.dengliming.redismodule.redistimeseries.protocol.decoder;

import org.redisson.client.codec.Codec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;
import static io.github.dengliming.redismodule.redistimeseries.Sample.Value;

import java.util.List;

/**
 * @author dengliming
 */
public class ValueDecoder implements MultiDecoder<Value> {

    @Override
    public Decoder<Object> getDecoder(Codec codec, int paramNum, State state) {
        return null;
    }

    @Override
    public Value decode(List<Object> parts, State state) {
        if (parts == null || parts.size() == 0) {
            return null;
        }
        return Value.of((Long) parts.get(0), Double.parseDouble((String) parts.get(1)));
    }
}
