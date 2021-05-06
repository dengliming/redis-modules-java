/*
 * Copyright 2021 dengliming.
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

package io.github.dengliming.redismodule.redisjson.args;

import io.github.dengliming.redismodule.common.util.RAssert;

import java.util.ArrayList;
import java.util.List;

import static io.github.dengliming.redismodule.redisjson.protocol.Keywords.INDENT;
import static io.github.dengliming.redismodule.redisjson.protocol.Keywords.NEWLINE;
import static io.github.dengliming.redismodule.redisjson.protocol.Keywords.NOESCAPE;
import static io.github.dengliming.redismodule.redisjson.protocol.Keywords.SPACE;

public final class GetArgs {

    private String[] paths;
    private String space;
    private boolean noEscape;
    private String newLine;
    private String indent;

    public GetArgs path(String... paths) {
        RAssert.notEmpty(paths, "paths must not be empty");

        this.paths = paths;
        return this;
    }

    public GetArgs space(String space) {
        RAssert.notEmpty(space, "space must not be empty");

        this.space = space;
        return this;
    }

    public GetArgs noEscape() {
        this.noEscape = true;
        return this;
    }

    public GetArgs newLine(String newLine) {
        this.newLine = newLine;
        return this;
    }

    public GetArgs indent(String indent) {
        this.indent = indent;
        return this;
    }

    public List<Object> build(String key) {
        List<Object> args = new ArrayList<>();
        args.add(key);
        if (indent != null) {
            args.add(INDENT);
            args.add(indent);
        }
        if (newLine != null) {
            args.add(NEWLINE);
            args.add(newLine);
        }
        if (space != null) {
            args.add(SPACE);
            args.add(space);
        }
        if (noEscape) {
            args.add(NOESCAPE);
        }
        if (paths != null) {
            for (String path : paths) {
                args.add(path);
            }
        }
        return args;
    }
}
