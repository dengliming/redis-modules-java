# Java Client for RedisTimeSeries 
See https://oss.redislabs.com/redistimeseries/ for more details.

## Redis commands mapping
Redis command|Sync / Async Api|
| --- | --- |
TS.CREATE | RedisTimeSeries.<br/>create()<br/>createAsync() |
TS.ALTER | RedisTimeSeries.<br/>alter()<br/>alterAsync() |
TS.ADD | RedisTimeSeries.<br/>add()<br/>addAsync() |
TS.MADD | RedisTimeSeries.<br/>add()<br/>addAsync() |
TS.INCRBY | RedisTimeSeries.<br/>incrBy()<br/>incrByAsync() |
TS.DECRBY | RedisTimeSeries.<br/>decrBy()<br/>decrByAsync() |
TS.CREATERULE | RedisTimeSeries.<br/>createRule()<br/>createRuleAsync() |
TS.DELETERULE | RedisTimeSeries.<br/>deleteRule()<br/>deleteRuleAsync() |
TS.RANGE | RedisTimeSeries.<br/>range()<br/>rangeAsync() |
TS.REVRANGE | RedisTimeSeries.<br/>revRange()<br/>revRangeAsync() |
TS.MRANGE | RedisTimeSeries.<br/>mrange()<br/>mrangeAsync() |
TS.GET | RedisTimeSeries.<br/>get()<br/>getAsync() |
TS.MGET | RedisTimeSeries.<br/>mget()<br/>mgetAsync() |
TS.INFO | RedisTimeSeries.<br/>info()<br/>infoAsync() |
TS.QUERYINDEX | RedisTimeSeries.<br/>queryIndex()<br/>queryIndexAsync() |
