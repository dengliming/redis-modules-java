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
import java.util.List;

/**
 * @author dengliming
 */
public class SortBy {

    private SortField[] sortFields;
    private int max;

    public SortBy(SortField... sortFields) {
        this.sortFields = sortFields;
    }

    public SortBy max(int max) {
        this.max = max;
        return this;
    }

    public SortField[] getSortFields() {
        return sortFields;
    }

    public int getMax() {
        return max;
    }

    public void build(List<Object> args) {
        args.add(Keywords.SORTBY);
        args.add(2 * sortFields.length);
        for (SortField sortField : sortFields) {
            args.add(sortField.getField());
            args.add(sortField.getOrder().name());
        }

        if (max > 0) {
            args.add(Keywords.MAX);
            args.add(max);
        }
    }
}
