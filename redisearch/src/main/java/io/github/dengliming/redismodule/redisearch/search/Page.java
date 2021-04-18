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
public class Page {

    private int offset;
    private int num = 10;

    public Page(int offset, int num) {
        this.offset = offset;
        this.num = num;
    }

    public void build(List<Object> args) {
        args.add(Keywords.LIMIT);
        args.add(offset);
        args.add(num);
    }
}
