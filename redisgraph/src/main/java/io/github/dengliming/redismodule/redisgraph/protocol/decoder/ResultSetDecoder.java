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

package io.github.dengliming.redismodule.redisgraph.protocol.decoder;

import io.github.dengliming.redismodule.redisgraph.enums.ColumnType;
import io.github.dengliming.redismodule.redisgraph.enums.ScalarType;
import io.github.dengliming.redismodule.redisgraph.model.Edge;
import io.github.dengliming.redismodule.redisgraph.model.GraphEntity;
import io.github.dengliming.redismodule.redisgraph.model.Header;
import io.github.dengliming.redismodule.redisgraph.model.Node;
import io.github.dengliming.redismodule.redisgraph.model.Path;
import io.github.dengliming.redismodule.redisgraph.model.Point;
import io.github.dengliming.redismodule.redisgraph.model.Record;
import io.github.dengliming.redismodule.redisgraph.model.ResultSet;
import io.github.dengliming.redismodule.redisgraph.model.Statistics;
import org.redisson.client.RedisException;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.dengliming.redismodule.redisgraph.enums.ColumnType.COLUMN_TYPES;
import static io.github.dengliming.redismodule.redisgraph.enums.ScalarType.getScalarType;

public class ResultSetDecoder implements MultiDecoder<ResultSet> {

    @Override
    public ResultSet decode(List<Object> parts, State state) {
        Statistics statistics;
        Header header = null;
        List<Record> records = null;
        if (parts.size() == 1) {
            statistics = parseStatistics(parts.get(0));
        } else if (parts.size() == 3) {
            header = parseHeader(parts.get(0));
            records = parseRecords(header, parts.get(1));
            statistics = parseStatistics(parts.get(2));
        } else {
            throw new RedisException("Unrecognized graph response format.");
        }

        return new ResultSet(header, records, statistics);
    }

    private Statistics parseStatistics(Object data) {
        Statistics statistics = new Statistics();
        ((List<String>) data).stream()
                .map(s -> s.split(": "))
                .forEach(kv -> {
                    switch (kv[0]) {
                        case "Nodes created":
                            statistics.setNodesCreated(getIntValue(kv[1]));
                            break;
                        case "Nodes deleted":
                            statistics.setNodesDeleted(getIntValue(kv[1]));
                            break;
                        case "Indices created":
                            statistics.setIndicesCreated(getIntValue(kv[1]));
                            break;
                        case "Indices deleted":
                            statistics.setIndicesDeleted(getIntValue(kv[1]));
                            break;
                        case "Labels added":
                            statistics.setLabelsAdded(getIntValue(kv[1]));
                            break;
                        case "Relationships deleted":
                            statistics.setRelationshipsDeleted(getIntValue(kv[1]));
                            break;
                        case "Relationships created":
                            statistics.setRelationshipsCreated(getIntValue(kv[1]));
                            break;
                        case "Properties set":
                            statistics.setPropertiesSet(getIntValue(kv[1]));
                            break;
                        case "Cached execution":
                            statistics.setCachedExecution("1".equals(kv[1]));
                            break;
                        case "Query internal execution time":
                            statistics.setQueryIntervalExecutionTime(kv[1]);
                            break;
                        default:
                            break;
                    }
                });
        return statistics;
    }

    private List<Record> parseRecords(Header header, Object data) {
        List<List<Object>> rawResultSet = (List<List<Object>>) data;
        List<Record> results = new ArrayList<>();
        if (rawResultSet == null || rawResultSet.isEmpty()) {
            return results;
        }

        // go over each raw result
        for (List<Object> row : rawResultSet) {

            List<Object> parsedRow = new ArrayList<>(row.size());
            // go over each object in the result
            for (int i = 0; i < row.size(); i++) {
                // get raw representation of the object
                List<Object> obj = (List<Object>) row.get(i);
                // get object type
                ColumnType objType = header.getSchemaTypes().get(i);
                // deserialize according to type and
                switch (objType) {
                    case NODE:
                        parsedRow.add(deserializeNode(obj));
                        break;
                    case RELATION:
                        parsedRow.add(deserializeEdge(obj));
                        break;
                    case SCALAR:
                        parsedRow.add(deserializeScalar(obj));
                        break;
                    default:
                        parsedRow.add(null);
                        break;
                }

            }
            // create new record from deserialized objects
            Record record = new Record(header.getSchemaNames(), parsedRow);
            results.add(record);
        }
        return results;
    }

