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

package io.github.dengliming.redismodule.redisearch.protocol.decoder;

import io.github.dengliming.redismodule.redisearch.aggregate.AggregateResult;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 */
public class AggregateDecoder implements MultiDecoder<AggregateResult> {

    @Override
    public Decoder<Object> getDecoder(int paramNum, State state) {
        return null;
    }

    @Override
    public AggregateResult decode(List<Object> parts, State state) {
        AggregateResult aggregateResult = new AggregateResult();
        aggregateResult.setTotal((Long) parts.get(0));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 1; i < parts.size(); i++) {
            rows.add((Map<String, Object>) parts.get(i));
        }
        aggregateResult.setRows(rows);
        return aggregateResult;
    }
}
