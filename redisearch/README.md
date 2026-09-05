# RediSearch Client

Java client for RediSearch (the Redis Query Engine). Built in to Redis 8; available as a module for Redis 7.
Official docs: https://redis.io/docs/latest/develop/ai/search-and-query/

Obtain an index handle with `RediSearchClient.getRediSearch(indexName)` or from `RediSearchBatch` for
pipelining. Every method has an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| FT.CREATE | `RediSearch.createIndex()` | TEXT, TAG, NUMERIC, GEO and `VectorField` (FLAT / HNSW); `WITHSUFFIXTRIE`, `INDEXEMPTY`, `INDEXMISSING`, `SORTABLE UNF` |
| FT.ALTER | `RediSearch.alterIndex()` | |
| FT.DROPINDEX | — | not implemented; `dropIndex()` still sends the removed `FT.DROP` |
| FT.INFO | `RediSearch.loadIndex()` | handles the deeper Redis 8 reply |
| FT._LIST | `RediSearch.listIndexes()` | |
| FT.SEARCH | `RediSearch.search()` | options via `SearchOptions`, including `PARAMS`, `DIALECT`, `TIMEOUT` for KNN queries |
| FT.AGGREGATE | `RediSearch.aggregate()` | options via `AggregateOptions`, including `PARAMS`, `DIALECT`, `TIMEOUT`; `WITHCURSOR` not supported |
| FT.CURSOR READ / DEL | — | not implemented |
| FT.HYBRID | — | not implemented (Redis 8.4) |
| FT.PROFILE | — | not implemented |
| FT.EXPLAIN | `RediSearch.explain()` | |
| FT.EXPLAINCLI | — | not implemented |
| FT.ALIASADD | `RediSearch.addAlias()` | |
| FT.ALIASUPDATE | `RediSearch.updateAlias()` | |
| FT.ALIASDEL | `RediSearch.deleteAlias()` | |
| FT.ALIASLIST | — | not implemented (Redis 8.10) |
| FT.TAGVALS | `RediSearch.getTagVals()` | |
| FT.SUGADD | `RediSearch.addSuggestion()` | |
| FT.SUGGET | `RediSearch.getSuggestion()` | honours `WITHSCORES` / `WITHPAYLOADS` |
| FT.SUGDEL | `RediSearch.deleteSuggestion()` | |
| FT.SUGLEN | `RediSearch.getSuggestionLength()` | |
| FT.SYNUPDATE | `RediSearch.updateSynonym()` | |
| FT.SYNDUMP | `RediSearch.dumpSynonyms()` | |
| FT.SPELLCHECK | `RediSearch.spellCheck()` | |
| FT.DICTADD | `RediSearch.addDict()` | |
| FT.DICTDEL | `RediSearch.deleteDict()` | |
| FT.DICTDUMP | `RediSearch.dumpDict()` | |
| FT.CONFIG SET / GET | `RediSearch.setConfig()`, `getConfig()` | removed in Redis 8; falls back to `CONFIG SET/GET search-*` automatically |
| FT.CONFIG HELP | `RediSearch.getHelp()` | RediSearch 2.x only |

### Removed upstream (RediSearch 1.x only)

These commands were removed in RediSearch 2.0 and do not exist on Redis 8. They remain in the API for
users of RediSearch 1.x and will be dropped in a future major release.

| Redis command | Java API |
| --- | --- |
| FT.ADD | `RediSearch.addDocument()` |
| FT.ADDHASH | `RediSearch.addHash()` |
| FT.GET | `RediSearch.getDocument()` |
| FT.MGET | `RediSearch.getDocuments()` |
| FT.DEL | `RediSearch.deleteDocument()` |
| FT.DROP | `RediSearch.dropIndex()` |
| FT.SYNADD | `RediSearch.addSynonym()` |
