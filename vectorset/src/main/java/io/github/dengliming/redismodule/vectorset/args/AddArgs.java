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

import io.github.dengliming.redismodule.vectorset.model.Quantization;
import io.github.dengliming.redismodule.vectorset.protocol.Keywords;

import java.util.List;

/**
 * Optional arguments of VADD.
 */
public class AddArgs {
    private Integer reduce;
    private boolean cas;
    private Quantization quantization;
    private Integer ef;
    private String attributes;
    private Integer m;

    /**
     * Random projection to {@code dimension} dimensions (REDUCE).
     */
    public AddArgs reduce(int dimension) {
        this.reduce = dimension;
        return this;
    }

    /**
     * Collect neighbour candidates in a background thread, check-and-set style (CAS).
     */
    public AddArgs cas() {
        this.cas = true;
        return this;
    }

    public AddArgs quantization(Quantization quantization) {
        this.quantization = quantization;
        return this;
    }

    /**
     * Build-time exploration factor (EF), default 200.
     */
    public AddArgs ef(int ef) {
        this.ef = ef;
        return this;
    }

    /**
     * JSON attributes to attach to the element (SETATTR).
     */
    public AddArgs attributes(String json) {
        this.attributes = json;
        return this;
    }

    /**
     * Maximum links per node in the HNSW graph (M), default 16.
     */
    public AddArgs m(int m) {
        this.m = m;
        return this;
    }

    /**
     * Arguments that precede the vector: REDUCE must come right before FP32 / VALUES.
     */
    public void buildBeforeVector(List<Object> args) {
        if (reduce != null) {
            args.add(Keywords.REDUCE);
            args.add(reduce);
        }
    }

    /**
     * Arguments that follow the element name.
     */
    public void buildAfterElement(List<Object> args) {
        if (cas) {
            args.add(Keywords.CAS);
        }
        if (quantization != null) {
            args.add(quantization.getKeyword());
        }
        if (ef != null) {
            args.add(Keywords.EF);
            args.add(ef);
        }
        if (attributes != null) {
            args.add(Keywords.SETATTR);
            args.add(attributes);
        }
        if (m != null) {
            args.add(Keywords.M);
            args.add(m);
        }
    }
}