    private Node deserializeNode(List<Object> rawNodeData) {
        Node node = new Node();
        node.setId((Long) rawNodeData.get(0));
        deserializeGraphEntityProperties(node, (List<List<Object>>) rawNodeData.get(2));
        return node;
    }

    private void deserializeGraphEntityProperties(GraphEntity entity, List<List<Object>> rawProperties) {
        for (List<Object> rawProperty : rawProperties) {
            // trimmed for getting to value using deserializeScalar
            List<Object> propertyScalar = rawProperty.subList(1, rawProperty.size());
            // TODO
            String name = "";
            entity.addProperty(((Long) rawProperty.get(0)).intValue(), name, deserializeScalar(propertyScalar));
        }
    }

    private Object deserializeScalar(List<Object> rawScalarData) {
        ScalarType type = getValueTypeFromObject(rawScalarData.get(0));

        Object obj = rawScalarData.get(1);
        switch (type) {
            case NULL:
                return null;
            case BOOLEAN:
                return Boolean.parseBoolean((String) obj);
            case ARRAY:
                return deserializeArray(obj);
            case NODE:
                return deserializeNode((List<Object>) obj);
            case EDGE:
                return deserializeEdge((List<Object>) obj);
            case PATH:
                return deserializePath(obj);
            case MAP:
                return deserializeMap(obj);
            case POINT:
                return deserializePoint(obj);
            case UNKNOWN:
            default:
                return obj;
        }
    }

    private Object deserializePoint(Object rawScalarData) {
        return new Point((List<Double>) rawScalarData);
    }

    private Edge deserializeEdge(List<Object> rawEdgeData) {
        Edge edge = new Edge();
        edge.setId((Long) rawEdgeData.get(0));
        edge.setRelationshipTypeIndex(((Long) rawEdgeData.get(1)).intValue());

        edge.setSource((long) rawEdgeData.get(2));
        edge.setDestination((long) rawEdgeData.get(3));
        deserializeGraphEntityProperties(edge, (List<List<Object>>) rawEdgeData.get(4));
        return edge;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deserializeMap(Object rawScalarData) {
        List<Object> keyTypeValueEntries = (List<Object>) rawScalarData;
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyTypeValueEntries.size(); i += 2) {
            String key = (String) keyTypeValueEntries.get(i);
            Object value = deserializeScalar((List<Object>) keyTypeValueEntries.get(i + 1));
            map.put(key, value);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Path deserializePath(Object rawScalarData) {
        List<List<Object>> array = (List<List<Object>>) rawScalarData;
        List<Node> nodes = (List<Node>) deserializeScalar(array.get(0));
        List<Edge> edges = (List<Edge>) deserializeScalar(array.get(1));
        return new Path(nodes, edges);
    }

    @SuppressWarnings("unchecked")
    private List<Object> deserializeArray(Object rawScalarData) {
        List<List<Object>> array = (List<List<Object>>) rawScalarData;
        List<Object> res = new ArrayList<>(array.size());
        for (List<Object> arrayValue : array) {
            res.add(deserializeScalar(arrayValue));
        }
        return res;
    }

    private ScalarType getValueTypeFromObject(Object rawScalarType) {
        return getScalarType(((Long) rawScalarType).intValue());
    }

    private Header parseHeader(Object data) {
        if (data == null) {
            return new Header();
        }

        List<List<Object>> list = (List<List<Object>>) data;
        List<ColumnType> types = new ArrayList<>(list.size());
        List<String> texts = new ArrayList<>(list.size());
        for (List<Object> tuple : list) {
            types.add(COLUMN_TYPES[((Long) tuple.get(0)).intValue()]);
            texts.add((String) tuple.get(1));
        }
        return new Header(types, texts);
    }

    private int getIntValue(String v) {
        return Optional.ofNullable(v).map(Integer::parseInt).orElse(0);
    }
}
