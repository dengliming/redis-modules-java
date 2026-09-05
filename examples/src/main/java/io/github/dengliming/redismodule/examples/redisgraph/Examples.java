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

package io.github.dengliming.redismodule.examples.redisgraph;

import io.github.dengliming.redismodule.redisgraph.RedisGraph;
import io.github.dengliming.redismodule.redisgraph.client.RedisGraphClient;
import io.github.dengliming.redismodule.redisgraph.model.Edge;
import io.github.dengliming.redismodule.redisgraph.model.Node;
import io.github.dengliming.redismodule.redisgraph.model.Record;
import io.github.dengliming.redismodule.redisgraph.model.ResultSet;
import org.redisson.config.Config;

public class Examples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedisGraphClient redisGraphClient = new RedisGraphClient(config);

        RedisGraph redisGraph = redisGraphClient.getRedisGraph();
        redisGraph.query("social", "CREATE (:person{name:'roi',age:32})", 0L);
        redisGraph.query("social", "CREATE (:person{name:'amit',age:30})", 0L);
        redisGraph.query("social", "MATCH (a:person), (b:person) WHERE (a.name = 'roi' AND b.name='amit') "
                + "CREATE (a)-[:knows{since:2000}]->(b)", 0L);

        ResultSet resultSet = redisGraph.query("social", "MATCH (a:person)-[r:knows]->(b:person) RETURN a, r, b.name", 0L);
        for (Record record : resultSet.getResults()) {
            Node a = (Node) record.getValue("a");
            Edge r = (Edge) record.getValue("r");
            // Property names and relationship types are resolved from the compact protocol indices
            System.out.println(a.getProperty("name") + " " + r.getRelationshipType() + " " + record.getString("b.name")
                    + " since " + r.getProperty("since"));
        }

        redisGraph.delete("social");
        redisGraphClient.shutdown();
    }
}
