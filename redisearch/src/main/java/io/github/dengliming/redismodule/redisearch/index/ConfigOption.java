/*
 * Copyright 2020-2024 dengliming.
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

package io.github.dengliming.redismodule.redisearch.index;

/**
 * RediSearch runtime configuration options.
 * <p>
 * {@link #getKeyword()} is the FT.CONFIG name used up to RediSearch 2.x; {@link #getConfigParameter()} is the
 * {@code CONFIG GET/SET} parameter that replaces it from Redis 8 on, where FT.CONFIG no longer exists.
 */
public enum ConfigOption {
    NOGC("NOGC", "search-no-gc"),
    MINPREFIX("MINPREFIX", "search-min-prefix"),
    MAXEXPANSIONS("MAXEXPANSIONS", "search-max-prefix-expansions"),
    TIMEOUT("TIMEOUT", "search-timeout"),
    ON_TIMEOUT("ON_TIMEOUT", "search-on-timeout"),
    MIN_PHONETIC_TERM_LEN("MIN_PHONETIC_TERM_LEN", "search-min-phonetic-term-len");

    private final String keyword;
    private final String configParameter;

    ConfigOption(String keyword, String configParameter) {
        this.keyword = keyword;
        this.configParameter = configParameter;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getConfigParameter() {
        return configParameter;
    }
}
