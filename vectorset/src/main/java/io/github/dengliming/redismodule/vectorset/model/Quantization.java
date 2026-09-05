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

package io.github.dengliming.redismodule.vectorset.model;

import io.github.dengliming.redismodule.vectorset.protocol.Keywords;

/**
 * Vector quantization used by a vector set. Chosen by the first VADD on a key; later calls may repeat it to
 * assert the set has the expected format.
 */
public enum Quantization {
    /** 32-bit floats, no quantization. */
    NOQUANT(Keywords.NOQUANT),
    /** Signed 8-bit quantization, the default. */
    Q8(Keywords.Q8),
    /** Binary quantization: fastest and smallest, lowest recall. */
    BIN(Keywords.BIN);

    private final Keywords keyword;

    Quantization(Keywords keyword) {
        this.keyword = keyword;
    }

    public Keywords getKeyword() {
        return keyword;
    }
}
