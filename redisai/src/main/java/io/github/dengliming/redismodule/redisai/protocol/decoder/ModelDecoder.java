/*
 * Copyright 2021 dengliming.
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

package io.github.dengliming.redismodule.redisai.protocol.decoder;

import io.github.dengliming.redismodule.redisai.Backend;
import io.github.dengliming.redismodule.redisai.Device;
import io.github.dengliming.redismodule.redisai.model.Model;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.List;

public class ModelDecoder implements MultiDecoder<Model> {

    @Override
    public Decoder<Object> getDecoder(int i, State state) {
        return null;
    }

    @Override
    public Model decode(List<Object> list, State state) {
        Backend backend = null;
        Device device = null;
        String tag = null;
        byte[] blob = null;
        long batchSize = 0;
        long minBatchSize = 0;
        List<String> inputs = null;
        List<String> outputs = null;
        for (int i = 0; i < list.size(); i += 2) {
            String key = String.valueOf(list.get(i));
            switch (key) {
                case "backend":
                    backend = Backend.valueOf(String.valueOf(list.get(i + 1)));
                    break;
                case "device":
                    device = Device.valueOf(String.valueOf(list.get(i + 1)));
                    break;
                case "tag":
                    tag = String.valueOf(list.get(i + 1));
                    break;
                case "blob":
                    blob = String.valueOf(list.get(i + 1)).getBytes();
                    break;
                case "batchsize":
                    batchSize = (Long) list.get(i + 1);
                    break;
                case "minbatchsize":
                    minBatchSize = (Long) list.get(i + 1);
                    break;
                case "inputs":
                    inputs = (List<String>) list.get(i + 1);
                    break;
                case "outputs":
                    outputs = (List<String>) list.get(i + 1);
                    break;
                default:
                    break;
            }
        }
        return new Model(backend, device, inputs, outputs, blob, tag, batchSize, minBatchSize);
    }
}
