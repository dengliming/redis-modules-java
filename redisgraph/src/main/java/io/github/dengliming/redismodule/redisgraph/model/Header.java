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
