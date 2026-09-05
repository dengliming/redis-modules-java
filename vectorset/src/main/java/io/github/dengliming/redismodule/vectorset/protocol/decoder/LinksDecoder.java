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
 * Decodes {@code VLINKS ... WITHSCORES}: one list per HNSW layer, each holding interleaved neighbour names
 * and scores.
 */
public class LinksDecoder implements MultiDecoder<List<List<Similarity>>> {

    @Override
    @SuppressWarnings("unchecked")
    public List<List<Similarity>> decode(List<Object> parts, State state) {
        List<List<Similarity>> layers = new ArrayList<>(parts.size());
        for (Object layer : parts) {
            List<Object> entries = (List<Object>) layer;
            List<Similarity> neighbours = new ArrayList<>(entries.size() / 2);
            for (int i = 0; i + 1 < entries.size(); i += 2) {
                neighbours.add(new Similarity(String.valueOf(entries.get(i)), SimilarityDecoder.toDouble(entries.get(i + 1)), null));
            }
            layers.add(neighbours);
        }
        return layers;
    }
}
