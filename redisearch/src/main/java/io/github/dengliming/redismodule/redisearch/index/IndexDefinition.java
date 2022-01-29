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

package io.github.dengliming.redismodule.redisearch.index;

import java.util.List;

public class IndexDefinition {

    public enum DataType {
        HASH,
        JSON
    }

    private final DataType dataType;

    private boolean async;
    private List<String> prefixes;
    private String filter;
    private RSLanguage languageField;
    private RSLanguage language;
    private String scoreFiled;
    private double score = 1.0;
    private String payloadField;

    public IndexDefinition() {
        this(DataType.HASH);
    }

    public IndexDefinition(DataType dataType) {
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isAsync() {
        return async;
    }

    public IndexDefinition setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public List<String> getPrefixes() {
        return prefixes;
    }

    public IndexDefinition setPrefixes(List<String> prefixes) {
        this.prefixes = prefixes;
        return this;
    }

    public String getFilter() {
        return filter;
    }

    public IndexDefinition setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public RSLanguage getLanguageField() {
        return languageField;
    }

    public IndexDefinition setLanguageField(RSLanguage languageField) {
        this.languageField = languageField;
        return this;
    }

    public RSLanguage getLanguage() {
        return language;
    }

    public IndexDefinition setLanguage(RSLanguage language) {
        this.language = language;
        return this;
    }

    public String getScoreFiled() {
        return scoreFiled;
    }

    public IndexDefinition setScoreFiled(String scoreFiled) {
        this.scoreFiled = scoreFiled;
        return this;
    }

    public double getScore() {
        return score;
    }

    public IndexDefinition setScore(double score) {
        this.score = score;
        return this;
    }

    public String getPayloadField() {
        return payloadField;
    }

    public IndexDefinition setPayloadField(String payloadField) {
        this.payloadField = payloadField;
        return this;
    }
}
