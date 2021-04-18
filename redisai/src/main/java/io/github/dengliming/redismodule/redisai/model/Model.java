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

package io.github.dengliming.redismodule.redisai.model;

import io.github.dengliming.redismodule.redisai.Backend;
import io.github.dengliming.redismodule.redisai.Device;

import java.util.List;

public class Model {

    private final Backend backend;
    private final Device device;
    private final List<String> inputs;
    private final List<String> outputs;
    private final byte[] blob;
    private final String tag;
    private final long batchSize;
    private final long minBatchSize;

    public Model(Backend backend, Device device, List<String> inputs, List<String> outputs, byte[] blob,
                 String tag, long batchSize, long minBatchSize) {
        this.backend = backend;
        this.device = device;
        this.inputs = inputs;
        this.outputs = outputs;
        this.blob = blob;
        this.tag = tag;
        this.batchSize = batchSize;
        this.minBatchSize = minBatchSize;
    }

    public Backend getBackend() {
        return backend;
    }

    public Device getDevice() {
        return device;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public byte[] getBlob() {
        return blob;
    }

    public String getTag() {
        return tag;
    }

    public long getBatchSize() {
        return batchSize;
    }

    public long getMinBatchSize() {
        return minBatchSize;
    }
}
