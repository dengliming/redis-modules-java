/*
 * Copyright 2020-2022 dengliming.
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

/**
 * @author dengliming
 */
public class Field {
    private String name;
    private String attribute;
    private boolean sortable;
    private boolean noIndex;
    private boolean unNormalizedForm;
    private boolean withSuffixTrie;
    private boolean indexEmpty;
    private boolean indexMissing;
    private FieldType fieldType;

    public Field(String name, FieldType fieldType) {
        this.name = name;
        this.fieldType = fieldType;
    }

    public Field sortable() {
        this.sortable = true;
        return this;
    }

    public Field noIndex() {
        this.noIndex = true;
        return this;
    }

    /**
     * Keep the original letter case and accents of a SORTABLE field (SORTABLE UNF).
     */
    public Field unNormalizedForm() {
        this.unNormalizedForm = true;
        return this;
    }

    /**
     * Build a suffix trie for fast suffix and infix queries (WITHSUFFIXTRIE, TEXT and TAG fields).
     */
    public Field withSuffixTrie() {
        this.withSuffixTrie = true;
        return this;
    }

    /**
     * Index empty strings so that {@code @field:""} matches (INDEXEMPTY, RediSearch 2.10+).
     */
    public Field indexEmpty() {
        this.indexEmpty = true;
        return this;
    }

    /**
     * Index documents lacking the field so that {@code ismissing(@field)} matches (INDEXMISSING, RediSearch 2.10+).
     */
    public Field indexMissing() {
        this.indexMissing = true;
        return this;
    }

    public boolean isUnNormalizedForm() {
        return unNormalizedForm;
    }

    public boolean isWithSuffixTrie() {
        return withSuffixTrie;
    }

    public boolean isIndexEmpty() {
        return indexEmpty;
    }

    public boolean isIndexMissing() {
        return indexMissing;
    }

    public String getName() {
        return name;
    }

    public boolean isSortable() {
        return sortable;
    }

    public boolean isNoIndex() {
        return noIndex;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getAttribute() {
        return attribute;
    }

    public Field attribute(String attribute) {
        this.attribute = attribute;
        return this;
    }
}
