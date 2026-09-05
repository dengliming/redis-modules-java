# RedisAI Client

> **Deprecated.** RedisAI reached end of life upstream and is not part of Redis 8. This module is kept for
> existing users and will be removed in a future major release. The RedisAI 1.2 command set
> (`AI.MODELSTORE`, `AI.MODELEXECUTE`, `AI.SCRIPTEXECUTE`, `AI.DAGEXECUTE`) will not be added.

Project: https://github.com/RedisAI/RedisAI

Obtain `RedisAI` from `RedisAIClient.getRedisAI()` or from `RedisAIBatch` for pipelining. Every method has
an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| AI.TENSORSET | `RedisAI.setTensor()` | |
| AI.TENSORGET | `RedisAI.getTensor()` | |
| AI.MODELSET | `RedisAI.setModel()` | options via `SetModelArgs` |
| AI.MODELGET | `RedisAI.getModel()` | |
| AI.MODELDEL | `RedisAI.deleteModel()` | |
| AI.MODELRUN | — | not implemented |
| AI.MODELSTORE / AI.MODELEXECUTE | — | RedisAI 1.2, not planned |
| AI.SCRIPTSET | `RedisAI.setScript()` | |
| AI.SCRIPTSTORE | `RedisAI.storeScript()` | options via `StoreScriptArgs` |
| AI.SCRIPTGET | `RedisAI.getScript()` | |
| AI.SCRIPTDEL | `RedisAI.deleteScript()` | |
| AI.SCRIPTRUN | `RedisAI.runScript()` | |
| AI.SCRIPTEXECUTE | — | RedisAI 1.2, not planned |
| AI.DAGRUN / AI.DAGRUN_RO | — | not implemented |
| AI.DAGEXECUTE / AI.DAGEXECUTE_RO | — | RedisAI 1.2, not planned |
| AI.INFO | `RedisAI.getInfo()`, `resetStat()` | |
| AI.CONFIG | `RedisAI.loadBackend()`, `setBackendPath()` | |
