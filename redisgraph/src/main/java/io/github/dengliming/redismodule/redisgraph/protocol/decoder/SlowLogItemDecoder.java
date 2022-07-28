/*
 * Copyright 2022 dengliming.
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

package io.github.dengliming.redismodule.redisgraph.protocol.decoder;

import io.github.dengliming.redismodule.redisgraph.model.SlowLogItem;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.List;

public class SlowLogItemDecoder implements MultiDecoder<SlowLogItem> {

    @Override
    public SlowLogItem decode(List<Object> parts, State state) {
        if (parts.size() < 4) {
            return null;
        }
        return new SlowLogItem((String) parts.get(0), (String) parts.get(1), (String) parts.get(2),
                (String) parts.get(3));
    }
}
