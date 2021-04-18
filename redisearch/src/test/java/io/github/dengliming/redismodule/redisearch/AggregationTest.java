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

import io.github.dengliming.redismodule.redisearch.aggregate.AggregateOptions;
import io.github.dengliming.redismodule.redisearch.aggregate.AggregateResult;
import io.github.dengliming.redismodule.redisearch.aggregate.Filter;
import io.github.dengliming.redismodule.redisearch.aggregate.Group;
import io.github.dengliming.redismodule.redisearch.aggregate.Reducers;
import io.github.dengliming.redismodule.redisearch.aggregate.SortBy;
import io.github.dengliming.redismodule.redisearch.aggregate.SortField;
import io.github.dengliming.redismodule.redisearch.index.Document;
import io.github.dengliming.redismodule.redisearch.index.DocumentOptions;
import io.github.dengliming.redismodule.redisearch.index.schema.Field;
import io.github.dengliming.redismodule.redisearch.index.schema.FieldType;
import io.github.dengliming.redismodule.redisearch.index.schema.Schema;
import io.github.dengliming.redismodule.redisearch.index.schema.TextField;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class AggregationTest extends AbstractTest {

    @Test
    public void testAggregations() {
        RediSearch rediSearch = getRediSearchClient().getRediSearch("testAggregations");
        assertThat(rediSearch.createIndex(new Schema()
                .addField(new TextField("name").sortable())
                .addField(new Field("count", FieldType.NUMERIC)))).isTrue();
        Map<String, Object> fields1 = new HashMap<>();
        fields1.put("name", "a");
        fields1.put("count", 10);
        assertThat(rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields1), new DocumentOptions())).isTrue();

        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("name", "b");
        fields2.put("count", 5);
        assertThat(rediSearch.addDocument(new Document(String.format("doc2"), 1.0d, fields2), new DocumentOptions())).isTrue();

        Map<String, Object> fields3 = new HashMap<>();
        fields3.put("name", "c");
        fields3.put("count", 15);
        assertThat(rediSearch.addDocument(new Document(String.format("doc3"), 1.0d, fields3), new DocumentOptions())).isTrue();

        AggregateResult aggregateResult = rediSearch.aggregate("*", new AggregateOptions()
                .groups(new Group().fields("@name").reducers(Reducers.sum("@count").as("sum")))
                .sortBy(new SortBy(SortField.desc("@sum")).max(10)));
        assertThat(aggregateResult.getTotal()).isEqualTo(3);

        Map<String, Object> row = aggregateResult.getRows().get(0);
        assertThat(row.get("name")).isEqualTo("c");
        assertThat(row.get("sum")).isEqualTo("15");

        Map<String, Object> row1 = aggregateResult.getRows().get(1);
        assertThat(row1.get("name")).isEqualTo("a");
        assertThat(row1.get("sum")).isEqualTo("10");

        Map<String, Object> row2 = aggregateResult.getRows().get(2);
        assertThat(row2.get("name")).isEqualTo("b");
        assertThat(row2.get("sum")).isEqualTo("5");

        // With filter
        aggregateResult = rediSearch.aggregate("*", new AggregateOptions()
                .groups(new Group().fields("@name").reducers(Reducers.sum("@count").as("sum")))
                .filters(new Filter("@sum>=10"))
                .sortBy(new SortBy(SortField.desc("@sum")).max(10)));
        assertThat(aggregateResult.getRows()).hasSize(2);
        assertThat(aggregateResult.getRows().get(0).get("name")).isEqualTo("c");
        assertThat(aggregateResult.getRows().get(1).get("name")).isEqualTo("a");
    }
}
