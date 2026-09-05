# RedisJSON Client

Java client for RedisJSON. Built in to Redis 8; available as a module for Redis 7.
Official docs: https://redis.io/docs/latest/develop/data-types/json/

Obtain `RedisJSON` from `RedisJSONClient.getRedisJSON()` or from `RedisJSONBatch` for pipelining. Values
are (de)serialized through a `JsonCodec`; Gson is the default and an optional dependency, see the
[main README](../README.md#redisjson). Every method has an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| JSON.SET | `RedisJSON.set()` | `NX` / `XX` via `SetArgs` |
| JSON.GET | `RedisJSON.get()` | formatting via `GetArgs`; expects a single value (legacy `.` paths) |
| JSON.MGET | `RedisJSON.mget()` | |
| JSON.MSET | — | not implemented (RedisJSON 2.6) |
| JSON.MERGE | — | not implemented (RedisJSON 2.6) |
| JSON.DEL | `RedisJSON.del()` | |
| JSON.FORGET | — | alias of JSON.DEL, use `del()` |
| JSON.CLEAR | — | not implemented |
| JSON.TOGGLE | — | not implemented |
| JSON.TYPE | `RedisJSON.getType()` | JSON `null` maps to `Void.class` |
| JSON.NUMINCRBY | `RedisJSON.incrBy()` | |
| JSON.NUMMULTBY | `RedisJSON.multBy()` | |
| JSON.STRAPPEND | `RedisJSON.strAppend()` | |
| JSON.STRLEN | `RedisJSON.strLen()` | |
| JSON.ARRAPPEND | `RedisJSON.arrAppend()` | |
| JSON.ARRINDEX | `RedisJSON.arrIndex()` | |
| JSON.ARRINSERT | `RedisJSON.arrInsert()` | |
| JSON.ARRLEN | `RedisJSON.arrLen()` | |
| JSON.ARRPOP | `RedisJSON.arrPop()` | |
| JSON.ARRTRIM | `RedisJSON.arrTrim()` | |
| JSON.OBJKEYS | `RedisJSON.objKeys()` | |
| JSON.OBJLEN | `RedisJSON.objLen()` | |
| JSON.DEBUG | — | not implemented |
| JSON.RESP | — | not implemented |
