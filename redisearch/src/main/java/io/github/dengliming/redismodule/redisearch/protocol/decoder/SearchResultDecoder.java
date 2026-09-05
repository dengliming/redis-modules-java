/*
 * Copyright 2020-2022 dengliming.
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

import io.github.dengliming.redismodule.redisearch.index.Document;
import io.github.dengliming.redismodule.redisearch.search.SearchResult;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 */
public class SearchResultDecoder implements MultiDecoder<SearchResult> {

    private final boolean withScores;
    private final boolean noContent;

    public SearchResultDecoder(boolean withScores, boolean noContent) {
        this.withScores = withScores;
        this.noContent = noContent;
    }

    @Override
    public SearchResult decode(List<Object> parts, State state) {
        Long total = (Long) parts.get(0);
        // Each document occupies: key [score] [fields]. The reply is [total, doc1..., doc2..., ...]
        int documentSize = 1 + (withScores ? 1 : 0) + (noContent ? 0 : 1);

        List<Document> documents = new ArrayList<>(total.intValue());
        for (int i = 1; i + documentSize - 1 < parts.size(); i += documentSize) {
            int offset = i;
            String id = (String) parts.get(offset++);
            double score = 1.0d;
            if (withScores) {
                score = parseScore(parts.get(offset++));
            }
            Map<String, Object> fields = null;
            if (!noContent) {
                fields = (Map<String, Object>) parts.get(offset);
            }
            documents.add(new Document(id, score, fields));
        }

        return new SearchResult(total, documents);
    }

    private static double parseScore(Object raw) {
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        }
        return Double.parseDouble(String.valueOf(raw));
    }
}
