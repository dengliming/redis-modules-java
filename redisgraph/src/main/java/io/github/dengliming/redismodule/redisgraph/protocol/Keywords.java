/*
 * Copyright 2022 dengliming.
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

package io.github.dengliming.redismodule.redisgraph.protocol;

/**
 * @author dengliming
 */
public enum Keywords {
    __COMPACT("--COMPACT");

    private String alias;

    Keywords() {
        this.alias = name();
    }

    Keywords(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
