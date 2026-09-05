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
 * Converts between Java objects and the JSON documents RedisJSON stores. The default implementation is
 * {@link GsonJsonCodec}; supply your own (Jackson, Moshi, ...) through {@code RedisJSONClient} or
 * {@code RedisJSON} to reuse the serialization setup of your application.
 */
public interface JsonCodec {

    /**
     * Serializes a value to a JSON document.
     */
    String toJson(Object value);

    /**
     * Deserializes a JSON document into the given type.
     */
    <T> T fromJson(String json, Class<T> type);
}
