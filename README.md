![build](https://github.com/dengliming/redis-modules-java/workflows/Java%20CI/badge.svg) [![license](https://img.shields.io/github/license/dengliming/redis-modules-java)](/LICENSE)

Java Client libraries for redis-modules, based on [Redisson](https://github.com/redisson/redisson).

## Support
* [RedisBloom](redisbloom/README.md) 
* [RediSearch](redisearch/README.md)

## TODO
* [RedisAI](https://oss.redislabs.com/redisai/)
* [RedisGears](https://oss.redislabs.com/redisgears/)
* [RedisTimeSeries](https://oss.redislabs.com/redistimeseries/)
* [...](https://redis.io/modules)
 
## Installing

#### Build form source
Execute ./mvnw clean install -DskipTests=true. The build process requires JDK8+.
#### Maven repository
TODO

## Usage example
RedisBloom
```java
RedisBloomClient redisBloomClient = new RedisBloomClient("redis://127.0.0.1:6379");

BloomFilter bloomFilter = redisBloomClient.getBloomFilter("bf");
bloomFilter.create(0.1d, 100);
bloomFilter.madd(new String[] {"a", "b", "c"});

TopKFilter topKFilter = redisBloomClient.getTopKFilter("topk_add");
topKFilter.reserve(1, 2000, 7, 0.925d);
topKFilter.add("test");
List<Boolean> itemExits = topKFilter.searchOptions("test");
Map<String, Integer> itemIncrement = new HashMap<>();
itemIncrement.put("test", 3);
topKFilter.incrby(itemIncrement);
List<String> allItems = topKFilter.list();

CountMinSketch countMinSketch = redisBloomClient.getCountMinSketch("cms_add");
countMinSketch.create(10, 10);
CountMinSketchInfo countMinSketchInfo = countMinSketch.getInfo();

CuckooFilter cuckooFilter = redisBloomClient.getCuckooFilter("cf_insert");
List<Boolean> result = cuckooFilter.insert(-1L, false, "a");

redisBloomClient.shutdown();
```
## License

[Apache License 2.0](/LICENSE)