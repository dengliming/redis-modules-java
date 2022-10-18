/*
 * Copyright 2022 dengliming.
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

package io.github.dengliming.redismodule.common;

import io.github.dengliming.redismodule.common.util.RAssert;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RAssertTest {

    @Test
    public void test() {
        assertThrows(IllegalArgumentException.class, () -> RAssert.notNull(null, "test not null"),
                "test not null");

        assertThrows(IllegalArgumentException.class, () -> RAssert.notEmpty((CharSequence) null, "test CharSequence not empty"),
                "test CharSequence not empty");

        assertThrows(IllegalArgumentException.class, () -> RAssert.notEmpty(new Object[]{}, "test Object[] not empty"),
                "test Object[] not empty");

        assertThrows(IllegalArgumentException.class, () -> RAssert.notEmpty(new int[]{}, "test int[] not empty"),
                "test int[] not empty");

        assertThrows(IllegalArgumentException.class, () -> RAssert.notEmpty(new double[]{}, "test double[] not empty"),
                "test double[] not empty");

        assertThrows(IllegalArgumentException.class, () -> RAssert.isTrue(false, "test boolean is false"),
                "test boolean is false");

        assertThrows(IllegalArgumentException.class, () -> RAssert.notEmpty(new HashMap<>(), "test HashMap not empty"),
                "test HashMap not empty");
    }
}
