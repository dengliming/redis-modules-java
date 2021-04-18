/*
 * Copyright 2020 dengliming.
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
 * @author dengliming
 */
public enum ConfigOption {
    NOGC("NOGC"),
    MINPREFIX("MINPREFIX"),
    MAXEXPANSIONS("MAXEXPANSIONS"),
    TIMEOUT("TIMEOUT"),
    ON_TIMEOUT("ON_TIMEOUT"),
    MIN_PHONETIC_TERM_LEN("MIN_PHONETIC_TERM_LEN");

    private String keyword;

    ConfigOption(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
