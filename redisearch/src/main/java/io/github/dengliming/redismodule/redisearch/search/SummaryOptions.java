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

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 */
public class SummaryOptions {

    private List<String> fields;
    private int frags;
    private int fragSize;
    private String separator;

    public SummaryOptions() {
        this.fields = new ArrayList<>();
    }

    public SummaryOptions frags(int frags) {
        this.frags = frags;
        return this;
    }

    public SummaryOptions fragSize(int fragSize) {
        this.fragSize = fragSize;
        return this;
    }

    public SummaryOptions separator(String separator) {
        this.separator = separator;
        return this;
    }

    public SummaryOptions fields(String... fields) {
        for (String field : fields) {
            this.fields.add(field);
        }
        return this;
    }

    public void build(List<Object> args) {
        args.add(Keywords.SUMMARIZE);
        if (fields.size() > 0) {
            args.add(Keywords.FIELDS);
            args.add(fields.size());
            args.addAll(fields);
        }
        if (frags > 0) {
            args.add(Keywords.FRAGS);
            args.add(frags);
        }
        if (fragSize > 0) {
            args.add(Keywords.LEN);
            args.add(fragSize);
        }
        if (separator != null) {
            args.add(Keywords.SEPARATOR);
            args.add(separator);
        }
    }
}
