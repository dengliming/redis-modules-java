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

package io.github.dengliming.redismodule.common.util;

import java.util.Map;

/**
 * Assertion utility class that assists in validating arguments.
 *
 * @author dengliming
 */
public final class RAssert {

    /**
     * Assert that an object is not {@code null}
     *
     * @param object
     * @param message
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that a string is not empty, it must not be {@code null} and it must not be empty.
     *
     * @param string
     * @param message
     */
    public static void notEmpty(CharSequence string, String message) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array contains elements; that is, it must not be {@code null} and must contain at least one element.
     *
     * @param array
     * @param message
     */
    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(int[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that a Map contains entries; that is, it must not be {@code null} and must contain at least one entry.
     *
     * @param map
     * @param message
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
