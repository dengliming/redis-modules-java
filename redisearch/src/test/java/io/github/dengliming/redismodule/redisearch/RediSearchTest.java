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
package io.github.dengliming.redismodule.redisearch;

import io.github.dengliming.redismodule.redisearch.index.ConfigOption;
import io.github.dengliming.redismodule.redisearch.index.DocumentOptions;
import io.github.dengliming.redismodule.redisearch.index.RSLanguage;
import io.github.dengliming.redismodule.redisearch.index.*;
import io.github.dengliming.redismodule.redisearch.schema.Schema;
import io.github.dengliming.redismodule.redisearch.schema.TextField;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dengliming
 */
public class RediSearchTest extends AbstractTest {

    @Test
    public void testIndex() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("index1");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        Object index = rediSearch.loadIndex();
        System.out.println(index);
    }

    @Test
    public void testAddHash() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("index1");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "hello world");
        Redisson redisson = rediSearchClient.getClient();
        RMap<String, Object> map = redisson.getMap("foo");
        map.putAll(fields);
        assertTrue(rediSearch.addHash("foo", 1, RSLanguage.ENGLISH));
    }

    @Test
    public void testDrop() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("testDrop");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        assertTrue(rediSearch.dropIndex());
    }

    @Test
    public void testDocument() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("testDocument");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi~");
        assertTrue(rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions()));

        Map<String, Object> fieldMap = rediSearch.getDocument("doc1");
        assertNotNull(fieldMap);
        assertTrue("Hi~".equals(fieldMap.get("title")));

        List<Map<String, Object>> fieldMaps = rediSearch.getDocuments("doc1");
        assertNotNull(fieldMaps);
        assertTrue("Hi~".equals(fieldMaps.get(0).get("title")));

        assertTrue(rediSearch.deleteDocument("doc1"));
    }

    @Test
    public void testConfig() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("testDocument");
        assertTrue(rediSearch.setConfig(ConfigOption.TIMEOUT, "1000"));
        Map<String, String> configs = rediSearch.getConfig(ConfigOption.TIMEOUT);
        assertNotNull(configs);
        assertTrue("1000".equals(configs.get(ConfigOption.TIMEOUT.name())));
    }
}
