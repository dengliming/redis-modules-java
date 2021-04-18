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

package io.github.dengliming.redismodule.redisai.args;

import io.github.dengliming.redismodule.redisai.Backend;
import io.github.dengliming.redismodule.redisai.Device;
import io.github.dengliming.redismodule.redisai.protocol.Keywords;

import java.util.List;

public class SetModelArgs {

    /**
     * the backend for the model can be one of:
     * TF : a TensorFlow backend
     * TFLITE : The TensorFlow Lite backend
     * TORCH : a PyTorch backend
     * ONNX : a ONNX backend
     */
    private Backend backEnd;
    /**
     * the device that will execute the model can be of:
     * CPU : a CPU device
     * GPU : a GPU device
     * GPU:0 , ..., GPU:n : a specific GPU device on a multi-GPU system
     */
    private Device device;
    /**
     * the Protobuf-serialized model. Since Redis supports strings up to 512MB,
     * blobs for very large models need to be chunked, e.g. BLOB chunk1 chunk2 ...
     */
    private byte[] blob;
    /**
     * one or more names of the model's input nodes (applicable only for TensorFlow models)
     */
    private List<String> inputs;
    /**
     * one or more names of the model's output nodes (applicable only for TensorFlow models)
     */
    private List<String> outputs;
    private long batchSize;
    private long minBatchSize;
    /**
     * an optional string for tagging the model such as a version number or any arbitrary identifier
     */
    private String tag;

    public Backend getBackEnd() {
        return backEnd;
    }

    public SetModelArgs backEnd(Backend backEnd) {
        this.backEnd = backEnd;
        return this;
    }

    public Device getDevice() {
        return device;
    }

    public SetModelArgs device(Device device) {
        this.device = device;
        return this;
    }

    public byte[] getBlob() {
        return blob;
    }

    public SetModelArgs blob(byte[] blob) {
        this.blob = blob;
        return this;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public SetModelArgs inputs(List<String> inputs) {
        this.inputs = inputs;
        return this;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public SetModelArgs outputs(List<String> outputs) {
        this.outputs = outputs;
        return this;
    }

    public long getBatchSize() {
        return batchSize;
    }

    public SetModelArgs batchSize(long batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public long getMinBatchSize() {
        return minBatchSize;
    }

    public SetModelArgs minBatchSize(long minBatchSize) {
        this.minBatchSize = minBatchSize;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public SetModelArgs tag(String tag) {
        this.tag = tag;
        return this;
    }

    public void build(List<Object> args) {
        if (backEnd != null) {
            args.add(backEnd);
        }
        if (device != null) {
            args.add(device);
        }
        if (tag != null) {
            args.add(Keywords.TAG);
            args.add(tag);
        }
        if (batchSize > 0) {
            args.add(Keywords.BATCHSIZE);
            args.add(batchSize);
        }
        if (minBatchSize > 0) {
            args.add(Keywords.MINBATCHSIZE);
            args.add(minBatchSize);
        }
        if (inputs != null) {
            args.add(Keywords.INPUTS);
            for (String input : inputs) {
                args.add(input);
            }
        }
        if (outputs != null) {
            args.add(Keywords.OUTPUTS);
            for (String output : outputs) {
                args.add(output);
            }
        }
        args.add(Keywords.BLOB);
        args.add(blob);
    }
}
