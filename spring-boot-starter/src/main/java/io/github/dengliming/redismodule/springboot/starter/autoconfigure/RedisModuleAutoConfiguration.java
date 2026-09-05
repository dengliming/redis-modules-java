/*
 * Copyright 2023-2024 dengliming.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dengliming.redismodule.springboot.starter.autoconfigure;

import io.github.dengliming.redismodule.common.BaseRedissonClient;
import io.github.dengliming.redismodule.redisai.client.RedisAIClient;
import io.github.dengliming.redismodule.redisbloom.client.RedisBloomClient;
import io.github.dengliming.redismodule.redisearch.client.RediSearchClient;
import io.github.dengliming.redismodule.redisgears.client.RedisGearsClient;
import io.github.dengliming.redismodule.redisgraph.client.RedisGraphClient;
import io.github.dengliming.redismodule.redisjson.client.RedisJSONClient;
import io.github.dengliming.redismodule.redisjson.codec.JsonCodec;
import io.github.dengliming.redismodule.redisjson.codec.JsonCodecs;
import io.github.dengliming.redismodule.redistimeseries.client.RedisTimeSeriesClient;
import io.github.dengliming.redismodule.springboot.starter.env.RedisModuleProperties;
import io.github.dengliming.redismodule.springboot.starter.env.RedisModuleProperties.RedisModuleConfig;
import io.github.dengliming.redismodule.vectorset.client.VectorSetClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.function.Function;

/**
 * Registers one client bean per enabled module.
 * <p>
 * Each module either gets its own Redisson instance from {@code redis-module.<module>.config}, or shares the
 * {@link RedissonClient} bean of the context. That shared bean is created from {@code redis-module.config}
 * unless the application already defines one (for example through the Redisson starter).
 */
@ConditionalOnProperty(
        prefix = RedisModuleProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@ConditionalOnClass(Redisson.class)
@EnableConfigurationProperties(RedisModuleProperties.class)
@Configuration
public class RedisModuleAutoConfiguration {
    private final RedisModuleProperties redisModuleProperties;

    public RedisModuleAutoConfiguration(RedisModuleProperties redisModuleProperties) {
        this.redisModuleProperties = redisModuleProperties;
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX, name = "config")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redisModuleRedissonClient() {
        return Redisson.create(parseRedisModuleConfig(redisModuleProperties.getConfig()));
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".redisearch", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RediSearchClient rediSearchClient(ObjectProvider<RedissonClient> redisson) {
        return createClient("redisearch", redisModuleProperties.getRedisearch(), redisson, RediSearchClient::new, RediSearchClient::new);
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".redisai", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisAIClient redisAIClient(ObjectProvider<RedissonClient> redisson) {
        return createClient("redisai", redisModuleProperties.getRedisai(), redisson, RedisAIClient::new, RedisAIClient::new);
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".redisbloom", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisBloomClient redisBloomClient(ObjectProvider<RedissonClient> redisson) {
        return createClient("redisbloom", redisModuleProperties.getRedisbloom(), redisson, RedisBloomClient::new, RedisBloomClient::new);
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".redisgears", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisGearsClient redisGearsClient(ObjectProvider<RedissonClient> redisson) {
        return createClient("redisgears", redisModuleProperties.getRedisgears(), redisson, RedisGearsClient::new, RedisGearsClient::new);
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".redisgraph", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisGraphClient redisGraphClient(ObjectProvider<RedissonClient> redisson) {
        return createClient("redisgraph", redisModuleProperties.getRedisgraph(), redisson, RedisGraphClient::new, RedisGraphClient::new);
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".redisjson", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisJSONClient redisJSONClient(ObjectProvider<RedissonClient> redisson, ObjectProvider<JsonCodec> jsonCodec) {
        // a JsonCodec bean (Jackson, ...) defined by the application replaces the Gson default
        JsonCodec codec = jsonCodec.getIfAvailable(JsonCodecs::defaultCodec);
        Function<Config, RedisJSONClient> fromConfig = config -> new RedisJSONClient(config, codec);
        Function<RedissonClient, RedisJSONClient> fromRedisson = shared -> new RedisJSONClient(shared, codec);
        return createClient("redisjson", redisModuleProperties.getRedisjson(), redisson, fromConfig, fromRedisson);
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".redistimeseries", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisTimeSeriesClient redisTimeSeriesClient(ObjectProvider<RedissonClient> redisson) {
        return createClient("redistimeseries", redisModuleProperties.getRedistimeseries(), redisson,
                RedisTimeSeriesClient::new, RedisTimeSeriesClient::new);
    }

    @ConditionalOnProperty(prefix = RedisModuleProperties.PREFIX + ".vectorset", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public VectorSetClient vectorSetClient(ObjectProvider<RedissonClient> redisson) {
        return createClient("vectorset", redisModuleProperties.getVectorset(), redisson, VectorSetClient::new, VectorSetClient::new);
    }

    /**
     * A module with its own {@code config} gets a dedicated Redisson instance that the client shuts down with
     * the context. Otherwise the client wraps the shared {@link RedissonClient} bean and leaves its lifecycle alone.
     */
    private <T extends BaseRedissonClient> T createClient(String module, RedisModuleConfig moduleConfig,
                                                          ObjectProvider<RedissonClient> sharedRedisson,
                                                          Function<Config, T> fromConfig,
                                                          Function<RedissonClient, T> fromRedisson) {
        if (moduleConfig != null && StringUtils.hasText(moduleConfig.getConfig())) {
            return fromConfig.apply(parseRedisModuleConfig(moduleConfig.getConfig()));
        }
        RedissonClient redisson = sharedRedisson.getIfAvailable();
        if (redisson == null) {
            throw new IllegalStateException("No Redisson configuration for module '" + module + "': set "
                    + RedisModuleProperties.PREFIX + "." + module + ".config, the shared " + RedisModuleProperties.PREFIX
                    + ".config, or define a RedissonClient bean");
        }
        return fromRedisson.apply(redisson);
    }

    private Config parseRedisModuleConfig(String configStr) {
        try {
            // YAML is a superset of JSON, so JSON configurations parse as well
            return Config.fromYAML(configStr);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Can't parse Redisson config", e);
        }
    }
}
