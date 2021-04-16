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
import io.github.dengliming.redismodule.redisearch.index.schema.Field;
import io.github.dengliming.redismodule.redisearch.index.schema.FieldType;
import io.github.dengliming.redismodule.redisearch.index.schema.Schema;
import io.github.dengliming.redismodule.redisearch.index.schema.TextField;
import io.github.dengliming.redismodule.redisearch.search.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author dengliming
 */
public class RediSearchTest extends AbstractTest {

    @Test
    public void testIndex() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("index1");
        assertTrue(rediSearch.createIndex(
                new Schema()
                        .addField(new TextField("title")),
                new IndexOptions()
                        .maxTextFields()
						.prefixes(Arrays.asList("doc:"))
                        .stopwords(Arrays.asList("kk"))));
        Map<String, Object> indexInfo = rediSearch.loadIndex();
        assertNotNull(indexInfo);
        assertEquals("index1", indexInfo.get("index_name"));
        assertEquals("MAXTEXTFIELDS", ((List) (indexInfo.get("index_options"))).get(0));
        assertEquals("title", ((List<List<Object>>) (indexInfo.get("fields"))).get(0).get(0));
    }

    @Test
	@Disabled("FT_ADDHASH No longer suppoted")
    public void testAddHash() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("index1");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "hello world");
        RMap<String, Object> map = rediSearchClient.getMap("foo");
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
        RediSearch rediSearch = rediSearchClient.getRediSearch("testConfig");
        assertTrue(rediSearch.setConfig(ConfigOption.TIMEOUT, "1000"));
        Map<String, String> configs = rediSearch.getConfig(ConfigOption.TIMEOUT);
        assertNotNull(configs);
        assertTrue("1000".equals(configs.get(ConfigOption.TIMEOUT.name())));
    }

    @Test
    public void testSearch() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("testSearch");
        assertTrue(rediSearch.createIndex(new Schema()
                .addField(new TextField("title"))
                .addField(new TextField("content"))
                .addField(new Field("age", FieldType.NUMERIC))
                .addField(new Field("location", FieldType.GEO))));
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi");
        fields.put("content", "OOOO");
        assertTrue(rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions()));

        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("title", "Hi guy");
        fields2.put("content", "hello world");
        assertTrue(rediSearch.addDocument(new Document(String.format("doc2"), 0.2d, fields2), new DocumentOptions()));

        SearchResult searchResult = rediSearch.search("Hi", new SearchOptions());
        assertEquals(2, searchResult.getTotal());

        searchResult = rediSearch.search("OOOO", new SearchOptions().noStopwords().language(RSLanguage.ENGLISH));
        assertEquals(1, searchResult.getTotal());

        Map<String, Object> fields3 = new HashMap<>();
        fields3.put("title", "hello");
        fields3.put("content", "test number");
        fields3.put("age", 3);
        fields3.put("location", "13.361389,38.115556");
        assertTrue(rediSearch.addDocument(new Document(String.format("doc3"), 0.3d, fields3), new DocumentOptions()));

        // Search with NumericFilter
        searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .filter(new NumericFilter("age", 1, 4)));
        assertEquals(1, searchResult.getTotal());

        // Search with GeoFilter
        searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .filter(new GeoFilter("location", 15, 37, 200, GeoFilter.Unit.KILOMETERS)));
        assertEquals(1, searchResult.getTotal());
    }

    @Test
    public void testAlias() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("testAlias");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        assertTrue(rediSearch.addAlias("TEST"));
        assertTrue(rediSearch.updateAlias("HI"));
        assertTrue(rediSearch.deleteAlias("HI"));
    }

    @Test
    public void testDict() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("testDict");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        assertEquals(2, rediSearch.addDict("TEST", "A", "B"));
        assertEquals(1, rediSearch.deleteDict("TEST", "A"));
        List<String> dicts = rediSearch.dumpDict("TEST");
        assertNotNull(dicts);
        assertEquals(1, dicts.size());
        assertEquals("B", dicts.get(0));
    }

    @Test
	@Disabled("FT_SYNADD No longer suppoted, use FT.SYNUPDATE.")
    public void testSynonym() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("testSynonym");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi~");
        long gid = rediSearch.addSynonym("a", "b", "c");
        assertTrue(gid >= 0);

        assertTrue(rediSearch.updateSynonym(gid, "c", "d"));

        Map<String, List<Long>> synonymMap = rediSearch.dumpSynonyms();
        assertNotNull(synonymMap);
        assertEquals(gid, synonymMap.get("c").get(0).longValue());
    }

    @Test
    public void testSpellCheck() {
        RediSearch rediSearch = rediSearchClient.getRediSearch("index1");
        assertTrue(rediSearch.createIndex(new Schema().addField(new TextField("title"))));
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi");
        fields.put("tip", "ki");
        assertTrue(rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions()));
        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("title", "Yi");
        fields2.put("tip", "hll2");
        assertTrue(rediSearch.addDocument(new Document(String.format("doc3"), 0.9d, fields2), new DocumentOptions()));
        List<MisspelledTerm> misspelledTerms = rediSearch.spellCheck("i", new SpellCheckOptions().distance(2));
        assertEquals(1, misspelledTerms.size());
        assertEquals("i", misspelledTerms.get(0).getTerm());
        assertEquals(2, misspelledTerms.get(0).getMisspelledSuggestions().size());
        assertTrue(0.5d == misspelledTerms.get(0).getMisspelledSuggestions().get(0).getScore());
        misspelledTerms = rediSearch.spellCheck("i|k", new SpellCheckOptions().distance(2));
        assertEquals(2, misspelledTerms.size());
        misspelledTerms = rediSearch.spellCheck("*", new SpellCheckOptions().distance(2));
        assertEquals(0, misspelledTerms.size());
    }
}
