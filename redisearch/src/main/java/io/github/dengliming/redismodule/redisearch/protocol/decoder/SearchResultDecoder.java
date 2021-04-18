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

import io.github.dengliming.redismodule.redisearch.index.Document;
import io.github.dengliming.redismodule.redisearch.search.SearchResult;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 */
public class SearchResultDecoder implements MultiDecoder<SearchResult> {

    @Override
    public Decoder<Object> getDecoder(int paramNum, State state) {
        return null;
    }

    @Override
    public SearchResult decode(List<Object> parts, State state) {
        Long total = (Long) parts.get(0);
        boolean noContent = total.longValue() == parts.size() + 1;
        List<Document> documents = new ArrayList<>(total.intValue());
        for (int i = 1; i < parts.size(); i++) {
            if (noContent) {
                documents.add(new Document((String) parts.get(i), 1.0d, null));
            } else if ((i + 1) % 2 != 0) {
                documents.add(new Document((String) parts.get(i - 1), 1.0d, (Map<String, Object>) parts.get(i)));
            }
        }
        return new SearchResult(total, documents);
    }
}
