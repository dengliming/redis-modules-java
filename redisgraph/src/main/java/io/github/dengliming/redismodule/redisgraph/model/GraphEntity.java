/*
 * Copyright 2021-2024 dengliming.
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
import java.util.function.IntFunction;

public abstract class GraphEntity {
    private long id;
    private final List<Property<?>> propertyList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Adds a property whose name is not yet known. The compact protocol only carries the property key index;
     * the name is resolved afterwards via {@link #resolvePropertyNames(IntFunction)}.
     */
    public void addProperty(int index, Object value) {
        addProperty(index, null, value);
    }

    public void addProperty(int index, String name, Object value) {
        propertyList.add(new Property(index, name, value));
    }

    /**
     * Replaces every property whose name is unknown with a copy carrying the name returned by the resolver.
     *
     * @param resolver maps a property key index to its name; may return null if the index is unknown
     */
    public void resolvePropertyNames(IntFunction<String> resolver) {
        for (int i = 0; i < propertyList.size(); i++) {
            Property<?> property = propertyList.get(i);
            if (property.getName() == null) {
                propertyList.set(i, new Property(property.getIndex(), resolver.apply(property.getIndex()), property.getValue()));
            }
        }
    }

    /**
     * Returns the value of the property with the given name, or null if the entity has no such property.
     */
    public Object getProperty(String name) {
        for (Property<?> property : propertyList) {
            if (name.equals(property.getName())) {
                return property.getValue();
            }
        }
        return null;
    }

    public List<Property<?>> getPropertyList() {
        return propertyList;
    }
}
