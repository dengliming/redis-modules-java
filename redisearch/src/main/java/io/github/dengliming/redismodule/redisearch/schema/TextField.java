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
package io.github.dengliming.redismodule.redisearch.schema;

import io.github.dengliming.redismodule.redisearch.index.Phonetic;

/**
 * @author dengliming
 */
public class TextField extends Field {

    private final double weight;
    private final boolean noStem;
    private final Phonetic phonetic;

    public TextField(String name) {
        this(name, 1.0d);
    }

    public TextField(String name, double weight) {
        this(name, weight, false);
    }

    public TextField(String name, double weight, boolean sortable) {
        this(name, weight, sortable, false);
    }

    public TextField(String name, double weight, boolean sortable, boolean noStem) {
        this(name, weight, sortable, noStem, false);
    }

    public TextField(String name, double weight, boolean sortable, boolean noStem, boolean noIndex) {
        this(name, weight, sortable, noStem, noIndex, null);
    }

    public TextField(String name, double weight, boolean sortable, boolean noStem, boolean noIndex, Phonetic phonetic) {
        super(name, FieldType.TEXT, sortable, noIndex);
        this.weight = weight;
        this.noStem = noStem;
        this.phonetic = phonetic;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isNoStem() {
        return noStem;
    }

    public Phonetic getPhonetic() {
        return phonetic;
    }
}
