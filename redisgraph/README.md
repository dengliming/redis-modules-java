# Graph Client (FalkorDB / RedisGraph)

Java client for the `GRAPH.*` command family. RedisGraph reached end of life upstream; its maintained
continuation is [FalkorDB](https://www.falkordb.com/), which keeps the same wire protocol and adds constraints,
graph copies and memory / runtime introspection. This module is tested against `falkordb/falkordb` in CI and
still works with RedisGraph 2.x for the commands both share.
Docs: https://docs.falkordb.com/commands/

Obtain `RedisGraph` from `RedisGraphClient.getRedisGraph()` or from `RedisGraphBatch` for pipelining. Query
results use the compact protocol; property names and relationship types are resolved through a per-graph cache
(not inside a batch). Every method has an `*Async` twin.

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
| GRAPH.CONSTRAINT CREATE | `RedisGraph.createConstraint()` | FalkorDB; enforced asynchronously, UNIQUE needs an index |
| GRAPH.CONSTRAINT DROP | `RedisGraph.dropConstraint()` | FalkorDB |
| GRAPH.COPY | `RedisGraph.copy()` | FalkorDB |
| GRAPH.MEMORY USAGE | `RedisGraph.memoryUsage()` | FalkorDB |
| GRAPH.INFO | `RedisGraph.info()` | FalkorDB; running / waiting queries, object pool |
