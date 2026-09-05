/*
 * Copyright 2024 dengliming.
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

package io.github.dengliming.redismodule.redisjson.codec;

import com.google.gson.Gson;

/**
 * {@link JsonCodec} backed by Gson. The default codec of the RedisJSON module.
 */
public class GsonJsonCodec implements JsonCodec {

    public static final GsonJsonCodec INSTANCE = new GsonJsonCodec();

    private final Gson gson;

    public GsonJsonCodec() {
        this(new Gson());
    }

    public GsonJsonCodec(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String toJson(Object value) {
        return gson.toJson(value);
    }

    @Override
    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
