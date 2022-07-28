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
