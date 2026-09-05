/*
 * Copyright 2020-2024 dengliming.
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

package io.github.dengliming.redismodule.redisbloom.protocol.decoder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * View over a RedisBloom {@code *.INFO} reply, which is a flat list of alternating field names and values.
 * Fields are looked up by name (case- and whitespace-insensitive) so that reordered or newly added fields
 * across RedisBloom versions do not shift every value.
 */
final class InfoReply {

    private final Map<String, Object> values = new HashMap<>();

    InfoReply(List<Object> parts) {
        for (int i = 0; i + 1 < parts.size(); i += 2) {
            values.put(normalize(String.valueOf(parts.get(i))), parts.get(i + 1));
        }
    }

    /**
     * @return the field as an Integer, or null if absent
     */
    Integer getInteger(String field) {
        Long value = getLong(field);
        return value == null ? null : value.intValue();
    }

    /**
     * @return the field as a Long, or null if absent
     */
    Long getLong(String field) {
        Object value = values.get(normalize(field));
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    long getLong(String field, long defaultValue) {
        Long value = getLong(field);
        return value == null ? defaultValue : value;
    }

    /**
     * @return the field as a Double, or null if absent
     */
    Double getDouble(String field) {
        Object value = values.get(normalize(field));
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    double getDouble(String field, double defaultValue) {
        Double value = getDouble(field);
        return value == null ? defaultValue : value;
    }

    private static String normalize(String field) {
        return field.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
