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

package io.github.dengliming.redismodule.redisearch;

import io.github.dengliming.redismodule.redisearch.index.ConfigOption;
import io.github.dengliming.redismodule.redisearch.index.Document;
import io.github.dengliming.redismodule.redisearch.index.DocumentOptions;
import io.github.dengliming.redismodule.redisearch.index.IndexDefinition;
import io.github.dengliming.redismodule.redisearch.index.IndexOptions;
import io.github.dengliming.redismodule.redisearch.index.RSLanguage;
import io.github.dengliming.redismodule.redisearch.index.Suggestion;
import io.github.dengliming.redismodule.redisearch.index.SuggestionOptions;
import io.github.dengliming.redismodule.redisearch.index.schema.DistanceMetric;
import io.github.dengliming.redismodule.redisearch.index.schema.Field;
import io.github.dengliming.redismodule.redisearch.index.schema.FieldType;
import io.github.dengliming.redismodule.redisearch.index.schema.Schema;
import io.github.dengliming.redismodule.redisearch.index.schema.TagField;
import io.github.dengliming.redismodule.redisearch.index.schema.TextField;
import io.github.dengliming.redismodule.redisearch.index.schema.VectorAlgorithm;
import io.github.dengliming.redismodule.redisearch.index.schema.VectorField;
import io.github.dengliming.redismodule.redisearch.index.schema.VectorType;
import io.github.dengliming.redismodule.redisearch.search.GeoFilter;
import io.github.dengliming.redismodule.redisearch.search.MisspelledTerm;
import io.github.dengliming.redismodule.redisearch.search.NumericFilter;
import io.github.dengliming.redismodule.redisearch.search.SearchOptions;
import io.github.dengliming.redismodule.redisearch.search.SearchResult;
import io.github.dengliming.redismodule.redisearch.search.SortBy;
import io.github.dengliming.redismodule.redisearch.search.SpellCheckOptions;
import io.github.dengliming.redismodule.redisearch.search.Vectors;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.SortOrder;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class RediSearchTest extends AbstractTest {

    @Test
    public void testIndex() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("index1");
        assertThat(rediSearch.createIndex(
                new Schema()
                        .addField(new TextField("title")),
                new IndexOptions()
                        .definition(new IndexDefinition().setPrefixes(Arrays.asList("doc:")))
                        .maxTextFields()
                        .prefixes(Arrays.asList("doc:"))
                        .stopwords(Arrays.asList("kk")))).isTrue();
        Map<String, Object> indexInfo = rediSearch.loadIndex();
        assertThat(indexInfo).isNotNull();
        assertThat(indexInfo.get("index_name")).isEqualTo("index1");
        assertThat(((List) (indexInfo.get("index_options"))).get(0)).isEqualTo("MAXTEXTFIELDS");
        assertThat(((List<List<Object>>) (indexInfo.get("attributes"))).get(0).get(1)).isEqualTo("title");

        List<String> indexes = rediSearch.listIndexes();
        assertThat(indexes).hasSize(1);
        assertThat(indexes).containsExactly("index1");

        rediSearch.dropIndex();
        indexes = rediSearch.listIndexes();
        assertThat(indexes).isEmpty();
    }

    @Test
    public void testIndexOnJSON() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("userIdx");
        assertThat(rediSearch.createIndex(
                new Schema()
                        .addField(new TextField("$.user.name").attribute("name"))
                        .addField(new TagField("$.user.tag").attribute("country")),
                new IndexOptions()
                        .definition(new IndexDefinition(IndexDefinition.DataType.JSON)))).isTrue();

        RedisJSON redisJSON = getRedisJSONClient().getRedisJSON();
        redisJSON.set("myDoc", SetArgs.Builder.create("$", "{\"user\":{\"name\":\"John Smith\","
                + "\"tag\":\"foo,bar\",\"hp\":1000, \"dmg\":150}}"));

        SearchResult result = rediSearch.search("@name:(John)", new SearchOptions());
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getDocuments().get(0).getId()).isEqualTo("myDoc");
    }

    @Test
    @Disabled("FT_ADDHASH No longer suppoted")
    public void testAddHash() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("index1");
        assertThat(rediSearch.createIndex(new Schema().addField(new TextField("title")))).isTrue();
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "hello world");
        RMap<String, Object> map = getRediSearchClient().getRedisson().getMap("foo");
        map.putAll(fields);
        assertThat(rediSearch.addHash("foo", 1, RSLanguage.ENGLISH)).isTrue();
    }

    @Test
    public void testDrop() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testDrop");
        assertThat(rediSearch.createIndex(new Schema().addField(new TextField("title")))).isTrue();
        assertThat(rediSearch.dropIndex()).isTrue();
    }

    @Test
    public void testDocument() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testDocument");
        assertThat(rediSearch.createIndex(new Schema().addField(new TextField("title")))).isTrue();
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi~");
        assertThat(rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions())).isTrue();

        Map<String, Object> fieldMap = rediSearch.getDocument("doc1");
        assertThat(fieldMap).isNotNull();
        assertThat(fieldMap.get("title")).isEqualTo("Hi~");

        List<Map<String, Object>> fieldMaps = rediSearch.getDocuments("doc1");
        assertThat(fieldMaps).isNotNull();
        assertThat(fieldMaps.get(0).get("title")).isEqualTo("Hi~");
        assertThat(rediSearch.deleteDocument("doc1")).isTrue();
    }

    @Test
    public void testConfig() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testConfig");
        assertThat(rediSearch.setConfig(ConfigOption.TIMEOUT, "1000")).isTrue();
        Map<String, String> configs = rediSearch.getConfig(ConfigOption.TIMEOUT);
        assertThat(configs).isNotNull();
        assertThat(configs.get(ConfigOption.TIMEOUT.name())).isEqualTo("1000");
    }

    @Test
    public void testSearch() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testSearch");
        assertThat(rediSearch.createIndex(new Schema()
                .addField(new TextField("title"))
                .addField(new TextField("content"))
                .addField(new Field("age", FieldType.NUMERIC))
                .addField(new Field("location", FieldType.GEO)))).isTrue();
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi");
        fields.put("content", "OOOO");
        assertThat(rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions())).isTrue();

        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("title", "Hi guy");
        fields2.put("content", "hello world");
        assertThat(rediSearch.addDocument(new Document(String.format("doc2"), 0.2d, fields2), new DocumentOptions())).isTrue();

        SearchResult searchResult = rediSearch.search("*", new SearchOptions().noContent().page(0, 1));
        assertThat(searchResult.getTotal()).isEqualTo(2);
        assertThat(searchResult.getDocuments().size()).isEqualTo(1);

        // WITHSCORES + NOCONTENT: reply is [total, id1, score1, id2, score2]
        searchResult = rediSearch.search("*", new SearchOptions().noContent().withScores());
        assertThat(searchResult.getTotal()).isEqualTo(2);
        assertThat(searchResult.getDocuments()).hasSize(2);
        assertThat(searchResult.getDocuments()).extracting(Document::getId).containsExactlyInAnyOrder("doc1", "doc2");
        assertThat(searchResult.getDocuments()).allSatisfy(doc -> {
            assertThat(doc.getFields()).isNull();
            assertThat(doc.getScore()).isGreaterThan(0);
        });

        searchResult = rediSearch.search("OOOO", new SearchOptions().noStopwords().language(RSLanguage.ENGLISH));
        assertThat(searchResult.getTotal()).isEqualTo(1);

        Map<String, Object> fields3 = new HashMap<>();
        fields3.put("title", "hello");
        fields3.put("content", "test number");
        fields3.put("age", 3);
        fields3.put("location", "13.361389,38.115556");
        assertThat(rediSearch.addDocument(new Document(String.format("doc3"), 0.3d, fields3), new DocumentOptions())).isTrue();

        // Search with NumericFilter
        searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .filter(new NumericFilter("age", 1, 4)));
        assertThat(searchResult.getTotal()).isEqualTo(1);

        // Search with GeoFilter
        searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .withScores()
                .filter(new GeoFilter("location", 15, 37, 200, GeoFilter.Unit.KILOMETERS)));
        assertThat(searchResult.getTotal()).isEqualTo(1);
        assertThat(searchResult.getDocuments()).hasSize(1);
        Document document = searchResult.getDocuments().get(0);
        assertThat(document.getFields()).containsEntry("title", "hello");
        assertThat(document.getScore()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testAlias() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testAlias");
        assertThat(rediSearch.createIndex(new Schema().addField(new TextField("title")))).isTrue();
        assertThat(rediSearch.addAlias("TEST")).isTrue();
        assertThat(rediSearch.updateAlias("HI")).isTrue();
        assertThat(rediSearch.deleteAlias("HI")).isTrue();
    }

    @Test
    public void testDict() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testDict");
        assertThat(rediSearch.createIndex(new Schema().addField(new TextField("title")))).isTrue();
        assertThat(rediSearch.addDict("TEST", "A", "B")).isEqualTo(2);
        assertThat(rediSearch.deleteDict("TEST", "A")).isEqualTo(1);
        List<String> dicts = rediSearch.dumpDict("TEST");
        assertThat(dicts).isNotNull();
        assertThat(dicts).hasSize(1);
        assertThat(dicts.get(0)).isEqualTo("B");
    }

    @Test
    @Disabled("FT_SYNADD No longer suppoted, use FT.SYNUPDATE.")
    public void testSynonym() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testSynonym");
        assertThat(rediSearch.createIndex(new Schema().addField(new TextField("title")))).isTrue();
        long gid = rediSearch.addSynonym("a", "b", "c");
        assertThat(gid).isGreaterThanOrEqualTo(0);

        assertThat(rediSearch.updateSynonym(gid, "c", "d")).isTrue();

        Map<String, List<Long>> synonymMap = rediSearch.dumpSynonyms();
        assertThat(synonymMap).isNotNull();
        assertThat(synonymMap.get("c").get(0).longValue()).isEqualTo(gid);
    }

    @Test
    public void testSpellCheck() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("index1");
        assertThat(rediSearch.createIndex(new Schema().addField(new TextField("title")))).isTrue();
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Hi");
        fields.put("tip", "ki");
        assertThat(rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions())).isTrue();
        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("title", "Yi");
        fields2.put("tip", "hll2");
        assertThat(rediSearch.addDocument(new Document(String.format("doc3"), 0.9d, fields2), new DocumentOptions())).isTrue();
        List<MisspelledTerm> misspelledTerms = rediSearch.spellCheck("i", new SpellCheckOptions().distance(2));
        assertThat(misspelledTerms).hasSize(1);
        assertThat(misspelledTerms.get(0).getTerm()).isEqualTo("i");
        assertThat(misspelledTerms.get(0).getMisspelledSuggestions()).hasSize(2);
        assertThat(misspelledTerms.get(0).getMisspelledSuggestions().get(0).getScore()).isEqualTo(0.5d);
        misspelledTerms = rediSearch.spellCheck("i|k", new SpellCheckOptions().distance(2));
        assertThat(misspelledTerms).hasSize(2);
        misspelledTerms = rediSearch.spellCheck("*", new SpellCheckOptions().distance(2));
        assertThat(misspelledTerms).isEmpty();
    }

    @Test
    public void testSuggestion() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testSuggestion");
        assertThat(rediSearch.addSuggestion(new Suggestion("hello world", 1.0d, "p1"))).isEqualTo(1);
        assertThat(rediSearch.addSuggestion(new Suggestion("hello there", 2.0d, null))).isEqualTo(2);
        assertThat(rediSearch.getSuggestionLength()).isEqualTo(2);

        List<Suggestion> suggestions = rediSearch.getSuggestion("hel", new SuggestionOptions());
        assertThat(suggestions).extracting(Suggestion::getTerm).containsExactlyInAnyOrder("hello world", "hello there");

        suggestions = rediSearch.getSuggestion("hel", new SuggestionOptions().withScores().withPayloads().maxNum(5));
        assertThat(suggestions).hasSize(2);
        Suggestion withPayload = suggestions.stream().filter(s -> "hello world".equals(s.getTerm())).findFirst().get();
        assertThat(withPayload.getPayload()).isEqualTo("p1");
        assertThat(withPayload.getScore()).isGreaterThan(0);

        assertThat(rediSearch.deleteSuggestion("hello world")).isTrue();
        assertThat(rediSearch.getSuggestionLength()).isEqualTo(1);
    }

    @Test
    public void testVectorSearch() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("idx:vec");
        assertThat(rediSearch.createIndex(new Schema()
                        .addField(new TextField("title").withSuffixTrie())
                        .addField(new VectorField("vec", VectorAlgorithm.HNSW, VectorType.FLOAT32, 2, DistanceMetric.L2)
                                .m(16).efConstruction(100)),
                new IndexOptions().definition(new IndexDefinition().setPrefixes(Arrays.asList("vec:"))))).isTrue();

        // documents are hashes with a binary FLOAT32 blob in the vector field
        storeVector("vec:1", "east", 1.0f, 0.0f);
        storeVector("vec:2", "north", 0.0f, 1.0f);
        storeVector("vec:3", "almost east", 0.9f, 0.1f);

        SearchResult result = rediSearch.search("*=>[KNN 2 @vec $BLOB AS score]", new SearchOptions()
                .param("BLOB", Vectors.toFloat32Bytes(1.0f, 0.0f))
                .dialect(2)
                .timeout(2000)
                .returnFields("title", "score")
                .sort(new SortBy("score", SortOrder.ASC)));
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getDocuments()).extracting(Document::getId).containsExactly("vec:1", "vec:3");
        assertThat(result.getDocuments().get(0).getFields()).containsEntry("title", "east").containsKey("score");

        // FLAT index and FT.INFO reporting the vector field
        RediSearch flat = getRediSearchClient().getRediSearch("idx:vec:flat");
        assertThat(flat.createIndex(new Schema()
                        .addField(new VectorField("vec", VectorAlgorithm.FLAT, VectorType.FLOAT32, 2, DistanceMetric.COSINE).blockSize(64)),
                new IndexOptions().definition(new IndexDefinition().setPrefixes(Arrays.asList("vec:"))))).isTrue();
        assertThat(flat.loadIndex()).containsEntry("index_name", "idx:vec:flat");

        AggregateResult aggregate = rediSearch.aggregate("*=>[KNN 3 @vec $BLOB AS score]", new AggregateOptions()
                .param("BLOB", Vectors.toFloat32Bytes(0.0f, 1.0f))
                .dialect(2)
                .loads("@title", "@score"));
        assertThat(aggregate.getRows()).hasSize(3);
    }

    private void storeVector(String key, String title, float... vector) {
        getRediSearchClient().getCommandExecutor().get(getRediSearchClient().getCommandExecutor().writeAsync(key,
                StringCodec.INSTANCE, RedisCommands.HSET, key, "title", title, "vec", Vectors.toFloat32Bytes(vector)));
    }
}
