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

import org.redisson.api.SortOrder;

/**
 * @author dengliming
 */
public class SortField {

    private String field;
    private SortOrder order;

    public SortField(String field, SortOrder order) {
        this.field = field;
        this.order = order;
    }

    public static SortField desc(String field) {
        return new SortField(field, SortOrder.DESC);
    }

    public static SortField asc(String field) {
        return new SortField(field, SortOrder.ASC);
    }

    public String getField() {
        return field;
    }

    public SortOrder getOrder() {
        return order;
    }
}
