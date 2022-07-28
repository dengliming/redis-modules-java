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

package io.github.dengliming.redismodule.redisgraph.model;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphEntity {
    private long id;
    private final List<Property<?>> propertyList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addProperty(int index, String name, Object value) {
        propertyList.add(new Property(index, name, value));
    }

    public List<Property<?>> getPropertyList() {
        return propertyList;
    }
}
