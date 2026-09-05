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

package io.github.dengliming.redismodule.vectorset.protocol.decoder;

import io.github.dengliming.redismodule.vectorset.model.Similarity;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes a flat VSIM reply: {@code element [score] [attributes]} per match, depending on
 * WITHSCORES / WITHATTRIBS.
 */
public class SimilarityDecoder implements MultiDecoder<List<Similarity>> {

    private final boolean withScores;
    private final boolean withAttribs;

    public SimilarityDecoder(boolean withScores, boolean withAttribs) {
        this.withScores = withScores;
        this.withAttribs = withAttribs;
    }

    @Override
    public List<Similarity> decode(List<Object> parts, State state) {
        int stride = 1 + (withScores ? 1 : 0) + (withAttribs ? 1 : 0);
        List<Similarity> result = new ArrayList<>(parts.size() / stride);
        for (int i = 0; i + stride - 1 < parts.size(); i += stride) {
            int offset = i;
            String element = String.valueOf(parts.get(offset++));
            Double score = null;
            if (withScores) {
                score = toDouble(parts.get(offset++));
            }
            String attributes = null;
            if (withAttribs) {
                Object raw = parts.get(offset);
                attributes = raw == null ? null : raw.toString();
            }
            result.add(new Similarity(element, score, attributes));
        }
        return result;
    }

    static Double toDouble(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        }
        return Double.parseDouble(raw.toString());
    }
}
