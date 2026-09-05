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

package io.github.dengliming.redismodule.redisjson.utils;

import io.github.dengliming.redismodule.redisjson.codec.GsonJsonCodec;

/**
 * Static shortcuts to the default {@link GsonJsonCodec}. Prefer injecting a
 * {@link io.github.dengliming.redismodule.redisjson.codec.JsonCodec} where the serialization strategy matters.
 */
public final class GsonUtils {

    private GsonUtils() {
    }

    public static String toJson(Object o) {
        return GsonJsonCodec.INSTANCE.toJson(o);
    }

    /**
     * From json to specified Class.
     *
     * @param <T>    the type parameter
     * @param json   the json
     * @param clazz the t class
     * @return the t
     */
    public static <T> T fromJson(final String json, final Class<T> clazz) {
        return GsonJsonCodec.INSTANCE.fromJson(json, clazz);
    }
}
