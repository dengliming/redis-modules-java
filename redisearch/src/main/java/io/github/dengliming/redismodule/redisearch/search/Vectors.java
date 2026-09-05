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

package io.github.dengliming.redismodule.redisearch.search;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Encodes query and document vectors as the little-endian blobs RediSearch expects in hash fields and
 * PARAMS values.
 */
public final class Vectors {

    private Vectors() {
    }

    /**
     * Little-endian FLOAT32 blob, for {@code VectorType.FLOAT32} fields.
     */
    public static byte[] toFloat32Bytes(float... vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        for (float component : vector) {
            buffer.putFloat(component);
        }
        return buffer.array();
    }

    /**
     * Little-endian FLOAT32 blob from doubles, narrowing each component to float.
     */
    public static byte[] toFloat32Bytes(double... vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        for (double component : vector) {
            buffer.putFloat((float) component);
        }
        return buffer.array();
    }

    /**
     * Little-endian FLOAT64 blob, for {@code VectorType.FLOAT64} fields.
     */
    public static byte[] toFloat64Bytes(double... vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * Double.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        for (double component : vector) {
            buffer.putDouble(component);
        }
        return buffer.array();
    }
}
