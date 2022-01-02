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

package io.github.dengliming.redismodule.examples.redisearch;

import io.github.dengliming.redismodule.redisearch.RediSearch;
import io.github.dengliming.redismodule.redisearch.client.RediSearchClient;
import io.github.dengliming.redismodule.redisearch.index.Document;
import io.github.dengliming.redismodule.redisearch.index.DocumentOptions;
import io.github.dengliming.redismodule.redisearch.index.RSLanguage;
import io.github.dengliming.redismodule.redisearch.index.schema.Field;
import io.github.dengliming.redismodule.redisearch.index.schema.FieldType;
import io.github.dengliming.redismodule.redisearch.index.schema.Schema;
import io.github.dengliming.redismodule.redisearch.index.schema.TextField;
import io.github.dengliming.redismodule.redisearch.search.GeoFilter;
import io.github.dengliming.redismodule.redisearch.search.NumericFilter;
import io.github.dengliming.redismodule.redisearch.search.SearchOptions;
import io.github.dengliming.redismodule.redisearch.search.SearchResult;
import org.redisson.config.Config;

import java.util.HashMap;
import java.util.Map;

public class Examples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RediSearchClient rediSearchClient = new RediSearchClient(config);

        RediSearch rediSearch = rediSearchClient.getRediSearch("testSearch");
        rediSearch.createIndex(new Schema()
                .addField(new TextField("title"))
                .addField(new TextField("content"))
                .addField(new Field("age", FieldType.NUMERIC))
                .addField(new Field("location", FieldType.GEO)));

        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi");
        fields.put("content", "OOOO");
        rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions());

        // Search with NumericFilter
        SearchResult searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .filter(new NumericFilter("age", 1, 4)));

        // Search with GeoFilter
        searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .filter(new GeoFilter("location", 15, 37, 200, GeoFilter.Unit.KILOMETERS)));
    }
}
