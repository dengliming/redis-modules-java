<h1 align="center">redis-modules-java</h1>

<p align="center">
  Java client libraries for Redis modules, built on <a href="https://github.com/redisson/redisson">Redisson</a>.
</p>

<p align="center">
  <a href="https://github.com/dengliming/redis-modules-java/actions/workflows/build.yml"><img src="https://github.com/dengliming/redis-modules-java/workflows/build/badge.svg" alt="build"></a>
  <a href="https://central.sonatype.com/artifact/io.github.dengliming.redismodule/redis-modules-java"><img src="https://img.shields.io/maven-central/v/io.github.dengliming.redismodule/redis-modules-java.svg?label=maven%20central" alt="Maven Central"></a>
  <img src="https://img.shields.io/badge/JDK-8%2B-brightgreen.svg" alt="JDK 8+">
  <a href="https://codecov.io/gh/dengliming/redis-modules-java"><img src="https://codecov.io/gh/dengliming/redis-modules-java/branch/master/graph/badge.svg?token=U8BA091JD5" alt="codecov"></a>
  <a href="/LICENSE"><img src="https://img.shields.io/github/license/dengliming/redis-modules-java" alt="license"></a>
</p>

---

Every module ships a synchronous and an asynchronous (`RFuture`) API, works on a single server, sentinel or
cluster through Redisson's configuration, supports pipelining, and can share one connection pool with the
other modules or with an existing Redisson instance.

## Modules

| Module | Artifact | Redis 8 built-in | Status | Docs |
| --- | --- | :---: | --- | --- |
| RedisBloom (Bloom, Cuckoo, Count-Min Sketch, Top-K, t-digest) | `redisbloom` | ✅ | Active | [commands](redisbloom/README.md) |
| RediSearch | `redisearch` | ✅ | Active | [commands](redisearch/README.md) |
| RedisJSON | `redisjson` | ✅ | Active | [commands](redisjson/README.md) |
| RedisTimeSeries | `redistimeseries` | ✅ | Active | [commands](redistimeseries/README.md) |
| Vector sets (Redis 8 native type) | `vectorset` | ✅ | Active | [commands](vectorset/README.md) |
| RedisGraph | `redisgraph` | ❌ | Deprecated, upstream end of life | [commands](redisgraph/README.md) |
| RedisAI | `redisai` | ❌ | Deprecated, upstream end of life | [commands](redisai/README.md) |
| RedisGears | `redisgears` | ❌ | Deprecated, upstream end of life | [commands](redisgears/README.md) |
| Spring Boot starter | `spring-boot-starter` | | Active | [guide](spring-boot-starter/README.md) |
| Everything above | `all` | | | |

Deprecated modules still work against the last released versions of their module and are kept for existing
users; they will be removed in a future major release.

## Requirements

- Java 8 or later
- Redis 8.x (modules built in), or Redis 7.x with the corresponding module loaded
- Vector sets need Redis 8.0+ (`VRANGE` needs 8.4+)
- Redisson 4.7.x (pulled in transitively)

## Installation

All modules in one dependency:

```xml
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>all</artifactId>
    <version>2.0.4</version>
</dependency>
```

Or a single module, for example RedisTimeSeries:

```xml
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>redistimeseries</artifactId>
    <version>2.0.4</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.dengliming.redismodule:all:2.0.4'
```

<details>
<summary>Snapshots</summary>

Every push to `master` publishes `2.0.5-SNAPSHOT` to the Central snapshot repository:

```xml
<repositories>
    <repository>
        <id>central-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```
</details>

## Quick start

```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");

RedisJSONClient client = new RedisJSONClient(config);
RedisJSON json = client.getRedisJSON();

json.set("user:1", SetArgs.Builder.create(".", "{\"name\":\"lisi\",\"age\":30}"));
Map<String, Object> user = json.get("user:1", Map.class, new GetArgs().path("."));
long age = json.incrBy("user:1", ".age", 1);

client.shutdown();
```

Every synchronous method has an `*Async` twin returning an `RFuture`:

```java
RFuture<Long> future = json.arrAppendAsync("user:1", ".tags", "vip");
future.thenAccept(size -> log.info("tags: {}", size));
```

## Usage

### RedisBloom

