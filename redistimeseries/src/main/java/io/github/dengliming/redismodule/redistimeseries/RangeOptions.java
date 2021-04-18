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

package io.github.dengliming.redismodule.redistimeseries;

import io.github.dengliming.redismodule.redistimeseries.protocol.Keywords;

import java.util.List;

/**
 * @author dengliming
 */
public class RangeOptions {

    private int count;
    private Aggregation aggregationType;
    private long timeBucket;
    private boolean withLabels;

    public RangeOptions max(int count) {
        this.count = count;
        return this;
    }

    public RangeOptions aggregationType(Aggregation aggregationType, long timeBucket) {
        this.aggregationType = aggregationType;
        this.timeBucket = timeBucket;
        return this;
    }

    public RangeOptions withLabels() {
        this.withLabels = true;
        return this;
    }

    public void build(List<Object> args) {
        if (count > 0) {
            args.add(Keywords.COUNT);
            args.add(count);
        }
        if (aggregationType != null) {
            args.add(Keywords.AGGREGATION);
            args.add(aggregationType.getKey());
            args.add(timeBucket);
        }
        if (withLabels) {
            args.add(Keywords.WITHLABELS);
        }
    }
}
