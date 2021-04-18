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

package io.github.dengliming.redismodule.redisbloom.protocol.decoder;

import io.github.dengliming.redismodule.redisbloom.model.CuckooFilterInfo;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.List;

/**
 * @author dengliming
 */
public class CuckooFilterDecoder implements MultiDecoder<CuckooFilterInfo> {

    @Override
    public Decoder<Object> getDecoder(int paramNum, State state) {
        return null;
    }

    @Override
    public CuckooFilterInfo decode(List<Object> parts, State state) {
        return new CuckooFilterInfo(((Long) parts.get(1)).intValue(), ((Long) parts.get(3)).intValue(), ((Long) parts.get(5)).intValue(),
                ((Long) parts.get(7)).intValue(), ((Long) parts.get(9)).intValue(), ((Long) parts.get(11)).intValue(),
                ((Long) parts.get(13)).intValue(), ((Long) parts.get(15)).intValue());
    }
}
