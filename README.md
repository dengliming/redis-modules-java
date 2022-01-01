![build](https://github.com/dengliming/redis-modules-java/workflows/build/badge.svg) ![java-version](https://img.shields.io/badge/JDK-1.8+-brightgreen.svg) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.dengliming.redismodule/redis-modules-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.dengliming.redismodule/redis-modules-java) [![license](https://img.shields.io/github/license/dengliming/redis-modules-java)](/LICENSE) [![codecov](https://codecov.io/gh/dengliming/redis-modules-java/branch/master/graph/badge.svg?token=U8BA091JD5)](https://codecov.io/gh/dengliming/redis-modules-java)


Java Client libraries for [redis-modules](https://redis.io/modules), based on [Redisson](https://github.com/redisson/redisson).

## Support
* [RedisBloom](redisbloom) 
* [RediSearch](redisearch)
* [RedisTimeSeries](redistimeseries)
* [RedisAI](redisai)
* [RedisGears](redisgears)
* [RedisJSON](redisjson)

## TODO
* [RedisGraph](https://oss.redislabs.com/redisgraph/)
* [RediSQL](https://redisql.com/)
* [...](https://redis.io/modules)
 
## Installing

#### Build from source
Execute `./mvnw clean install -DskipTests=true -Dgpg.skip`. The build process requires `JDK8+`.

#### Maven repository
Include all
```xml
<!-- release -->
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>all</artifactId>
    <version>1.0.4</version>
</dependency>
```
Include single module like:
```xml
<!-- release -->
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>redistimeseries</artifactId>
    <version>1.0.4</version>
</dependency>
```

## Usage example
RedisBloom
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");
RedisBloomClient redisBloomClient = new RedisBloomClient(config);

BloomFilter bloomFilter = redisBloomClient.getRBloomFilter("bf");
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

RedisAI
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");
RedisAIClient redisAIClient = new RedisAIClient(config);

RedisAI redisAI = redisAIClient.getRedisAI();
redisAI.setTensor("tensor1", DataType.FLOAT, new int[]{2, 2}, null, new String[]{"1", "2", "3", "4"});
redisAIClient.shutdown();
```

RedisGears
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");
RedisGearsClient redisGearsClient = new RedisGearsClient(config);

RedisGears redisGears = redisGearsClient.getRedisGears();
redisGears.pyExecute("GB().run()", false);
redisGearsClient.shutdown();
```

RedisJSON
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");
RedisJSONClient redisJSONClient = new RedisJSONClient(config);

RedisJSON redisJSON = redisJSONClient.getRedisJSON();
String key = "foo";
Map<String, Object> m = new HashMap<>();
m.put("id", 1);
m.put("name", "lisi");
redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)));
Map<String, Object> actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
redisJSONClient.shutdown();
```
## License

[Apache License 2.0](/LICENSE)
