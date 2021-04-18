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

import io.github.dengliming.redismodule.redisai.Device;

public class Script {

    /**
     * the device that will execute the model. can be of CPU or GPU
     */
    private final Device device;
    /**
     * tag is an optional string for tagging the model such as a version number or any arbitrary
     * identifier
     */
    private final String tag;
    /**
     * a string containing TorchScript source code
     */
    private final String source;

    public Script(Device device, String tag, String source) {
        this.device = device;
        this.tag = tag;
        this.source = source;
    }

    public Device getDevice() {
        return device;
    }

    public String getTag() {
        return tag;
    }

    public String getSource() {
        return source;
    }
}
