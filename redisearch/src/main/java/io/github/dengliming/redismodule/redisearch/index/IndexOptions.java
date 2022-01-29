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

package io.github.dengliming.redismodule.redisearch.index;

import io.github.dengliming.redismodule.redisearch.protocol.Keywords;

import java.util.List;

/**
 * @author dengliming
 */
public class IndexOptions {

    private long expire;
    private boolean maxTextFields;
    private boolean noOffsets;
    private boolean noFreqs;
    private boolean noFields;
    private boolean noHL;
    private List<String> stopwords;
    private List<String> prefixes;
    private IndexDefinition definition;

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public boolean isMaxTextFields() {
        return maxTextFields;
    }

    public IndexOptions maxTextFields() {
        this.maxTextFields = true;
        return this;
    }

    public boolean isNoOffsets() {
        return noOffsets;
    }

    public IndexOptions noOffsets() {
        this.noOffsets = true;
        return this;
    }

    public boolean isNoFreqs() {
        return noFreqs;
    }

    public IndexOptions noFreqs() {
        this.noFreqs = true;
        return this;
    }

    public boolean isNoFields() {
        return noFields;
    }

    public IndexOptions noFields() {
        this.noFields = true;
        return this;
    }

    public boolean isNoHL() {
        return noHL;
    }

    public IndexOptions noHL() {
        this.noHL = true;
        return this;
    }

    public List<String> getStopwords() {
        return stopwords;
    }

    public IndexOptions stopwords(List<String> stopwords) {
        this.stopwords = stopwords;
        return this;
    }

    public List<String> getPrefixes() {
        return prefixes;
    }

    public IndexOptions prefixes(List<String> prefixes) {
        this.prefixes = prefixes;
        return this;
    }

    public IndexOptions definition(IndexDefinition definition) {
        this.definition = definition;
        return this;
    }

    public IndexDefinition getDefinition() {
        return definition;
    }

    public static IndexOptions defaultOptions() {
        return new IndexOptions();
    }

    public void build(List<Object> args) {
        definition = getDefinition();
        if (definition != null) {
            args.add(Keywords.ON);
            args.add(definition.getDataType());

            if (definition.getPrefixes() != null) {
                args.add(Keywords.PREFIX.name());
                args.add(definition.getPrefixes().size());
                args.addAll(definition.getPrefixes());
            }

            if (definition.getFilter() != null) {
                args.add(Keywords.FILTER);
                args.add(definition.getFilter());
            }

            if (definition.getLanguage() != null) {
                args.add(Keywords.LANGUAGE);
                args.add(definition.getLanguage());
            }

            if (definition.getLanguageField() != null) {
                args.add(Keywords.LANGUAGE_FIELD);
                args.add(definition.getLanguageField());
            }

            args.add(Keywords.SCORE);
            args.add(definition.getScore());

            if (definition.getScoreFiled() != null) {
                args.add(Keywords.SCORE_FIELD);
                args.add(definition.getScoreFiled());
            }

            if (definition.getPayloadField() != null) {
                args.add(Keywords.PAYLOAD_FIELD);
                args.add(definition.getPayloadField());
            }
        }

        if (isMaxTextFields()) {
            args.add(Keywords.MAXTEXTFIELDS.name());
        }
        if (getExpire() > 0) {
            args.add(Keywords.TEMPORARY.name());
            args.add(getExpire());
        }
        if (isNoOffsets()) {
            args.add(Keywords.NOOFFSETS.name());
        }
        if (isNoHL()) {
            args.add(Keywords.NOHL.name());
        }
        if (isNoFields()) {
            args.add(Keywords.NOFIELDS.name());
        }
        if (isNoFreqs()) {
            args.add(Keywords.NOFREQS.name());
        }
        if (getStopwords() != null) {
            args.add(Keywords.STOPWORDS.name());
            args.add(getStopwords().size());
            args.addAll(getStopwords());
        }
    }
}
