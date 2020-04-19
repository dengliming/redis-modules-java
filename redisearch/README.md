# Java Client for RediSearch 
See https://oss.redislabs.com/redisearch/ for more details.

## Redis commands mapping
Redis command|Sync / Async Api|
| --- | --- |
FT.CREATE | RediSearch.<br/>createIndex()<br/>createIndexAsync() |
FT.ADD | RediSearch.<br/>addDocument()<br/>addDocumentAsync() |
FT.ADDHASH | RediSearch.<br/>addHash()<br/>addHashAsync() |
FT.ALTER | RediSearch.<br/>alterIndex()<br/>alterIndexAsync() |
FT.ALIASADD | RediSearch.<br/>addAlias()<br/>addAliasAsync() |
FT.ALIASUPDATE | RediSearch.<br/>updateAlias()<br/>updateAliasAsync() |
FT.ALIASDEL | RediSearch.<br/>deleteAlias()<br/>deleteAliasAsync() |
FT.INFO | RediSearch.<br/>loadIndex()<br/>loadIndexAsync() |
FT.SEARCH | N/A |
FT.AGGREGATE | N/A |
FT.EXPLAIN | RediSearch.<br/>explain()<br/>explainAsync() |
FT.EXPLAINCLI | N/A |
FT.DEL | RediSearch.<br/>deleteDocument()<br/>deleteDocumentAsync() |
FT.GET | RediSearch.<br/>getDocument()<br/>getDocumentAsync() |
FT.MGET | RediSearch.<br/>getDocuments()<br/>getDocumentsAsync() |
FT.DROP | RediSearch.<br/>dropIndex()<br/>dropIndexAsync() |
FT.TAGVALS | RediSearch.<br/>getTagVals()<br/>getTagValsAsync() |
FT.SUGADD | RediSearch.<br/>addSuggestion()<br/>addSuggestionAsync() |
FT.SUGGET | RediSearch.<br/>getSuggestion()<br/>getSuggestionAsync() |
FT.SUGDEL | RediSearch.<br/>deleteSuggestion()<br/>deleteSuggestionAsync() |
FT.SUGLEN | RediSearch.<br/>getSuggestionLength()<br/>getSuggestionLengthAsync() |
FT.SYNADD | RediSearch.<br/>addSynonym()<br/>addSynonymAsync() |
FT.SYNUPDATE | RediSearch.<br/>updateSynonym()<br/>updateSynonymAsync() |
FT.SYNDUMP | RediSearch.<br/>dumpSynonyms()<br/>dumpSynonymsAsync() |
FT.SPELLCHECK | N/A |
FT.DICTADD | RediSearch.<br/>addDict()<br/>addDictAsync() |
FT.DICTDEL | RediSearch.<br/>deleteDict()<br/>deleteDictAsync() |
FT.DICTDUMP | RediSearch.<br/>dumpDict()<br/>dumpDictAsync() |
FT.CONFIG | RediSearch.<br/>setConfig()<br/>setConfigAsync()<br/>getConfig()<br/>getConfigAsync()<br/>getHelp()<br/>getHelpAsync() |