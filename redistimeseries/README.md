# RedisTimeSeries Client

Java client for RedisTimeSeries. Built in to Redis 8; available as a module for Redis 7.
Official docs: https://redis.io/docs/latest/develop/data-types/timeseries/

Obtain `RedisTimeSeries` from `RedisTimeSeriesClient.getRedisTimeSeries()` or from `RedisTimeSeriesBatch`
for pipelining. Every method has an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| TS.CREATE | `RedisTimeSeries.create()` | options via `TimeSeriesOptions`; `ENCODING COMPRESSED` / `CHUNK_SIZE` not exposed yet |
| TS.ALTER | `RedisTimeSeries.alter()` | |
| TS.ADD | `RedisTimeSeries.add(Sample, TimeSeriesOptions)` | `ON_DUPLICATE` via `TimeSeriesOptions.duplicatePolicy()` |
| TS.MADD | `RedisTimeSeries.add(Sample...)` | |
| TS.INCRBY | `RedisTimeSeries.incrBy()` | |
| TS.DECRBY | `RedisTimeSeries.decrBy()` | |
| TS.DEL | — | not implemented (RedisTimeSeries 1.6) |
| TS.CREATERULE | `RedisTimeSeries.createRule()` | |
| TS.DELETERULE | `RedisTimeSeries.deleteRule()` | |
| TS.RANGE | `RedisTimeSeries.range()` | options via `RangeOptions`; `LATEST`, `FILTER_BY_*`, `BUCKETTIMESTAMP`, `EMPTY` not exposed yet |
| TS.REVRANGE | `RedisTimeSeries.revRange()` | |
| TS.MRANGE | `RedisTimeSeries.mrange()` | `GROUPBY ... REDUCE` via `GroupByOptions` |
| TS.MREVRANGE | — | not implemented |
| TS.NRANGE / TS.NREVRANGE | — | not implemented (Redis 8.10) |
| TS.GET | `RedisTimeSeries.get()` | |
| TS.MGET | `RedisTimeSeries.mget()` | |
| TS.READ | — | not implemented (Redis 8.10) |
| TS.INFO | `RedisTimeSeries.info()` | |
| TS.QUERYINDEX | `RedisTimeSeries.queryIndex()` | |
| TS.QUERYLABELS | — | not implemented (Redis 8.10) |
