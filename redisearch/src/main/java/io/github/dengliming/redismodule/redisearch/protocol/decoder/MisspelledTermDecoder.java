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

package io.github.dengliming.redismodule.redisearch.protocol.decoder;

import io.github.dengliming.redismodule.redisearch.search.MisspelledSuggestion;
import io.github.dengliming.redismodule.redisearch.search.MisspelledTerm;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 */
public class MisspelledTermDecoder implements MultiDecoder<MisspelledTerm> {

    @Override
    public Decoder<Object> getDecoder(int paramNum, State state) {
        return null;
    }

    @Override
    public MisspelledTerm decode(List<Object> parts, State state) {
        if (parts == null || parts.size() < 3) {
            return null;
        }

        MisspelledTerm misspelledTerm = new MisspelledTerm((String) parts.get(1));
        List<List<Object>> list = (List<List<Object>>) (Object) parts.get(2);
        List<MisspelledSuggestion> misspelledSuggestions = new ArrayList<>(parts.size());
        for (List<Object> entry : list) {
            misspelledSuggestions.add(new MisspelledSuggestion(Double.parseDouble((String) entry.get(0)), (String) entry.get(1)));
        }
        misspelledTerm.misspelledSuggestions(misspelledSuggestions);
        return misspelledTerm;
    }
}
