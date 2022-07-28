package io.github.dengliming.redismodule.redisgraph.model;

public class Property<T> {

    private final int index;
    private final String name;
    private final T value;

    public Property(int index, String name, T value) {
        this.index = index;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }
}
