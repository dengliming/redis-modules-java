# RedisGraph Client

> **Deprecated.** RedisGraph reached end of life upstream and is not part of Redis 8. This module is kept
> for existing users of RedisGraph 2.x and will be removed in a future major release.

Project: https://github.com/RedisGraph/RedisGraph

Obtain `RedisGraph` from `RedisGraphClient.getRedisGraph()` or from `RedisGraphBatch` for pipelining.
Query results are returned in compact form; property names and relationship types are resolved through a
per-graph cache (not inside a batch). Every method has an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| GRAPH.QUERY | `RedisGraph.query()` | |
| GRAPH.RO_QUERY | `RedisGraph.readOnlyQuery()` | |
| GRAPH.EXPLAIN | `RedisGraph.explain()` | |
| GRAPH.PROFILE | `RedisGraph.profile()` | |
| GRAPH.DELETE | `RedisGraph.delete()` | |
| GRAPH.LIST | `RedisGraph.list()` | |
| GRAPH.SLOWLOG | `RedisGraph.slowLog()` | |
| GRAPH.CONFIG GET / SET | `RedisGraph.getConfig()`, `setConfig()` | |
| GRAPH.CONSTRAINT | — | not implemented |
