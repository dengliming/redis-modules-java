![build](https://github.com/dengliming/redis-modules-java/workflows/Java%20CI/badge.svg) ![java-version](https://img.shields.io/badge/JDK-1.8+-brightgreen.svg) ![maven](https://img.shields.io/badge/maven-3.5+-brightgreen.svg) [![license](https://img.shields.io/github/license/dengliming/redis-modules-java)](/LICENSE)

Java Client libraries for [redis-modules](https://redis.io/modules), based on [Redisson](https://github.com/redisson/redisson).

## Support
* [RedisBloom](redisbloom) 
* [RediSearch](redisearch)
* [RedisTimeSeries](redistimeseries)

## TODO
* [RedisAI](https://oss.redislabs.com/redisai/)
* [RedisGears](https://oss.redislabs.com/redisgears/)
* [RedisJSON](https://oss.redislabs.com/rejson/)
* [...](https://redis.io/modules)
 
## Installing

#### Build from source
Execute ./mvnw clean install -DskipTests=true. The build process requires JDK8+.
#### Maven repository
repositories
```xml
<repositories>
    <repository>
        <id>snapshots-repo</id>
        <url>https://raw.githubusercontent.com/dengliming/mvn-repo/master/snapshots</url>
    </repository>
    <repository>
        <id>releases-repo</id>
        <url>https://raw.githubusercontent.com/dengliming/mvn-repo/master/releases</url>
    </repository>
</repositories>
```
Include all
```xml
<!-- snapshot -->
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>all</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```
or
```xml
<!-- release -->
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>all</artifactId>
    <version>1.0.0</version>
</dependency>
```
Include single module like:
```xml
<!-- snapshot -->
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>redistimeseries</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```
or
```xml
<!-- release -->
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>redistimeseries</artifactId>
    <version>1.0.0</version>
</dependency>
```
## Usage example
RedisBloom
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");
RedisBloomClient redisBloomClient = new RedisBloomClient(config);

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

RediSearch
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://" + DEFAULT_HOST + ":" + DEFAULT_PORT);
RediSearchClient rediSearchClient = new RediSearchClient(config);

RediSearch rediSearch = rediSearchClient.getRediSearch("testSearch");
rediSearch.createIndex(new Schema()
    .addField(new TextField("title"))
    .addField(new TextField("content"))
    .addField(new Field("age", FieldType.NUMERIC))
    .addField(new Field("location", FieldType.GEO)));

Map<String, Object> fields = new HashMap<>();
fields.put("title", "Hi");
fields.put("content", "OOOO");
rediSearch.addDocument(new Document(String.format("doc1"), 1.0d, fields), new DocumentOptions());

// Search with NumericFilter
SearchResult searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .filter(new NumericFilter("age", 1, 4)));

// Search with GeoFilter
searchResult = rediSearch.search("number", new SearchOptions()
                .noStopwords()
                .language(RSLanguage.ENGLISH)
                .filter(new GeoFilter("location", 15, 37, 200, GeoFilter.Unit.KILOMETERS)));
```

RedisTimeSeries
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://192.168.50.16:6383");
RedisTimeSeriesClient redisTimeSeriesClient = new RedisTimeSeriesClient(config);

RedisTimeSeries redisTimeSeries = redisTimeSeriesClient.getRedisTimeSeries();
long timestamp = System.currentTimeMillis();
redisTimeSeries.add(new Sample("temperature:2:32", Sample.Value.of(timestamp, 26)), new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32")));
redisTimeSeriesClient.shutdown();
```
## License

[Apache License 2.0](/LICENSE)
