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

package io.github.dengliming.redismodule.redisearch.search;

import io.github.dengliming.redismodule.redisearch.protocol.Keywords;

import java.util.List;

/**
 * @author dengliming
 */
public class NumericFilter extends Filter {

    private final double min;
    private final boolean exclusiveMin;
    private final double max;
    private final boolean exclusiveMax;

    public NumericFilter(String field, double min, boolean exclusiveMin, double max) {
        this(field, min, exclusiveMin, max, false);
    }

    public NumericFilter(String field, double min, double max) {
        this(field, min, false, max, false);
    }

    public NumericFilter(String field, double min, double max, boolean exclusiveMax) {
        this(field, min, false, max, exclusiveMax);
    }

    public NumericFilter(String field, double min, boolean exclusiveMin, double max, boolean exclusiveMax) {
        super(field);
        this.min = min;
        this.exclusiveMin = exclusiveMin;
        this.max = max;
        this.exclusiveMax = exclusiveMax;
    }

    public double getMin() {
        return min;
    }

    public boolean isExclusiveMin() {
        return exclusiveMin;
    }

    public double getMax() {
        return max;
    }

    public boolean isExclusiveMax() {
        return exclusiveMax;
    }

    public void build(List<Object> args) {
        args.add(Keywords.FILTER);
        args.add(this.getField());
        args.add(formatNum(this.getMin(), this.exclusiveMin));
        args.add(formatNum(this.getMax(), this.exclusiveMax));
    }

    private String formatNum(double num, boolean exclude) {
        if (num == Double.POSITIVE_INFINITY) {
            return "+inf";
        }
        if (num == Double.NEGATIVE_INFINITY) {
            return "-inf";
        }

        return exclude ? "(" + num : String.valueOf(num);
    }
}
