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

package io.github.dengliming.redismodule.redisearch.index.schema;

/**
 * @author dengliming
 */
public class Field {
    private String name;
    private boolean sortable;
    private boolean noIndex;
    private FieldType fieldType;

    public Field(String name, FieldType fieldType) {
        this.name = name;
        this.fieldType = fieldType;
    }

    public Field sortable() {
        this.sortable = true;
        return this;
    }

    public Field noIndex() {
        this.noIndex = true;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isSortable() {
        return sortable;
    }

    public boolean isNoIndex() {
        return noIndex;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}
