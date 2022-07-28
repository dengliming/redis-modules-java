package io.github.dengliming.redismodule.redisgraph.model;

import java.util.List;

public class ResultSet {

    private final Header header;
    private final List<Record> results;
    private final Statistics statistics;

    public ResultSet(Header header, List<Record> results, Statistics statistics) {
        this.header = header;
        this.results = results;
        this.statistics = statistics;
    }

    public Header getHeader() {
        return header;
    }

    public List<Record> getResults() {
        return results;
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
