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

package io.github.dengliming.redismodule.springboot.starter.env;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = RedisModuleProperties.PREFIX)
public class RedisModuleProperties {
    public static final String PREFIX = "redis-module";

    private boolean enabled;
    private RedisModuleConfig redisai;
    private RedisModuleConfig redisbloom;
    private RedisModuleConfig redisearch;
    private RedisModuleConfig redisgears;
    private RedisModuleConfig redisgraph;
    private RedisModuleConfig redisjson;
    private RedisModuleConfig redistimeseries;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RedisModuleConfig getRedisai() {
        return redisai;
    }

    public void setRedisai(RedisModuleConfig redisai) {
        this.redisai = redisai;
    }

    public RedisModuleConfig getRedisbloom() {
        return redisbloom;
    }

    public void setRedisbloom(RedisModuleConfig redisbloom) {
        this.redisbloom = redisbloom;
    }

    public RedisModuleConfig getRedisearch() {
        return redisearch;
    }

    public void setRedisearch(RedisModuleConfig redisearch) {
        this.redisearch = redisearch;
    }

    public RedisModuleConfig getRedisgears() {
        return redisgears;
    }

    public void setRedisgears(RedisModuleConfig redisgears) {
        this.redisgears = redisgears;
    }

    public RedisModuleConfig getRedisgraph() {
        return redisgraph;
    }

    public void setRedisgraph(RedisModuleConfig redisgraph) {
        this.redisgraph = redisgraph;
    }

    public RedisModuleConfig getRedisjson() {
        return redisjson;
    }

    public void setRedisjson(RedisModuleConfig redisjson) {
        this.redisjson = redisjson;
    }

    public RedisModuleConfig getRedistimeseries() {
        return redistimeseries;
    }

    public void setRedistimeseries(RedisModuleConfig redistimeseries) {
        this.redistimeseries = redistimeseries;
    }

    public static class RedisModuleConfig {
        private boolean enabled;
        private String config;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }
    }
}
