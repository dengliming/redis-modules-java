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

package io.github.dengliming.redismodule.redisai.args;

import io.github.dengliming.redismodule.redisai.Device;
import io.github.dengliming.redismodule.redisai.protocol.Keywords;

import java.util.List;

public class StoreScriptArgs {

    private String tag;

    private Device device;

    private List<String> entryPoints;

    private String script;

    public Device getDevice() {
        return device;
    }

    public List<String> getEntryPoints() {
        return entryPoints;
    }

    public String getScript() {
        return script;
    }

    public String getTag() {
        return tag;
    }

    public StoreScriptArgs device(Device device) {
        this.device = device;
        return this;
    }

    public StoreScriptArgs tag(String tag) {
        this.tag = tag;
        return this;
    }

    public StoreScriptArgs script(String script) {
        this.script = script;
        return this;
    }

    public StoreScriptArgs entryPoints(List<String> entryPoints) {
        this.entryPoints = entryPoints;
        return this;
    }

    public void build(List<Object> args) {
        if (device != null) {
            args.add(device);
        }
        if (tag != null) {
            args.add(Keywords.TAG);
            args.add(tag);
        }

        if (entryPoints != null && !entryPoints.isEmpty()) {
            args.add(Keywords.ENTRY_POINTS);
            args.add(entryPoints.size());
            for (String entryPoint : entryPoints) {
                args.add(entryPoint);
            }
        }

        if (script != null) {
            args.add(Keywords.SOURCE);
            args.add(script);
        }
    }
}
