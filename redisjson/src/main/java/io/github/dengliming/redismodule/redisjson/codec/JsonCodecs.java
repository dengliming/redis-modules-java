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

/**
 * Resolves the {@link JsonCodec} used when none is supplied explicitly.
 * <p>
 * Gson is an optional dependency of this module. The default codec is only looked up when a
 * {@code RedisJSON} / {@code RedisJSONClient} is created without a codec, and the Gson-backed class is
 * loaded only if Gson is on the classpath, so applications that bring their own {@link JsonCodec}
 * never need Gson.
 */
public final class JsonCodecs {

    private static final String GSON_CLASS = "com.google.gson.Gson";

    private JsonCodecs() {
    }

    /**
     * @return the Gson-backed codec
     * @throws IllegalStateException if Gson is not on the classpath
     */
    public static JsonCodec defaultCodec() {
        if (!isGsonPresent()) {
            throw new IllegalStateException("No JsonCodec configured and Gson is not on the classpath. "
                    + "Add com.google.code.gson:gson or pass a JsonCodec to RedisJSONClient / RedisJSON.");
        }
        return GsonHolder.CODEC;
    }

    public static boolean isGsonPresent() {
        try {
            Class.forName(GSON_CLASS, false, JsonCodecs.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Holder so that {@link GsonJsonCodec} (and with it Gson) is loaded only on first access.
     */
    private static final class GsonHolder {
        static final JsonCodec CODEC = GsonJsonCodec.INSTANCE;
    }
}
