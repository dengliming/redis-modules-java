# Java Client for RedisGraph 
See https://oss.redislabs.com/redisgraph/ for more details.

## Redis commands mapping
Redis command|Sync / Async Api|
| --- | --- |
GRAPH.CONFIG | RedisGraph.<br/>getConfig()<br/>getConfigAsync()<br/>setConfig()<br/>setConfigAsync() |
GRAPH.DELETE | RedisGraph.<br/>delete()<br/>deleteAsync()<br/> |
GRAPH.EXPLAIN | RedisGraph.<br/>explain()<br/>explainAsync()<br/> |
GRAPH.LIST | RedisGraph.<br/>list()<br/>listAsync()<br/> |
GRAPH.PROFILE | RedisGraph.<br/>profile()<br/>profileAsync()<br/> |
GRAPH.QUERY | RedisGraph.<br/>query()<br/>queryAsync()<br/> |
GRAPH.RO_QUERY | RedisGraph.<br/>readOnlyQuery()<br/>readOnlyQueryAsync()<br/> |
GRAPH.SLOWLOG | RedisGraph.<br/>slowLog()<br/>slowLogAsync()<br/> |


