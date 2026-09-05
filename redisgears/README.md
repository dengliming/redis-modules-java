# RedisGears Client

> **Deprecated.** RedisGears (the Python runtime, v1) reached end of life upstream and is not part of
> Redis 8. This module is kept for existing users and will be removed in a future major release.

Project: https://github.com/RedisGears/RedisGears

Obtain `RedisGears` from `RedisGearsClient.getRedisGears()` or from `RedisGearsBatch` for pipelining.
Every method has an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| RG.PYEXECUTE | `RedisGears.pyExecute()` | |
| RG.PYSTATS | `RedisGears.pyStats()` | |
| RG.CONFIGGET | `RedisGears.getConfig()` | |
| RG.CONFIGSET | `RedisGears.setConfig()` | |
| RG.UNREGISTER | `RedisGears.unRegister()` | |
| RG.ABORTEXECUTION | `RedisGears.abortExecution()` | |
| RG.DROPEXECUTION | `RedisGears.dropExecution()` | |
| RG.INFOCLUSTER | `RedisGears.clusterInfo()` | |
| RG.REFRESHCLUSTER | `RedisGears.refreshCluster()` | |
| RG.DUMPEXECUTIONS | — | not implemented |
| RG.DUMPREGISTRATIONS | — | not implemented |
| RG.GETEXECUTION | — | not implemented |
| RG.GETRESULTS / RG.GETRESULTSBLOCKING | — | not implemented |
| RG.PYDUMPREQS | — | not implemented |
| RG.TRIGGER | — | not implemented |
