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

import io.github.dengliming.redismodule.redisearch.protocol.Keywords;

import java.util.List;

/**
 * @author dengliming
 */
public class SuggestionOptions {

    private boolean fuzzy;
    private boolean withScores;
    private boolean withPayloads;
    private int maxNum;

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

    public SuggestionOptions fuzzy() {
        this.fuzzy = true;
        return this;
    }

    public SuggestionOptions withScores() {
        this.withScores = true;
        return this;
    }

    public SuggestionOptions withPayloads() {
        this.withPayloads = true;
        return this;
    }

    public SuggestionOptions maxNum(int num) {
        this.maxNum = num;
        return this;
    }

    public void build(List<Object> args) {
        if (isFuzzy()) {
            args.add(Keywords.FUZZY.name());
        }
        if (isWithScores()) {
            args.add(Keywords.WITHSCORES.name());
        }
        if (isWithPayloads()) {
            args.add(Keywords.WITHPAYLOADS.name());
        }
        if (getMaxNum() > 0) {
            args.add(Keywords.MAX.name());
            args.add(getMaxNum());
        }
    }
}
