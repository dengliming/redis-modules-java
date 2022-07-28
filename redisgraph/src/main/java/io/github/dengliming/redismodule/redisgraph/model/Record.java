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

package io.github.dengliming.redismodule.redisgraph.model;

import java.util.List;

public class Record<T> {
    private final List<String> header;
    private final List<Object> values;

    public Record(List<String> header, List<Object> values) {
        this.header = header;
        this.values = values;
    }

    public <T> T getValue(int index) {
        return (T) this.values.get(index);
    }

    public <T> T getValue(String key) {
        return getValue(this.header.indexOf(key));
    }

    public String getString(int index) {
        return this.values.get(index).toString();
    }

    public String getString(String key) {
        return getString(this.header.indexOf(key));
    }

    public List<String> keys() {
        return header;
    }

    public List<Object> values() {
        return this.values;
    }

    public boolean containsKey(String key) {
        return this.header.contains(key);
    }

    public int size() {
        return this.header.size();
    }
}
