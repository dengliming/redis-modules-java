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

package io.github.dengliming.redismodule.redisearch.protocol.decoder;

import io.github.dengliming.redismodule.redisearch.index.Suggestion;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes the flat FT.SUGGET reply: {@code term [score] [payload]} per suggestion, depending on
 * WITHSCORES / WITHPAYLOADS.
 */
public class SuggestionDecoder implements MultiDecoder<List<Suggestion>> {

    private final boolean withScores;
    private final boolean withPayloads;

    public SuggestionDecoder(boolean withScores, boolean withPayloads) {
        this.withScores = withScores;
        this.withPayloads = withPayloads;
    }

    @Override
    public List<Suggestion> decode(List<Object> parts, State state) {
        int stride = 1 + (withScores ? 1 : 0) + (withPayloads ? 1 : 0);
        List<Suggestion> suggestions = new ArrayList<>(parts.size() / stride);
        for (int i = 0; i + stride - 1 < parts.size(); i += stride) {
            int offset = i;
            String term = (String) parts.get(offset++);
            double score = 1.0d;
            if (withScores) {
                Object raw = parts.get(offset++);
                score = raw instanceof Number ? ((Number) raw).doubleValue() : Double.parseDouble(String.valueOf(raw));
            }
            String payload = null;
            if (withPayloads) {
                Object raw = parts.get(offset);
                payload = raw == null ? null : raw.toString();
            }
            suggestions.add(new Suggestion(term, score, payload));
        }
        return suggestions;
    }
}
