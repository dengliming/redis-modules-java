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
 * @author xdev.developer
 */
public class GroupByOptions {

    private String groupByLabel;
    private Reducer reducer;

    /**
     * Group by label using reducer aggregation
     * @param label grouping label
     * @param reducer reducer
     * @return RangeOptions
     */
    public GroupByOptions groupBy(String label, Reducer reducer) {
        this.groupByLabel = label;
        this.reducer = reducer;
        return this;
    }

    public void build(List<Object> args) {
        if (groupByLabel != null && reducer != null) {
            args.add(Keywords.GROUPBY);
            args.add(groupByLabel);
            args.add(Keywords.REDUCE);
            args.add(reducer.getKey());
        }
    }
}
