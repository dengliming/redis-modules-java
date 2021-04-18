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
public class HighlightOptions {

    private String[] fields;
    private Tags tags;

    public HighlightOptions tags(Tags tags) {
        this.tags = tags;
        return this;
    }

    public HighlightOptions fields(String... fields) {
        this.fields = fields;
        return this;
    }

    public void build(List<Object> args) {
        args.add(Keywords.HIGHLIGHT);
        if (fields != null) {
            args.add(Keywords.FIELDS);
            for (String field : fields) {
                args.add(field);
            }
        }
        if (tags != null) {
            tags.build(args);
        }
    }
}
