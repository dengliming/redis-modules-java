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

package io.github.dengliming.redismodule.redistimeseries;

/**
 * Aggregation type
 *
 * @author dengliming
 */
public enum Aggregation {
    AVG("avg"), SUM("sum"), MIN("min"), MAX("max"), RANGE("range"), COUNT("count"),
    FIRST("first"), LAST("last"), STD_P("std.p"), STD_S("std.s"), VAR_P("var.p"), VAR_S("var.s");

    private String key;

    Aggregation(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
