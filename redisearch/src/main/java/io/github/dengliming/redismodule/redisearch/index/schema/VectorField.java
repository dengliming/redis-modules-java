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

package io.github.dengliming.redismodule.redisearch.index.schema;

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisearch.protocol.Keywords;

import java.util.ArrayList;
import java.util.List;

/**
 * A VECTOR schema field. Vectors are stored as binary blobs (hash field) or number arrays (JSON) and queried
 * with a KNN clause, for example {@code *=>[KNN 10 @vec $BLOB AS score]} with a {@code BLOB} parameter.
 *
 * <pre>{@code
 * new VectorField("vec", VectorAlgorithm.HNSW, VectorType.FLOAT32, 768, DistanceMetric.COSINE)
 *         .m(16).efConstruction(200)
 * }</pre>
 */
public class VectorField extends Field {

    private final VectorAlgorithm algorithm;
    private final VectorType type;
    private final int dimension;
    private final DistanceMetric distanceMetric;
    private Integer initialCap;
    private Integer blockSize;
    private Integer m;
    private Integer efConstruction;
    private Integer efRuntime;
    private Double epsilon;

    public VectorField(String name, VectorAlgorithm algorithm, VectorType type, int dimension, DistanceMetric distanceMetric) {
        super(name, FieldType.VECTOR);
        RAssert.notNull(algorithm, "algorithm must not be null");
        RAssert.notNull(type, "type must not be null");
        RAssert.isTrue(dimension > 0, "dimension must be positive");
        RAssert.notNull(distanceMetric, "distanceMetric must not be null");
        this.algorithm = algorithm;
        this.type = type;
        this.dimension = dimension;
        this.distanceMetric = distanceMetric;
    }

    /**
     * Initial vector capacity, affects memory allocation (INITIAL_CAP).
     */
    public VectorField initialCap(int initialCap) {
        this.initialCap = initialCap;
        return this;
    }

    /**
     * FLAT only: block size to hold vectors (BLOCK_SIZE), default 1024.
     */
    public VectorField blockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * HNSW only: maximum outgoing edges per node (M), default 16.
     */
    public VectorField m(int m) {
        this.m = m;
        return this;
    }

    /**
     * HNSW only: candidates examined while building the graph (EF_CONSTRUCTION), default 200.
     */
    public VectorField efConstruction(int efConstruction) {
        this.efConstruction = efConstruction;
        return this;
    }

    /**
     * HNSW only: candidates examined at query time (EF_RUNTIME), default 10.
     */
    public VectorField efRuntime(int efRuntime) {
        this.efRuntime = efRuntime;
        return this;
    }

    /**
     * HNSW only: relative factor for range query accuracy (EPSILON), default 0.01.
     */
    public VectorField epsilon(double epsilon) {
        this.epsilon = epsilon;
        return this;
    }

    public VectorAlgorithm getAlgorithm() {
        return algorithm;
    }

    public VectorType getType() {
        return type;
    }

    public int getDimension() {
        return dimension;
    }

    public DistanceMetric getDistanceMetric() {
        return distanceMetric;
    }

    /**
     * Appends {@code <algorithm> <count> <attribute value>...} as FT.CREATE expects after the VECTOR keyword.
     */
    public void buildAttributes(List<Object> args) {
        List<Object> attributes = new ArrayList<>();
        attributes.add(Keywords.TYPE);
        attributes.add(type.name());
        attributes.add(Keywords.DIM);
        attributes.add(dimension);
        attributes.add(Keywords.DISTANCE_METRIC);
        attributes.add(distanceMetric.name());
        addIfSet(attributes, Keywords.INITIAL_CAP, initialCap);
        addIfSet(attributes, Keywords.BLOCK_SIZE, blockSize);
        addIfSet(attributes, Keywords.M, m);
        addIfSet(attributes, Keywords.EF_CONSTRUCTION, efConstruction);
        addIfSet(attributes, Keywords.EF_RUNTIME, efRuntime);
        addIfSet(attributes, Keywords.EPSILON, epsilon);

        args.add(algorithm.name());
        args.add(attributes.size());
        args.addAll(attributes);
    }

    private static void addIfSet(List<Object> attributes, Keywords keyword, Object value) {
        if (value != null) {
            attributes.add(keyword);
            attributes.add(value);
        }
    }
}
