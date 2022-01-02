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

package io.github.dengliming.redismodule.common.test;

import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisConditionsDecoder implements MultiDecoder<Set<String>> {

    @Override
    public Set<String> decode(List<Object> parts, State state) {
        if (parts == null || parts.size() == 0) {
            return null;
        }
        Set<String> commands = parts.stream()
                .map(p -> (List<String>) p)
                .filter(Objects::nonNull)
                .filter(it -> it.size() > 0)
                .map(values -> values.get(0).toLowerCase())
                .collect(Collectors.toSet());
        return commands;
    }
}

