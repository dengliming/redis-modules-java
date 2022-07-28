package io.github.dengliming.redismodule.redisgraph.model;

import java.util.List;

public class Record<T> {
    private final List<String> header;
    private final List<Object> values;

    public Record(List<String> header, List<Object> values) {
        this.header = header;
        this.values = values;
    }

    public <T> T getValue(int index) {
        return (T) this.values.get(index);
    }

    public <T> T getValue(String key) {
        return getValue(this.header.indexOf(key));
    }

    public String getString(int index) {
        return this.values.get(index).toString();
    }

    public String getString(String key) {
        return getString(this.header.indexOf(key));
    }

    public List<String> keys() {
        return header;
    }

    public List<Object> values() {
        return this.values;
    }

    public boolean containsKey(String key) {
        return this.header.contains(key);
    }

    public int size() {
        return this.header.size();
    }
}
