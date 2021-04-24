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
import io.github.dengliming.redismodule.redisjson.protocol.Keywords;

import java.util.ArrayList;
import java.util.List;

public final class SetArgs {

    private String path;
    private String json;
    private boolean nx;
    private boolean xx;

    private SetArgs() {

    }

    public SetArgs path(String path) {
        RAssert.notEmpty(path, "path must not be empty");

        this.path = path;
        return this;
    }

    public SetArgs json(String json) {
        RAssert.notEmpty(json, "json must not be empty");

        this.json = json;
        return this;
    }

    public SetArgs nx() {
        this.nx = true;
        return this;
    }

    public SetArgs xx() {
        this.xx = true;
        return this;
    }

    public List<Object> build(String key) {
        List<Object> args = new ArrayList<>();
        args.add(key);
        args.add(path);
        args.add(json);
        if (nx) {
            args.add(Keywords.NX);
        } else if (xx) {
            args.add(Keywords.XX);
        }
        return args;
    }

    /**
     * Builder entry points for {@link SetArgs}.
     */
    public static final class Builder {
        /**
         * Utility constructor.
         */
        private Builder() {
        }

        public static SetArgs create(String path, String json) {
            return new SetArgs().path(path).json(json);
        }

        public static SetArgs nx(String path, String json) {
            return new SetArgs().path(path).json(json).nx();
        }

        public static SetArgs xx(String path, String json) {
            return new SetArgs().path(path).json(json).xx();
        }
    }
}
