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

import io.github.dengliming.redismodule.redisgraph.enums.ColumnType;

import java.util.Collections;
import java.util.List;

public class Header {
    private final List<ColumnType> schemaTypes;
    private final List<String> schemaNames;

    public Header() {
        this.schemaTypes = Collections.emptyList();
        this.schemaNames = Collections.emptyList();
    }

    public Header(List<ColumnType> schemaTypes, List<String> schemaNames) {
        this.schemaTypes = schemaTypes;
        this.schemaNames = schemaNames;
    }

    public List<ColumnType> getSchemaTypes() {
        return schemaTypes;
    }

    public List<String> getSchemaNames() {
        return schemaNames;
    }
}
