# RedisBloom Client

Java client for the RedisBloom probabilistic data structures: Bloom filter, Cuckoo filter, Count-Min Sketch,
Top-K and t-digest. Built in to Redis 8; available as a module for Redis 7.
Official docs: https://redis.io/docs/latest/develop/data-types/probabilistic/

Obtain the data structures from `RedisBloomClient` (`getRBloomFilter`, `getCuckooFilter`, `getCountMinSketch`,
`getTopKFilter`, `getTDigest`) or from `RedisBloomBatch` for pipelining. Every method has an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| BF.RESERVE | `BloomFilter.create()` | `EXPANSION` / `NONSCALING` not exposed yet |
| BF.ADD | `BloomFilter.add()` | |
| BF.MADD | `BloomFilter.madd()` | |
| BF.INSERT | `BloomFilter.insert()` | options via `InsertArgs` |
| BF.EXISTS | `BloomFilter.exists()` | |
| BF.MEXISTS | `BloomFilter.existsMulti()` | |
| BF.SCANDUMP | `BloomFilter.scanDump()` | |
| BF.LOADCHUNK | `BloomFilter.loadChunk()` | |
| BF.INFO | `BloomFilter.getInfo()` | fields read by name, works across RedisBloom versions |
| BF.CARD | — | not implemented (RedisBloom 2.4.4) |
| CF.RESERVE | `CuckooFilter.reserve()` | |
| CF.ADD | `CuckooFilter.add()` | |
| CF.ADDNX | `CuckooFilter.addNx()` | |
| CF.INSERT | `CuckooFilter.insert()` | |
| CF.INSERTNX | `CuckooFilter.insertNx()` | |
| CF.EXISTS | `CuckooFilter.exists()` | |
| CF.MEXISTS | — | not implemented |
| CF.DEL | `CuckooFilter.delete()` | |
| CF.COUNT | `CuckooFilter.count()` | |
| CF.SCANDUMP | `CuckooFilter.scanDump()` | |
| CF.LOADCHUNK | `CuckooFilter.loadChunk()` | |
| CF.INFO | `CuckooFilter.getInfo()` | |
| CMS.INITBYDIM | `CountMinSketch.create(int width, int depth)` | |
| CMS.INITBYPROB | `CountMinSketch.create(double error, double probability)` | |
| CMS.INCRBY | `CountMinSketch.incrby()` | |
| CMS.QUERY | `CountMinSketch.query()` | |
| CMS.MERGE | `CountMinSketch.merge()` | |
| CMS.INFO | `CountMinSketch.getInfo()` | |
| TOPK.RESERVE | `TopKFilter.reserve()` | |
| TOPK.ADD | `TopKFilter.add()` | |
| TOPK.INCRBY | `TopKFilter.incrby()` | |
| TOPK.QUERY | `TopKFilter.query()` | |
| TOPK.COUNT | `TopKFilter.count()` | |
| TOPK.LIST | `TopKFilter.list()` | |
| TOPK.INFO | `TopKFilter.getInfo()` | |
| TDIGEST.CREATE | `TDigest.create()` | |
| TDIGEST.RESET | `TDigest.reset()` | |
| TDIGEST.ADD | `TDigest.add()` | |
| TDIGEST.MIN | `TDigest.getMin()` | |
| TDIGEST.MAX | `TDigest.getMax()` | |
| TDIGEST.QUANTILE | `TDigest.getQuantile()` | |
| TDIGEST.CDF | `TDigest.getCdf()` | |
| TDIGEST.MERGE | `TDigest.mergeTo()` | |
| TDIGEST.INFO | `TDigest.getInfo()` | |
| TDIGEST.RANK | `TDigest.rank()` | |
| TDIGEST.REVRANK | `TDigest.revRank()` | |
| TDIGEST.BYRANK | `TDigest.byRank()` | |
| TDIGEST.BYREVRANK | `TDigest.byRevRank()` | |
| TDIGEST.TRIMMED_MEAN | `TDigest.trimmedMean()` | |
