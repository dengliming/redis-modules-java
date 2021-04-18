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

/**
 * @author dengliming
 */
public class Sample {

    private final String key;
    private final Value value;

    public Sample(String key, Value value) {
        this.key = key;
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public static class Value {

        private final long timestamp;
        private final double value;

        public Value(long timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public double getValue() {
            return value;
        }

        public static Value of(long timestamp, double value) {
            return new Value(timestamp, value);
        }
    }
}
