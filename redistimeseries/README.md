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
TS.RANGE | N/A |
TS.MRANGE | N/A |
TS.GET | N/A |
TS.MGET | N/A |
TS.INFO | N/A |
TS.QUERYINDEX | N/A |