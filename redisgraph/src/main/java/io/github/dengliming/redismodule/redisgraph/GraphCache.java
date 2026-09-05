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

package io.github.dengliming.redismodule.redisgraph;

import io.github.dengliming.redismodule.redisgraph.model.Edge;
import io.github.dengliming.redismodule.redisgraph.model.GraphEntity;
import io.github.dengliming.redismodule.redisgraph.model.Path;
import io.github.dengliming.redismodule.redisgraph.model.Record;
import io.github.dengliming.redismodule.redisgraph.model.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Per-graph cache of the names that the compact protocol replaces with indices: property keys and
 * relationship types. The cache is refreshed lazily when a result references an index it does not know yet.
 */
final class GraphCache {

    static final String PROPERTY_KEYS_PROCEDURE = "CALL db.propertyKeys()";
    static final String RELATIONSHIP_TYPES_PROCEDURE = "CALL db.relationshipTypes()";

    private volatile List<String> propertyKeys = Collections.emptyList();
    private volatile List<String> relationshipTypes = Collections.emptyList();

    /**
     * Fills in property names and relationship type names for every entity in the result set,
     * refreshing the cache first if the result references indices beyond what is cached.
     *
     * @param resultSet the decoded result whose entities still carry unresolved indices
     * @param loader runs a procedure call against the graph and yields the returned names
     */
    CompletableFuture<ResultSet> resolve(ResultSet resultSet, Function<String, CompletableFuture<List<String>>> loader) {
        List<GraphEntity> entities = new ArrayList<>();
        if (resultSet.getResults() != null) {
            for (Record record : resultSet.getResults()) {
                collectEntities(record.values(), entities);
            }
        }
        if (entities.isEmpty()) {
            return CompletableFuture.completedFuture(resultSet);
        }

        int maxPropertyIndex = -1;
        int maxRelationshipIndex = -1;
        for (GraphEntity entity : entities) {
            for (int i = 0; i < entity.getPropertyList().size(); i++) {
                maxPropertyIndex = Math.max(maxPropertyIndex, entity.getPropertyList().get(i).getIndex());
            }
            if (entity instanceof Edge) {
                maxRelationshipIndex = Math.max(maxRelationshipIndex, ((Edge) entity).getRelationshipTypeIndex());
            }
        }

        CompletableFuture<Void> refresh = CompletableFuture.completedFuture(null);
        if (maxPropertyIndex >= propertyKeys.size()) {
            refresh = refresh.thenCompose(v -> loader.apply(PROPERTY_KEYS_PROCEDURE))
                    .thenAccept(names -> propertyKeys = names);
        }
        if (maxRelationshipIndex >= relationshipTypes.size()) {
            refresh = refresh.thenCompose(v -> loader.apply(RELATIONSHIP_TYPES_PROCEDURE))
                    .thenAccept(names -> relationshipTypes = names);
        }

        return refresh.thenApply(v -> {
            List<String> keys = propertyKeys;
            List<String> types = relationshipTypes;
            for (GraphEntity entity : entities) {
                entity.resolvePropertyNames(index -> index < keys.size() ? keys.get(index) : null);
                if (entity instanceof Edge) {
                    Edge edge = (Edge) entity;
                    int index = edge.getRelationshipTypeIndex();
                    edge.setRelationshipType(index < types.size() ? types.get(index) : null);
                }
            }
            return resultSet;
        });
    }

    @SuppressWarnings("unchecked")
    private static void collectEntities(Object value, List<GraphEntity> out) {
        if (value instanceof GraphEntity) {
            out.add((GraphEntity) value);
        } else if (value instanceof Path) {
            out.addAll(((Path) value).getNodes());
            out.addAll(((Path) value).getEdges());
        } else if (value instanceof List) {
            for (Object item : (List<Object>) value) {
                collectEntities(item, out);
            }
        } else if (value instanceof Map) {
            for (Object item : ((Map<Object, Object>) value).values()) {
                collectEntities(item, out);
            }
        }
    }
}
