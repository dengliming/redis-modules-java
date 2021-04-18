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

import java.util.HashMap;
import java.util.Map;

/**
 * @author dengliming
 */
public class Document {

    private String id;
    private double score;
    private byte[] payload;
    private Map<String, Object> fields;

    public Document(String id, double score) {
        this(id, score, null, new HashMap<>());
    }

    public Document(String id, double score, Map<String, Object> fields) {
        this(id, score, null, fields);
    }

    public Document(String id, double score, byte[] payload, Map<String, Object> fields) {
        this.id = id;
        this.score = score;
        this.payload = payload;
        this.fields = fields;
    }

    public Document field(String field, String value) {
        this.fields.put(field, value);
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public Document setScore(double score) {
        this.score = score;
        return this;
    }

    public byte[] getPayload() {
        return payload;
    }

    public Document setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
