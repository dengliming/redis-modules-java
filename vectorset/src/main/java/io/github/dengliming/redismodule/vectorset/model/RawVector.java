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

/**
 * The internal representation of an element's vector, as returned by {@code VEMB ... RAW}.
 */
public class RawVector {
    private final String quantization;
    private final byte[] blob;
    private final double norm;
    private final Double range;

    public RawVector(String quantization, byte[] blob, double norm, Double range) {
        this.quantization = quantization;
        this.blob = blob;
        this.norm = norm;
        this.range = range;
    }

    /**
     * @return {@code f32}, {@code int8} or {@code bin}
     */
    public String getQuantization() {
        return quantization;
    }

    /**
     * @return little-endian 4-byte floats for fp32, a bitmap for bin, a byte array for int8
     */
    public byte[] getBlob() {
        return blob;
    }

    /**
     * @return L2 norm of the vector before normalization
     */
    public double getNorm() {
        return norm;
    }

    /**
     * @return quantization range (int8 only): multiply integer components by it to recover normalized values
     */
    public Double getRange() {
        return range;
    }
}
