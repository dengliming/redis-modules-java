/*
 * Copyright 2024 dengliming.
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

import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Decodes GRAPH.INFO: alternating section names and section payloads, for example
 * {@code "# Running queries", [...], "# Waiting queries", [...], "Object Pool", [[name, value], ...]}.
 */
public class GraphInfoDecoder implements MultiDecoder<Map<String, Object>> {

    @Override
    public Map<String, Object> decode(List<Object> parts, State state) {
        Map<String, Object> sections = new LinkedHashMap<>();
        for (int i = 0; i + 1 < parts.size(); i += 2) {
            sections.put(String.valueOf(parts.get(i)), parts.get(i + 1));
        }
        return sections;
    }
}
