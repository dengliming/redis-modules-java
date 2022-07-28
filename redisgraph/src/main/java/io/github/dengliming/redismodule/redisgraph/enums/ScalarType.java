package io.github.dengliming.redismodule.redisgraph.enums;

import org.redisson.client.RedisException;

public enum ScalarType {
    UNKNOWN,
    NULL,
    STRING,
    INTEGER,
    BOOLEAN,
    DOUBLE,
    ARRAY,
    EDGE,
    NODE,
    PATH,
    MAP,
    POINT;

    public static final ScalarType[] SCALAR_TYPES = ScalarType.values();

    public static ScalarType getScalarType(int index) {
        try {
            return SCALAR_TYPES[index];
        } catch (IndexOutOfBoundsException e) {
            throw new RedisException("Unrecognized response type");
        }
    }
}
