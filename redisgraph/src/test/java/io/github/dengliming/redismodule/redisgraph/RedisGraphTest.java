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

package io.github.dengliming.redismodule.redisgraph;

import io.github.dengliming.redismodule.redisgraph.model.Header;
import io.github.dengliming.redismodule.redisgraph.model.Node;
import io.github.dengliming.redismodule.redisgraph.model.Record;
import io.github.dengliming.redismodule.redisgraph.model.ResultSet;
import io.github.dengliming.redismodule.redisgraph.model.SlowLogItem;
import io.github.dengliming.redismodule.redisgraph.model.Statistics;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class RedisGraphTest extends AbstractTest {

    @Test
    public void testConfig() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.setConfig("TIMEOUT", 10000)).isTrue();

        Map<String, Object> configMap = redisGraph.getConfig("TIMEOUT");
        assertThat(configMap).containsEntry("TIMEOUT", 10000L);
    }

    @Test
    public void testDelete() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.query("social",
                "CREATE (:person{name:'roi',age:32, doubleValue:3.14, boolValue:true})", -1)).isNotNull();
        assertThat(redisGraph.delete("social")).contains("OK");
    }

    @Test
    public void testList() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.profile("social", "CREATE (:person{name:'roi',age:32})", 0L)).isNotEmpty();
        List<String> graphList = redisGraph.list();
        assertThat(graphList).contains("social");
    }

    @Test
    public void testSlowLog() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.profile("social", "CREATE (:person{name:'roi',age:32})", 0L)).isNotEmpty();
        assertThat(redisGraph.profile("social", "CREATE (:person{name:'amit',age:30})", 0L)).isNotEmpty();

        List<SlowLogItem> slowLogItems = redisGraph.slowLog("social");
        assertThat(slowLogItems).isNotNull();
        assertThat(slowLogItems.size()).isEqualTo(2);
        assertThat(slowLogItems.get(0).getCommand()).isEqualToIgnoringCase("GRAPH.PROFILE");
    }

    @Test
    public void testQuery() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.query("social", "CREATE (:person{name:'filipe',age:30})", 0L)).isNotNull();
        assertThat(redisGraph.query("social", "MATCH (a:person) WHERE (a.name = 'filipe') RETURN a.age", 1000L)).isNotNull();
    }

    @Test
    public void testHeader() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.query("social", "CREATE (:person{name:'roi',age:32})", 0L)).isNotNull();
        assertThat(redisGraph.query("social", "CREATE (:person{name:'amit',age:30})", 0L)).isNotNull();
        assertThat(redisGraph.query("social",
                "MATCH (a:person), (b:person) WHERE (a.name = 'roi' AND b.name='amit')  CREATE (a)-[:knows]->(a)",
                0L)).isNotNull();

        ResultSet resultSet = redisGraph.query("social", "MATCH (a:person)-[r:knows]->(b:person) RETURN a,r, a.age", 0L);
        assertThat(resultSet).isNotNull();
        Header header = resultSet.getHeader();
        assertThat(header).isNotNull();
        List<String> schemaNames = header.getSchemaNames();

        assertThat(schemaNames).isNotNull();
        assertThat(schemaNames.size()).isEqualTo(3);
        assertThat(schemaNames.get(0)).isEqualTo("a");
        assertThat(schemaNames.get(1)).isEqualTo("r");
        assertThat(schemaNames.get(2)).isEqualTo("a.age");
    }

    @Test
    public void testRecord() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.query("social",
                "CREATE (:person{name:'roi',age:32, doubleValue:3.14, boolValue:true})", -1)).isNotNull();
        assertThat(redisGraph.query("social", "CREATE (:person{name:'amit',age:30})", -1)).isNotNull();
        assertThat(redisGraph.query("social", "MATCH (a:person), (b:person) WHERE (a.name = 'roi' AND b.name='amit')  "
                + "CREATE (a)-[:knows{place:'TLV', since:2000,doubleValue:3.14, boolValue:false}]->(b)", -1)).isNotNull();

        ResultSet resultSet = redisGraph.query("social", "MATCH (a:person)-[r:knows]->(b:person) RETURN a,r, "
                + "a.name, a.age, a.doubleValue, a.boolValue, " + "r.place, r.since, r.doubleValue, r.boolValue", -1);
        assertThat(resultSet).isNotNull();
        Statistics stats = resultSet.getStatistics();
        assertThat(stats.getNodesCreated()).isEqualTo(0);
        assertThat(stats.getNodesDeleted()).isEqualTo(0);
        assertThat(stats.getLabelsAdded()).isEqualTo(0);
        assertThat(stats.getPropertiesSet()).isEqualTo(0);
        assertThat(stats.getRelationshipsCreated()).isEqualTo(0);
        assertThat(stats.getRelationshipsDeleted()).isEqualTo(0);

        assertThat(resultSet.getResults().size()).isEqualTo(1);
        Record record = resultSet.getResults().get(0);
        Node node = (Node) record.getValue(0);
        assertThat(node).isNotNull();
        assertThat(node.getPropertyList().get(0).getValue()).isEqualTo("roi");

        assertThat(record.getString(2)).isEqualTo("roi");
        assertThat(record.getString(3)).isEqualTo("32");
        assertThat(((Long) record.getValue(3)).longValue()).isEqualTo(32L);
        assertThat(((Long) record.getValue("a.age")).longValue()).isEqualTo(32L);
        assertThat(record.getString("a.name")).isEqualTo("roi");
        assertThat(record.getString("a.age")).isEqualTo("32");

        resultSet = redisGraph.readOnlyQuery("social", "MATCH (a:person)-[r:knows]->(b:person) RETURN a,r, "
                + "a.name, a.age, a.doubleValue, a.boolValue, " + "r.place, r.since, r.doubleValue, r.boolValue", 500);
        assertThat(resultSet).isNotNull();
    }

    @Test
    public void testExplain() {
        RedisGraph redisGraph = getRedisGraph();
        assertThat(redisGraph.query("social", "CREATE (:person{name:'filipe',age:30})", 0L)).isNotNull();
        assertThat(redisGraph.explain("social", "MATCH (a:person) WHERE (a.name = 'filipe') RETURN a.age")).isNotEmpty();
    }

    @Test
    public void testPipelining() {
        RedisGraphBatch batch = getRedisGraphBatch();
        RedisGraph redisGraph = batch.getRedisGraph();
        redisGraph.profileAsync("social", "CREATE (:person{name:'roi',age:32})", 0L);
        redisGraph.listAsync();
        BatchResult<?> res = batch.execute();
        assertThat(res.getResponses().size()).isEqualTo(2);
        assertThat((List<String>) res.getResponses().get(0)).isNotEmpty();
        assertThat((List<String>) res.getResponses().get(1)).contains("social");
    }
}
