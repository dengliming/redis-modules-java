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

package io.github.dengliming.redismodule.vectorset.protocol.decoder;

import io.github.dengliming.redismodule.vectorset.model.RawVector;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Decodes {@code VEMB ... RAW}: quantization type, raw blob, L2 norm and (int8 only) quantization range.
 */
public class RawVectorDecoder implements MultiDecoder<RawVector> {

    private static final int BLOB_INDEX = 1;

    /**
     * The blob must stay binary; every other part is text.
     */
    @Override
    public Decoder<Object> getDecoder(Codec codec, int paramNum, State state) {
        if (paramNum == BLOB_INDEX) {
            return ByteArrayCodec.INSTANCE.getValueDecoder();
        }
        return StringCodec.INSTANCE.getValueDecoder();
    }

    @Override
    public RawVector decode(List<Object> parts, State state) {
        String quantization = asString(parts.get(0));
        byte[] blob = asBytes(parts.get(BLOB_INDEX));
        double norm = Double.parseDouble(asString(parts.get(2)));
        Double range = parts.size() > 3 && parts.get(3) != null ? Double.valueOf(asString(parts.get(3))) : null;
        return new RawVector(quantization, blob, norm, range);
    }

    private static String asString(Object value) {
        if (value instanceof byte[]) {
            return new String((byte[]) value, StandardCharsets.UTF_8);
        }
        return String.valueOf(value);
    }

    private static byte[] asBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        return String.valueOf(value).getBytes(StandardCharsets.ISO_8859_1);
    }
}
