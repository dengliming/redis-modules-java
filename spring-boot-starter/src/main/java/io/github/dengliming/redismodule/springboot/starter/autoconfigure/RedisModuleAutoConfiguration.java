/*
 * Copyright 2024 dengliming.
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

import io.github.dengliming.redismodule.redisai.client.RedisAIClient;
import io.github.dengliming.redismodule.redisbloom.client.RedisBloomClient;
import io.github.dengliming.redismodule.redisearch.client.RediSearchClient;
import io.github.dengliming.redismodule.redisgears.client.RedisGearsClient;
import io.github.dengliming.redismodule.redisgraph.client.RedisGraphClient;
import io.github.dengliming.redismodule.redisjson.client.RedisJSONClient;
import io.github.dengliming.redismodule.redistimeseries.client.RedisTimeSeriesClient;
import io.github.dengliming.redismodule.springboot.starter.env.RedisModuleProperties;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

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

    @ConditionalOnProperty(
            prefix = RedisModuleProperties.PREFIX + ".redisearch",
            name = "enabled",
            havingValue = "true"
    )
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RediSearchClient rediSearchClient() {
        return new RediSearchClient(parseRedisModuleConfig(redisModuleProperties.getRedisearch().getConfig()));
    }

    @ConditionalOnProperty(
            prefix = RedisModuleProperties.PREFIX + ".redisai",
            name = "enabled",
            havingValue = "true"
    )
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisAIClient redisAIClient() {
        return new RedisAIClient(parseRedisModuleConfig(redisModuleProperties.getRedisai().getConfig()));
    }

    @ConditionalOnProperty(
            prefix = RedisModuleProperties.PREFIX + ".redisbloom",
            name = "enabled",
            havingValue = "true"
    )
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisBloomClient redisBloomClient() {
        return new RedisBloomClient(parseRedisModuleConfig(redisModuleProperties.getRedisbloom().getConfig()));
    }

    @ConditionalOnProperty(
            prefix = RedisModuleProperties.PREFIX + ".redisgears",
            name = "enabled",
            havingValue = "true"
    )
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisGearsClient redisGearsClient() {
        return new RedisGearsClient(parseRedisModuleConfig(redisModuleProperties.getRedisgears().getConfig()));
    }

    @ConditionalOnProperty(
            prefix = RedisModuleProperties.PREFIX + ".redisgraph",
            name = "enabled",
            havingValue = "true"
    )
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisGraphClient redisGraphClient() {
        return new RedisGraphClient(parseRedisModuleConfig(redisModuleProperties.getRedisgraph().getConfig()));
    }

    @ConditionalOnProperty(
            prefix = RedisModuleProperties.PREFIX + ".redisjson",
            name = "enabled",
            havingValue = "true"
    )
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisJSONClient redisJSONClient() {
        return new RedisJSONClient(parseRedisModuleConfig(redisModuleProperties.getRedisjson().getConfig()));
    }

    @ConditionalOnProperty(
            prefix = RedisModuleProperties.PREFIX + ".redistimeseries",
            name = "enabled",
            havingValue = "true"
    )
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedisTimeSeriesClient redisTimeSeriesClient() {
        return new RedisTimeSeriesClient(parseRedisModuleConfig(redisModuleProperties.getRedistimeseries().getConfig()));
    }

    private Config parseRedisModuleConfig(String configStr) {
        Config config;
        try {
            config = Config.fromYAML(configStr);
        } catch (IOException e) {
            try {
                config = Config.fromJSON(configStr);
            } catch (IOException ex) {
                ex.addSuppressed(e);
                throw new IllegalArgumentException("Can't parse config", ex);
            }
        }
        return config;
    }
}
