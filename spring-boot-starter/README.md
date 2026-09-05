# SpringBoot Starter


## Usage

Add dependency
```xml
<dependencies>
    <dependency>
        <groupId>io.github.dengliming.redismodule</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>2.0.4</version>
    </dependency>
</dependencies>
```

Every module gets its own `enabled` flag. Each module either has its own Redisson `config` (YAML or JSON), or shares
the one under `redis-module.config`. If the application already defines a `RedissonClient` bean (for example through
the Redisson starter), modules without their own config wrap that bean instead and leave its lifecycle alone.

Shared connection pool for several modules
```yaml
redis-module:
  enabled: true
  config: |
    singleServerConfig:
      address: "redis://127.0.0.1:6379"
  redisjson:
    enabled: true
  redisearch:
    enabled: true
  redisbloom:
    enabled: true
```

Dedicated Redisson instance for one module
```yaml
redis-module:
  enabled: true
  redisjson:
    enabled: true
    config: |
      singleServerConfig:
        idleConnectionTimeout: 10000
        connectTimeout: 10000
        timeout: 3000
        retryAttempts: 3
        retryInterval: 1500
        password: null
        subscriptionsPerConnection: 5
        clientName: null
        address: "redis://127.0.0.1:6379"
        subscriptionConnectionMinimumIdleSize: 1
        subscriptionConnectionPoolSize: 50
        connectionMinimumIdleSize: 24
        connectionPoolSize: 64
        database: 0
        dnsMonitoringInterval: 5000
      threads: 16
      nettyThreads: 32
      codec: !<org.redisson.codec.Kryo5Codec> {}
      transportMode: "NIO"
```

Custom JSON serialization: define a `JsonCodec` bean and the RedisJSON client uses it instead of the Gson default.
```java
@Bean
public JsonCodec jsonCodec(ObjectMapper mapper) {
    return new JsonCodec() {
        public String toJson(Object value) { return mapper.writeValueAsString(value); }
        public <T> T fromJson(String json, Class<T> type) { return mapper.readValue(json, type); }
    };
}
```

Use in Spring
```java
@Autowired(required = false)
private RedisJSONClient redisJSONClient;

public void test() {
    RedisJSON redisJSON = redisJSONClient.getRedisJSON();
    String key = "foo";
    redisJSON.set(key, SetArgs.Builder.create(".", "\"bar\""));
}
```
