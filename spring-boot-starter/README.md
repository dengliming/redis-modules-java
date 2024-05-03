# SpringBoot Starter


## Usage

Add dependency
```xml
<dependencies>
    <dependency>
        <groupId>io.github.dengliming.redismodule</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>2.0.4-SNAPSHOT</version>
    </dependency>
</dependencies>
```

RedisJSON
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