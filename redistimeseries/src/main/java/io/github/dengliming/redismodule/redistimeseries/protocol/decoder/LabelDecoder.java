/*
 * Copyright 2020 dengliming.
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

package io.github.dengliming.redismodule.redistimeseries.protocol.decoder;

import io.github.dengliming.redismodule.redistimeseries.Label;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 */
public class LabelDecoder implements MultiDecoder<List<Label>> {
    @Override
    public Decoder<Object> getDecoder(int paramNum, State state) {
        return null;
    }

    /**
     * [[label1, test], [label2, test1]]
     *
     * @param parts
     * @param state
     * @return
     */
    @Override
    public List<Label> decode(List<Object> parts, State state) {
        List<List<Object>> list = (List<List<Object>>) (Object) parts;
        List<Label> labels = new ArrayList<>(list.size());
        Map<Object, Object> result = new LinkedHashMap<Object, Object>(parts.size() / 2);
        for (List<Object> entry : list) {
            result.put(entry.get(0), entry.get(1));
            labels.add(new Label((String) entry.get(0), (String) entry.get(1)));
        }
        return labels;
    }
}
