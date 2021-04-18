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

import io.github.dengliming.redismodule.redisai.DataType;

public class Tensor {

    private final DataType dataType;
    private final long[] shape;
    private final Object values;

    public Tensor(DataType dataType, long[] shape, Object values) {
        this.dataType = dataType;
        this.shape = shape;
        this.values = values;
    }

    public DataType getDataType() {
        return dataType;
    }

    public long[] getShape() {
        return shape;
    }

    public Object getValues() {
        return values;
    }
}
