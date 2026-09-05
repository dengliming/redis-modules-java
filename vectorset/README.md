# Vector Set Client

Java client for Redis vector sets, a native data type of Redis 8.0+ for storing vectors under named elements
and running approximate nearest-neighbour search over them (HNSW). Elements may carry JSON attributes that
searches can filter on.
Official docs: https://redis.io/docs/latest/develop/data-types/vector-sets/

Obtain a set with `VectorSetClient.getVectorSet(key)` or from `VectorSetBatch` for pipelining. Vectors are
passed as `double[]` (sent as `VALUES`) or as a little-endian FP32 `byte[]`. Every method has an `*Async` twin.

## Command mapping

| Redis command | Java API | Notes |
| --- | --- | --- |
| VADD | `VectorSet.add()` | `REDUCE`, `CAS`, quantization, `EF`, `SETATTR`, `M` via `AddArgs` |
| VSIM | `VectorSet.similar()`, `similarTo()` | options via `SimilarArgs`; result carries score and attributes when requested |
| VREM | `VectorSet.remove()` | |
| VCARD | `VectorSet.cardinality()` | |
| VDIM | `VectorSet.dimension()` | |
| VEMB | `VectorSet.getVector()`, `getRawVector()` | `RAW` returns quantization type, blob, norm and range |
| VGETATTR | `VectorSet.getAttributes()` | |
| VSETATTR | `VectorSet.setAttributes()` | |
| VINFO | `VectorSet.info()` | |
| VLINKS | `VectorSet.links()`, `linksWithScores()` | one list per HNSW layer |
| VISMEMBER | `VectorSet.contains()` | |
| VRANDMEMBER | `VectorSet.randomMember()`, `randomMembers()` | |
| VRANGE | `VectorSet.range()`, `members()` | Redis 8.4+; lexicographic paging with `[`, `(`, `-`, `+` bounds |
