# RedisBloom Client
See https://oss.redislabs.com/redisbloom/ for more details.

## Redis commands mapping
Redis command|Sync / Async Api|
| --- | --- |
BF.RESERVE | BloomFilter.<br/>create()<br/>createAsync() |
BF.ADD | BloomFilter.<br/>add()<br/>addAsync() |
BF.MADD | BloomFilter.<br/>madd()<br/>maddAsync() |
BF.INSERT | BloomFilter.<br/>insert()<br/>insertAsync() |
BF.EXISTS | BloomFilter.<br/>exists()<br/>existsAsync() |
BF.MEXISTS | BloomFilter.<br/>existsMulti()<br/>existsMultiAsync() |
BF.SCANDUMP | N/A |
BF.LOADCHUNK | N/A |
BF.INFO | BloomFilter.<br/>getInfo()<br/>getInfoAsync() |
CF.RESERVE | CuckooFilter.<br/>reserve()<br/>reserveAsync() |
CF.ADD | CuckooFilter.<br/>add()<br/>addAsync() |
CF.ADDNX | CuckooFilter.<br/>addNx()<br/>addNxAsync() |
CF.INSERT | CuckooFilter.<br/>insert()<br/>insertAsync() |
CF.INSERTNX | CuckooFilter.<br/>insertNx()<br/>insertNxAsync() |
CF.EXISTS | CuckooFilter.<br/>exists()<br/>existsAsync() |
CF.DEL | CuckooFilter.<br/>delete()<br/>deleteAsync() |
CF.COUNT | CuckooFilter.<br/>count()<br/>countAsync() |
CF.SCANDUMP | N/A |
CF.LOADDUMP | N/A |
CF.INFO | CuckooFilter.<br/>getInfo()<br/>getInfoAsync() |
CMS.INITBYDIM | CountMinSketch.<br/>create()<br/>createAsync() |
CMS.INITBYPROB | CountMinSketch.<br/>create()<br/>createAsync() |
CMS.INCRBY | CountMinSketch.<br/>incrby()<br/>incrbyAsync() |
CMS.QUERY | CountMinSketch.<br/>searchOptions()<br/>queryAsync() |
CMS.MERGE | CountMinSketch.<br/>merge()<br/>mergeAsync() |
CMS.INFO | CountMinSketch.<br/>getInfo()<br/>getInfoAsync() |
TOPK.RESERVE | TopKFilter.<br/>reserve()<br/>reserveAsync() |
TOPK.ADD | TopKFilter.<br/>add()<br/>addAsync() |
TOPK.INCRBY | TopKFilter.<br/>incrby()<br/>incrbyAsync() |
TOPK.QUERY | TopKFilter.<br/>searchOptions()<br/>queryAsync() |
TOPK.COUNT | TopKFilter.<br/>count()<br/>countAsync() |
TOPK.LIST | TopKFilter.<br/>list()<br/>listAsync() |
TOPK.INFO | TopKFilter.<br/>getInfo()<br/>getInfoAsync() |

