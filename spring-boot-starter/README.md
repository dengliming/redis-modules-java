# Spring Boot Starter

Auto-configures one client bean per enabled module: `RedisJSONClient`, `RediSearchClient`,
`RedisBloomClient`, `RedisTimeSeriesClient` (and the deprecated `RedisGraphClient`, `RedisAIClient`,
`RedisGearsClient`). Works with Spring Boot 2.7 and 3.x.

## Dependency

```xml
<dependency>
    <groupId>io.github.dengliming.redismodule</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.0.4</version>
</dependency>
```

## Configuration

| Property | Description |
| --- | --- |
| `redis-module.enabled` | Master switch, defaults to `true`. |
| `redis-module.config` | Redisson configuration (YAML or JSON) shared by every module without its own `config`. Creates a `RedissonClient` bean unless the application already defines one. |
| `redis-module.<module>.enabled` | Registers the client bean of that module. `<module>` is one of `redisjson`, `redisearch`, `redisbloom`, `redistimeseries`, `redisgraph`, `redisai`, `redisgears`. |
| `redis-module.<module>.config` | Dedicated Redisson configuration for that module; the client owns and shuts down this instance. |

Resolution order for a module: its own `config` → the `RedissonClient` bean of the context (yours, or the
one built from `redis-module.config`). A module with neither fails fast at startup with a message naming
the missing property.

### Shared connection pool

```yaml
redis-module:
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

### Dedicated instance for one module

```yaml
redis-module:
  redisjson:
    enabled: true
    config: |
      singleServerConfig:
        address: "redis://127.0.0.1:6379"
        connectionPoolSize: 64
        connectionMinimumIdleSize: 24
        timeout: 3000
      threads: 16
      nettyThreads: 32
      codec: !<org.redisson.codec.Kryo5Codec> {}
```

Any [Redisson configuration](https://github.com/redisson/redisson/wiki/2.-Configuration) is accepted,
including sentinel and cluster setups.

### Reusing an existing RedissonClient

If a `RedissonClient` bean already exists (for example from `redisson-spring-boot-starter`), leave
`redis-module.config` unset and the module clients wrap that bean without touching its lifecycle.

### Custom JSON serialization

RedisJSON uses Gson by default. Define a `JsonCodec` bean to use your own library instead:

```java
@Bean
public JsonCodec jsonCodec(ObjectMapper mapper) {
    return new JsonCodec() {
        public String toJson(Object value) { return mapper.writeValueAsString(value); }
        public <T> T fromJson(String json, Class<T> type) { return mapper.readValue(json, type); }
    };
}
```

## Usage

```java
@Service
public class UserService {

    private final RedisJSON redisJSON;

    public UserService(RedisJSONClient redisJSONClient) {
        this.redisJSON = redisJSONClient.getRedisJSON();
    }

    public void save(String id, String json) {
        redisJSON.set("user:" + id, SetArgs.Builder.create(".", json));
    }
}
```

Client beans are destroyed with the context; a client built on the shared `RedissonClient` leaves that bean
to be closed by its own owner.
