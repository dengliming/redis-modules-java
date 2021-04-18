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

package io.github.dengliming.redismodule.redisearch.aggregate;

import io.github.dengliming.redismodule.redisearch.protocol.Keywords;
import org.redisson.api.SortOrder;

import java.util.List;

/**
 * @author dengliming
 */
public final class Reducers {

    /**
     * Return the maximal value of a property, whether it is a string, number or NULL
     *
     * @param field
     * @return
     */
    public static Reducer max(String field) {
        return buildSingleFieldReducer(Keywords.MAX, field);
    }

    /**
     * Return the average value of a numeric property.
     *
     * @param field
     * @return
     */
    public static Reducer avg(String field) {
        return buildSingleFieldReducer(Keywords.AVG, field);
    }

    /**
     * Count the number of records in each group
     *
     * @return
     */
    public static Reducer count() {
        return new Reducer(null) {
            @Override
            public void build(List<Object> args) {
                args.add(Keywords.REDUCE);
                args.add(Keywords.COUNT);
                args.add(0);
            }
        };
    }

    /**
     * Count the number of distinct values for property
     *
     * @param field
     * @return
     */
    public static Reducer countDistinct(String field) {
        return buildSingleFieldReducer(Keywords.COUNT_DISTINCT, field);
    }

    /**
     * Same as COUNT_DISTINCT - but provide an approximation instead of an exact count
     *
     * @param field
     * @return
     */
    public static Reducer countDistinctish(String field) {
        return buildSingleFieldReducer(Keywords.COUNT_DISTINCTISH, field);
    }

    /**
     * Return the sum of all numeric values of a given property in a group.
     *
     * @param field
     * @return
     */
    public static Reducer sum(String field) {
        return buildSingleFieldReducer(Keywords.SUM, field);
    }

    /**
     * Return the minimal value of a property, whether it is a string, number or NULL.
     *
     * @param field
     * @return
     */
    public static Reducer min(String field) {
        return buildSingleFieldReducer(Keywords.MIN, field);
    }

    /**
     * Return the standard deviation of a numeric property in the group.
     *
     * @param field
     * @return
     */
    public static Reducer stdDev(String field) {
        return buildSingleFieldReducer(Keywords.STDDEV, field);
    }

    /**
     * Return the value of a numeric property at a given quantile of the results.
     *
     * @param field
     * @param quantile
     * @return
     */
    public static Reducer quantile(String field, double quantile) {
        return new Reducer(field) {
            @Override
            public void build(List<Object> args) {
                args.add(Keywords.REDUCE);
                args.add(Keywords.QUANTILE);
                args.add(2);
                args.add(field);
                args.add(quantile);
            }
        };
    }

    /**
     * Merge all distinct values of a given property into a single array.
     *
     * @param field
     * @return
     */
    public static Reducer toList(String field) {
        return buildSingleFieldReducer(Keywords.TOLIST, field);
    }

    /**
     * Return the first or top value of a given property in the group.
     *
     * @param field
     * @return
     */
    public static Reducer firstValue(String field) {
        return new FirstValue(field);
    }

    /**
     * @param field
     * @param by    If no BY is specified, we return the first value we encounter in the group
     * @return
     */
    public static Reducer firstValue(String field, String by) {
        return new FirstValue(field).by(by);
    }

    public static Reducer firstValue(String field, String by, SortOrder order) {
        return new FirstValue(field).by(by, order);
    }

    /**
     * Perform a reservoir sampling of the group elements with a given size, and return an array of the sampled items with an even distribution.
     *
     * @param field
     * @param sampleSize
     * @return
     */
    public static Reducer randomSample(String field, int sampleSize) {
        return new RandomSample(field, sampleSize);
    }

    private static Reducer buildSingleFieldReducer(Keywords name, String field) {
        return new Reducer(field) {
            @Override
            public void build(List<Object> args) {
                args.add(Keywords.REDUCE);
                args.add(name);
                args.add(1);
                args.add(field);
                if (getAlias() != null) {
                    args.add(Keywords.AS);
                    args.add(getAlias());
                }
            }
        };
    }

    private static class FirstValue extends Reducer {

        private int nargs = 1;
        private String by;
        private SortOrder order;

        FirstValue(String field) {
            super(field);
        }

        public FirstValue by(String by, SortOrder order) {
            this.by = by;
            this.order = order;
            this.nargs += 3;
            return this;
        }

        public FirstValue by(String by) {
            this.by = by;
            this.nargs += 2;
            return this;
        }

        @Override
        public void build(List<Object> args) {
            args.add(Keywords.REDUCE);
            args.add(Keywords.FIRST_VALUE);
            args.add(nargs);
            args.add(getField());
            if (by != null) {
                args.add(Keywords.BY);
                args.add(by);

                if (order != null) {
                    args.add(order.name());
                }
            }
        }
    }

    private static class RandomSample extends Reducer {

        private int sampleSize;

        RandomSample(String field, int sampleSize) {
            super(field);
            this.sampleSize = sampleSize;
        }

        @Override
        public void build(List<Object> args) {
            args.add(Keywords.REDUCE);
            args.add(Keywords.RANDOM_SAMPLE);
            args.add(2);
            args.add(getField());
            args.add(sampleSize);
        }
    }
}
