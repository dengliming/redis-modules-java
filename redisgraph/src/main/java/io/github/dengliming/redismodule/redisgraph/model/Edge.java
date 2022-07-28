package io.github.dengliming.redismodule.redisgraph.model;

public class Edge extends GraphEntity {
    private int relationshipTypeIndex;
    private long source;
    private long destination;

    public int getRelationshipTypeIndex() {
        return relationshipTypeIndex;
    }

    public void setRelationshipTypeIndex(int relationshipTypeIndex) {
        this.relationshipTypeIndex = relationshipTypeIndex;
    }

    public long getSource() {
        return source;
    }

    public void setSource(long source) {
        this.source = source;
    }

    public long getDestination() {
        return destination;
    }

    public void setDestination(long destination) {
        this.destination = destination;
    }
}