```java
RedisBloomClient client = new RedisBloomClient(config);

BloomFilter bloomFilter = client.getRBloomFilter("bf");
bloomFilter.create(0.01d, 1000);
bloomFilter.madd("a", "b", "c");
List<Boolean> exists = bloomFilter.existsMulti("a", "z");   // [true, false]

CuckooFilter cuckooFilter = client.getCuckooFilter("cf");
cuckooFilter.reserve(1000);
cuckooFilter.add("a");

CountMinSketch sketch = client.getCountMinSketch("cms");
sketch.create(2000, 5);
Map<String, Integer> increments = new HashMap<>();
increments.put("a", 3);
sketch.incrby(increments);

TopKFilter topK = client.getTopKFilter("topk");
topK.reserve(3, 2000, 7, 0.925d);
topK.add("a", "b", "a");
List<String> top = topK.list();

TDigest tDigest = client.getTDigest("td");
tDigest.create(100);
tDigest.add(Arrays.asList(new AbstractMap.SimpleEntry<>(1.0, 1.0), new AbstractMap.SimpleEntry<>(2.0, 1.0)));  // (value, weight)
List<Double> quantiles = tDigest.getQuantile(0.5);
```

### RediSearch

```java
RediSearchClient client = new RediSearchClient(config);
RediSearch rediSearch = client.getRediSearch("idx:products");

rediSearch.createIndex(new Schema()
        .addField(new TextField("title"))
        .addField(new Field("price", FieldType.NUMERIC))
        .addField(new Field("location", FieldType.GEO)),
    new IndexOptions().definition(new IndexDefinition().setPrefixes(Arrays.asList("product:"))));

// documents are plain hashes under the configured prefix
SearchResult result = rediSearch.search("phone", new SearchOptions()
        .withScores()
        .filter(new NumericFilter("price", 100, 500))
        .filter(new GeoFilter("location", 15, 37, 200, GeoFilter.Unit.KILOMETERS))
        .page(0, 10));

AggregateResult aggregate = rediSearch.aggregate("*", new AggregateOptions()
        .groups(new Group().fields("@brand").reducers(Reducers.count().as("count"))));

List<Suggestion> suggestions = rediSearch.getSuggestion("pho", new SuggestionOptions().withScores());
```

### RedisJSON

```java
RedisJSONClient client = new RedisJSONClient(config);
RedisJSON json = client.getRedisJSON();

json.set("user:1", SetArgs.Builder.create(".", "{\"name\":\"lisi\",\"tags\":[]}"));
json.arrAppend("user:1", ".tags", "vip", "beta");
long tags = json.arrLen("user:1", ".tags");                     // 2
Class type = json.getType("user:1", ".name");                   // String.class
List<Map> users = json.mget(".", Map.class, "user:1", "user:2"); // missing keys yield null
```

RedisJSON serializes with Gson by default. Gson is an **optional** dependency of the `redisjson` artifact (the
`all` artifact includes it): either add `com.google.code.gson:gson` yourself, or plug in your own `JsonCodec`:

```java
JsonCodec jackson = new JsonCodec() {
    private final ObjectMapper mapper = new ObjectMapper();
    public String toJson(Object value) { return mapper.writeValueAsString(value); }
    public <T> T fromJson(String json, Class<T> type) { return mapper.readValue(json, type); }
};
RedisJSONClient client = new RedisJSONClient(config, jackson);
```

### RedisTimeSeries

```java
RedisTimeSeriesClient client = new RedisTimeSeriesClient(config);
RedisTimeSeries ts = client.getRedisTimeSeries();

ts.create("temperature:2:32", new TimeSeriesOptions()
        .retentionTime(60_000L)
        .labels(new Label("sensor_id", "2"), new Label("area_id", "32")));

ts.add(new Sample("temperature:2:32", Sample.Value.of(System.currentTimeMillis(), 26.5)), null);
ts.incrBy("requests:total", 1);

List<Sample.Value> values = ts.range("temperature:2:32", 0, Long.MAX_VALUE,
        new RangeOptions().aggregationType(Aggregation.AVG, 60_000));
List<TimeSeries> byArea = ts.mrange(0, Long.MAX_VALUE, new RangeOptions().withLabels(), "area_id=32");
```

### Vector sets

