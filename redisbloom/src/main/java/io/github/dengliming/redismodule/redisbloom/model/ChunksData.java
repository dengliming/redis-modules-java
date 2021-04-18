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

package io.github.dengliming.redismodule.redisbloom.model;

/**
 * @author dengliming
 */
public class ChunksData {
    private int iter;
    private byte[] data;

    public ChunksData(int iter, byte[] data) {
        this.iter = iter;
        this.data = data;
    }

    public int getIter() {
        return iter;
    }

    public byte[] getData() {
        return data;
    }
}
