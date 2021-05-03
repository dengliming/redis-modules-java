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

package io.github.dengliming.redismodule.redisbloom.model;

/**
 * @author dengliming
 */
public class TDigestInfo {

    private final long compression;
    private final long capacity;
    private final long mergedNodes;
    private final long unmergedNodes;
    private final double mergedWeight;
    private final double unmergedWeight;
    private final long totalCompressions;

    public TDigestInfo(long compression, long capacity, long mergedNodes, long unmergedNodes,
                       double mergedWeight, double unmergedWeight, long totalCompressions) {
        this.compression = compression;
        this.capacity = capacity;
        this.mergedNodes = mergedNodes;
        this.unmergedNodes = unmergedNodes;
        this.mergedWeight = mergedWeight;
        this.unmergedWeight = unmergedWeight;
        this.totalCompressions = totalCompressions;
    }

    public long getCompression() {
        return compression;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getMergedNodes() {
        return mergedNodes;
    }

    public long getUnmergedNodes() {
        return unmergedNodes;
    }

    public double getMergedWeight() {
        return mergedWeight;
    }

    public double getUnmergedWeight() {
        return unmergedWeight;
    }

    public long getTotalCompressions() {
        return totalCompressions;
    }
}
