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

package io.github.dengliming.redismodule.redistimeseries;

import java.util.List;
import static io.github.dengliming.redismodule.redistimeseries.Sample.Value;

/**
 * @author dengliming
 */
public class TimeSeries {

    private String key;
    private List<Label> labels;
    private List<Value> values;

    public TimeSeries(String key) {
        this.key = key;
    }

    public TimeSeries labels(List<Label> labels) {
        this.labels = labels;
        return this;
    }

    public TimeSeries values(List<Value> values) {
        this.values = values;
        return this;
    }

    public String getKey() {
        return key;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public List<Value> getValues() {
        return values;
    }
}
