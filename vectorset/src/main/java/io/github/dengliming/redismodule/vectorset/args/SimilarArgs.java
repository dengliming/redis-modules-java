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

package io.github.dengliming.redismodule.vectorset.args;

import io.github.dengliming.redismodule.vectorset.protocol.Keywords;

import java.util.List;

/**
 * Optional arguments of VSIM.
 */
public class SimilarArgs {
    private boolean withScores;
    private boolean withAttribs;
    private Integer count;
    private Double epsilon;
    private Integer ef;
    private String filter;
    private Integer filterEf;
    private boolean truth;
    private boolean noThread;

    public boolean isWithScores() {
        return withScores;
    }

    public boolean isWithAttribs() {
        return withAttribs;
    }

    /**
     * Return the similarity score (1 identical, 0 opposite) with each element.
     */
    public SimilarArgs withScores() {
        this.withScores = true;
        return this;
    }

    /**
     * Return the JSON attributes with each element.
     */
    public SimilarArgs withAttribs() {
        this.withAttribs = true;
        return this;
    }

    public SimilarArgs count(int count) {
        this.count = count;
        return this;
    }

    /**
     * Only return elements whose distance is at most {@code epsilon}, between 0 and 1.
     */
    public SimilarArgs epsilon(double epsilon) {
        this.epsilon = epsilon;
        return this;
    }

    /**
     * Search exploration factor; higher values improve recall at the cost of speed.
     */
    public SimilarArgs ef(int ef) {
        this.ef = ef;
        return this;
    }

    /**
     * Filter expression over element attributes, for example {@code .year > 2000}.
     */
    public SimilarArgs filter(String expression) {
        this.filter = expression;
        return this;
    }

    /**
     * Maximum filtering effort (FILTER-EF).
     */
    public SimilarArgs filterEf(int filterEf) {
        this.filterEf = filterEf;
        return this;
    }

    /**
     * Exact linear scan instead of the HNSW graph (TRUTH). Slow; for benchmarking and recall measurement.
     */
    public SimilarArgs truth() {
        this.truth = true;
        return this;
    }

    /**
     * Run in the main thread instead of a worker thread (NOTHREAD).
     */
    public SimilarArgs noThread() {
        this.noThread = true;
        return this;
    }

    public void build(List<Object> args) {
        if (withScores) {
            args.add(Keywords.WITHSCORES);
        }
        if (withAttribs) {
            args.add(Keywords.WITHATTRIBS);
        }
        if (count != null) {
            args.add(Keywords.COUNT);
            args.add(count);
        }
        if (epsilon != null) {
            args.add(Keywords.EPSILON);
            args.add(epsilon);
        }
        if (ef != null) {
            args.add(Keywords.EF);
            args.add(ef);
        }
        if (filter != null) {
            args.add(Keywords.FILTER);
            args.add(filter);
        }
        if (filterEf != null) {
            args.add(Keywords.FILTER_EF);
            args.add(filterEf);
        }
        if (truth) {
            args.add(Keywords.TRUTH);
        }
        if (noThread) {
            args.add(Keywords.NOTHREAD);
        }
    }
}