```java
VectorSetClient client = new VectorSetClient(config);
VectorSet movies = client.getVectorSet("movies");

movies.add("matrix", new double[]{0.9, 0.1, 0.0}, new AddArgs().attributes("{\"year\":1999}"));
movies.add("amelie", new double[]{0.0, 0.2, 0.9}, new AddArgs().attributes("{\"year\":2001}"));

List<Similarity> hits = movies.similar(new double[]{1.0, 0.0, 0.0}, new SimilarArgs().withScores().withAttribs().count(5));
List<Similarity> recent = movies.similarTo("matrix", new SimilarArgs().filter(".year > 2000"));
List<Double> vector = movies.getVector("matrix");
```

### RedisGraph (deprecated)

```java
RedisGraphClient client = new RedisGraphClient(config);
RedisGraph graph = client.getRedisGraph();

graph.query("social", "CREATE (:person{name:'roi',age:32})-[:knows{since:2000}]->(:person{name:'amit',age:30})", 0L);
ResultSet resultSet = graph.query("social", "MATCH (a:person)-[r:knows]->(b:person) RETURN a, r, b.name", 0L);
for (Record record : resultSet.getResults()) {
    Node a = (Node) record.getValue("a");
    Edge r = (Edge) record.getValue("r");
    // property names and relationship types are resolved, not just their indices
    System.out.println(a.getProperty("name") + " " + r.getRelationshipType() + " " + record.getString("b.name"));
}
```

### RedisAI and RedisGears (deprecated)

```java
RedisAI redisAI = new RedisAIClient(config).getRedisAI();
redisAI.setTensor("tensor1", DataType.FLOAT, new int[]{2, 2}, null, new String[]{"1", "2", "3", "4"});

RedisGears redisGears = new RedisGearsClient(config).getRedisGears();
redisGears.pyExecute("GB().run()", false);
```

## Pipelining

Every client has `createXxxBatch()`. Objects obtained from the batch queue their `*Async` calls and
`execute()` sends them in a single round trip, returning the responses in call order:

```java
RedisBloomBatch batch = redisBloomClient.createRedisBloomBatch();
BloomFilter bloomFilter = batch.getRBloomFilter("bf");
bloomFilter.createAsync(0.01d, 1000);
bloomFilter.maddAsync("a", "b", "c");
bloomFilter.existsAsync("a");

BatchResult<?> result = batch.execute();
result.getResponses();   // [true, [true, true, true], true]
```

## Sharing one Redisson instance

Each client can wrap an existing `RedissonClient`, so several modules (or your own Redisson code) share a
single connection pool. A wrapping client never shuts the shared instance down:

```java
RedissonClient redisson = Redisson.create(config);

RedisJSONClient jsonClient = new RedisJSONClient(redisson);
RediSearchClient searchClient = new RediSearchClient(redisson);
RedisBloomClient bloomClient = new RedisBloomClient(redisson);

// ...
redisson.shutdown();
```

## Spring Boot

Add the starter and enable the modules you need; they share one connection pool or use dedicated ones,
and pick up your own `RedissonClient` and `JsonCodec` beans when present.

```yaml
redis-module:
  config: |
    singleServerConfig:
      address: "redis://127.0.0.1:6379"
  redisjson:
    enabled: true
  redisearch:
    enabled: true
```

```java
@Autowired
private RedisJSONClient redisJSONClient;
```

See the [starter guide](spring-boot-starter/README.md) for every option.

## Compatibility notes

- CI runs the test suite against the official `redis:8.8` image, where Search, JSON, TimeSeries and Bloom are
  built in. `FT.CONFIG` was removed in Redis 8; `RediSearch.setConfig()` / `getConfig()` fall back to
  `CONFIG SET/GET search-*` automatically.
- RediSearch 1.x document commands (`FT.ADD`, `FT.GET`, `FT.DEL`, `FT.DROP`, ...) are still exposed but no longer
  exist on RediSearch 2.x / Redis 8. Index hashes or JSON documents under a prefix instead.
- Commands are routed by their key, so cluster deployments work with Redisson's `useClusterServers()`.

## Building from source

```bash
./mvnw clean install -DskipTests -Dgpg.skip
```

Integration tests need a running Redis with the modules loaded; pass its location with `-DREDIS_HOST` and
`-DREDIS_PORT` (see [`build.yml`](.github/workflows/build.yml) for the full matrix). More runnable snippets
live in [`examples`](examples/src/main/java/io/github/dengliming/redismodule/examples).

## Contributing

Issues and pull requests are welcome. Please run `./mvnw verify -DskipTests` before opening a PR so that
Checkstyle passes.

## License

[Apache License 2.0](/LICENSE)
