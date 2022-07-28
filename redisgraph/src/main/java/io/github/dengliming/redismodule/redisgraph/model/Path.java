package io.github.dengliming.redismodule.redisgraph.model;

import java.util.List;

public class Path {
    private final List<Node> nodes;
    private final List<Edge> edges;

    public Path(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
