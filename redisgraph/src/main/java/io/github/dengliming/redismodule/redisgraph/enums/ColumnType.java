package io.github.dengliming.redismodule.redisgraph.enums;

public enum ColumnType {

    UNKNOWN,
    SCALAR,
    NODE,
    RELATION;

    public static final ColumnType[] COLUMN_TYPES = ColumnType.values();
}
