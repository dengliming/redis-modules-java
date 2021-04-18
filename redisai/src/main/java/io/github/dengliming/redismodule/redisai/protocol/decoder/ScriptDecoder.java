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

import io.github.dengliming.redismodule.redisai.Device;
import io.github.dengliming.redismodule.redisai.model.Script;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.List;

public class ScriptDecoder implements MultiDecoder<Script> {

    @Override
    public Decoder<Object> getDecoder(int i, State state) {
        return null;
    }

    @Override
    public Script decode(List<Object> list, State state) {
        String source = null;
        Device device = null;
        String tag = null;
        for (int i = 0; i < list.size(); i += 2) {
            String key = String.valueOf(list.get(i));
            switch (key) {
                case "source":
                    source = String.valueOf(list.get(i + 1));
                    break;
                case "device":
                    device = Device.valueOf(String.valueOf(list.get(i + 1)));
                    break;
                case "tag":
                    tag = String.valueOf(list.get(i + 1));
                    break;
                default:
                    break;
            }
        }
        return new Script(device, tag, source);
    }
}
