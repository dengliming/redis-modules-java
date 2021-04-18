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

package io.github.dengliming.redismodule.redisearch.index.schema;

import io.github.dengliming.redismodule.redisearch.index.Phonetic;

/**
 * @author dengliming
 */
public class TextField extends Field {

    private final double weight;
    private boolean noStem;
    private Phonetic phonetic;

    public TextField(String name) {
        this(name, 1.0d);
    }

    public TextField(String name, double weight) {
        super(name, FieldType.TEXT);
        this.weight = weight;
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

    public TextField noStem() {
        this.noStem = true;
        return this;
    }

    public TextField phonetic(Phonetic phonetic) {
        this.phonetic = phonetic;
        return this;
    }
}
