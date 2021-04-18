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
public class TopKFilterInfo {
    private Integer topK;
    private Integer width;
    private Integer depth;
    private Double decay;

    public TopKFilterInfo(Integer topK, Integer width, Integer depth, Double decay) {
        this.topK = topK;
        this.width = width;
        this.depth = depth;
        this.decay = decay;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Double getDecay() {
        return decay;
    }

    public void setDecay(Double decay) {
        this.decay = decay;
    }
}
