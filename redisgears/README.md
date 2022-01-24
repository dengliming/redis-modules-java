# Java Client for RedisGears 
See https://oss.redislabs.com/redisgears/ for more details.

## Redis commands mapping
Redis command|Sync / Async Api|
| --- | --- |
RG.PYEXECUTE | RedisGears.<br/>pyExecute()<br/>pyExecuteAsync() |
RG.ABORTEXECUTION | RedisGears.<br/>abortExecution()<br/>abortExecutionAsync() |
RG.CONFIGGET | RedisGears.<br/>getConfig()<br/>getConfigAsync() |
RG.CONFIGSET | RedisGears.<br/>setConfig()<br/>setConfigAsync() |
RG.DROPEXECUTION | RedisGears.<br/>dropExecution()<br/>dropExecutionAsync() |
RG.DUMPEXECUTIONS | N/A |
RG.DUMPREGISTRATIONS | N/A |
RG.GETEXECUTION | N/A |
RG.GETRESULTS | N/A |
RG.GETRESULTSBLOCKING | N/A |
RG.INFOCLUSTER | RedisGears.<br/>clusterInfo()<br/>clusterInfoAsync() |
RG.PYSTATS | RedisGears.<br/>pyStats()<br/>pyStatsAsync() |
RG.PYDUMPREQS | N/A |
RG.REFRESHCLUSTER | RedisGears.<br/>refreshCluster()<br/>refreshClusterAsync() |
RG.TRIGGER | N/A |
RG.UNREGISTER | RedisGears.<br/>unRegister()<br/>unRegisterAsync() |
