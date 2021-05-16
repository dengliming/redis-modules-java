# Java Client for RedisJSON 
See https://oss.redislabs.com/redisjson/ for more details.

## Redis commands mapping
Redis command|Sync / Async Api|
| --- | --- |
JSON.DEL | RedisJSON.<br/>del()<br/>delAsync() |
JSON.GET | RedisJSON.<br/>get()<br/>getAsync() |
JSON.MGET | RedisJSON.<br/>mget()<br/>mgetAsync() |
JSON.SET | RedisJSON.<br/>set()<br/>setAsync() |
JSON.TYPE | RedisJSON.<br/>getType()<br/>getTypeAsync() |
JSON.NUMINCRBY | RedisJSON.<br/>incrBy()<br/>incrByAsync() |
JSON.NUMMULTBY | RedisJSON.<br/>multBy()<br/>multByAsync() |
JSON.STRAPPEND | RedisJSON.<br/>strAppend()<br/>strAppendAsync() |
JSON.STRLEN | RedisJSON.<br/>strLen()<br/>strLenAsync() |
JSON.ARRAPPEND | RedisJSON.<br/>arrAppend()<br/>arrAppendAsync() |
JSON.ARRINDEX | RedisJSON.<br/>arrIndex()<br/>arrIndexAsync() |
JSON.ARRINSERT | RedisJSON.<br/>arrInsert()<br/>arrInsertAsync() |
JSON.ARRLEN | RedisJSON.<br/>arrLen()<br/>arrLenAsync() |
JSON.ARRPOP | RedisJSON.<br/>arrPop()<br/>arrPopAsync() |
JSON.ARRTRIM | RedisJSON.<br/>arrTrim()<br/>arrTrimAsync() |
JSON.OBJKEYS | N/A |
JSON.OBJLEN | N/A |
JSON.DEBUG | N/A |
JSON.FORGET | N/A |
JSON.RESP | N/A |


