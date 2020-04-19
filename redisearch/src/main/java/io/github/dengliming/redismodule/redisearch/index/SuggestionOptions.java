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

/**
 * @author dengliming
 */
public class SuggestionOptions {

    private boolean fuzzy;
    private boolean withScores;
    private boolean withPayloads;
    private int maxNum;

    public SuggestionOptions(Builder builder) {
        this.fuzzy = builder.fuzzy;
        this.withScores = builder.withScores;
        this.withPayloads = builder.withPayloads;
        this.maxNum = builder.maxNum;
    }

    public boolean isFuzzy() {
        return fuzzy;
    }

    public boolean isWithScores() {
        return withScores;
    }

    public boolean isWithPayloads() {
        return withPayloads;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public static class Builder {

        private boolean fuzzy;
        private boolean withScores;
        private boolean withPayloads;
        private int maxNum;

        private Builder() {
        }

        public Builder fuzzy() {
            this.fuzzy = true;
            return this;
        }

        public Builder withScores() {
            this.withScores = true;
            return this;
        }

        public Builder withPayloads() {
            this.withPayloads = true;
            return this;
        }

        public Builder maxNum(int num) {
            this.maxNum = num;
            return this;
        }

        public SuggestionOptions build() {
            return new SuggestionOptions(this);
        }
    }
}
