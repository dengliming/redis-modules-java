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

import io.github.dengliming.redismodule.redistimeseries.TimeSeries;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;
import static io.github.dengliming.redismodule.redistimeseries.Sample.Value;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 */
public class TimeSeriesDecoder implements MultiDecoder<List<TimeSeries>> {

    private LabelDecoder labelDecoder = new LabelDecoder();
    private ValueDecoder valueDecoder = new ValueDecoder();

    @Override
    public Decoder<Object> getDecoder(int paramNum, State state) {
        return null;
    }

    /**
     * [temperature:2:33, [[label1, test], [label2, test1]], [1588266627081, 13]]
     *
     * @param parts
     * @param state
     * @return
     */
    @Override
    public List<TimeSeries> decode(List<Object> parts, State state) {
        if (parts == null) {
            return null;
        }
        List<TimeSeries> timeSeries = new ArrayList<>(parts.size());
        for (Object part : parts) {
            List<Object> o = (List) part;
            TimeSeries series = new TimeSeries((String) o.get(0));
            series.labels(labelDecoder.decode((List<Object>) o.get(1), state));

            List<Value> values = new ArrayList<>();
            List<Object> objects = (List<Object>) o.get(2);
            // all samples
            if (objects.get(0) instanceof List) {
                ((List<List<Object>>) o.get(2)).forEach(valueObject -> values.add(valueDecoder.decode(valueObject, state)));
            } else {
                // the last sample
                values.add(valueDecoder.decode((List<Object>) o.get(2), state));
            }
            series.values(values);
            timeSeries.add(series);
        }

        return timeSeries;
    }
}
