# Java Client for RedisAi 
See https://oss.redislabs.com/redisai/ for more details.

## Redis commands mapping
Redis command|Sync / Async Api|
| --- | --- |
AI.TENSORSET | RedisAI.<br/>setTensor()<br/>setTensorAsync() |
AI.TENSORGET | RedisAI.<br/>getTensor()<br/>getTensorAsync() |
AI.MODELSET | RedisAI.<br/>setModel()<br/>setModelAsync() |
AI.MODELGET | RedisAI.<br/>getModel()<br/>getModelAsync() |
AI.MODELDEL | RedisAI.<br/>deleteModel()<br/>deleteModelAsync() |
AI.MODELRUN | N/A |
AI._MODELSCAN | N/A |
AI.SCRIPTSET | RedisAI.<br/>setScript()<br/>setScriptAsync() |
AI.SCRIPTGET | N/A |
AI.SCRIPTDEL | RedisAI.<br/>deleteScript()<br/>deleteScriptAsync() |
AI.SCRIPTRUN | RedisAI.<br/>runScript()<br/>runScriptAsync() |
AI._SCRIPTSCAN | N/A |
AI.DAGRUN | N/A |
AI.DAGRUN_RO | N/A |
AI.INFO | RedisAI.<br/>getInfo()<br/>getInfoAsync()<br/>resetStat()<br/>resetStatAsync() |
AI.CONFIG | RedisAI.<br/>loadBackend()<br/>loadBackendAsync()<br/>setBackendPath()<br/>setBackendPathAsync() |
