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

package io.github.dengliming.redismodule.redisearch.search;

import io.github.dengliming.redismodule.redisearch.protocol.Keywords;

import java.util.List;

/**
 * @author dengliming
 */
public class SpellCheckOptions {

    private int distance;
    private String[] exclusionDicts;
    private String[] inclusionDicts;

    public SpellCheckOptions distance(int distance) {
        this.distance = distance;
        return this;
    }

    public SpellCheckOptions exclude(String... exclusionDicts) {
        this.exclusionDicts = exclusionDicts;
        return this;
    }

    public SpellCheckOptions include(String... inclusionDicts) {
        this.inclusionDicts = inclusionDicts;
        return this;
    }

    public void build(List<Object> args) {
        if (distance > 0) {
            args.add(Keywords.DISTANCE);
            args.add(distance);
        }

        if (exclusionDicts != null) {
            for (String exclusionDict : exclusionDicts) {
                args.add(Keywords.TERMS);
                args.add(Keywords.EXCLUDE);
                args.add(exclusionDict);
            }
        }

        if (inclusionDicts != null) {
            for (String inclusionDict : inclusionDicts) {
                args.add(Keywords.TERMS);
                args.add(Keywords.EXCLUDE);
                args.add(inclusionDict);
            }
        }
    }
}
