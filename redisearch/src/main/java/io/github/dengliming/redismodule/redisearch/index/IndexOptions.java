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

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public boolean isMaxTextFields() {
        return maxTextFields;
    }

    public void setMaxTextFields(boolean maxTextFields) {
        this.maxTextFields = maxTextFields;
    }

    public boolean isNoOffsets() {
        return noOffsets;
    }

    public void setNoOffsets(boolean noOffsets) {
        this.noOffsets = noOffsets;
    }

    public boolean isNoFreqs() {
        return noFreqs;
    }

    public void setNoFreqs(boolean noFreqs) {
        this.noFreqs = noFreqs;
    }

    public boolean isNoFields() {
        return noFields;
    }

    public void setNoFields(boolean noFields) {
        this.noFields = noFields;
    }

    public boolean isNoHL() {
        return noHL;
    }

    public void setNoHL(boolean noHL) {
        this.noHL = noHL;
    }

    public List<String> getStopwords() {
        return stopwords;
    }

    public void setStopwords(List<String> stopwords) {
        this.stopwords = stopwords;
    }

    public static IndexOptions defaultOptions() {
        return new IndexOptions();
    }
}
