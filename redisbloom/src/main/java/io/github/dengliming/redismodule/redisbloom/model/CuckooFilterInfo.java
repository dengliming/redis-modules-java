/*
 * Copyright 2020-2022 dengliming.
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
public class CuckooFilterInfo {
    private final Integer size;
    private final Integer bucketNum;
    private final Integer filterNum;
    private final Integer insertedNum;
    private final Integer deletedNum;
    private final Integer bucketSize;
    private final Integer expansionRate;
    private final Integer maxIteration;

    public CuckooFilterInfo(Integer size, Integer bucketNum, Integer filterNum, Integer insertedNum, Integer deletedNum,
                            Integer bucketSize, Integer expansionRate, Integer maxIteration) {
        this.size = size;
        this.bucketNum = bucketNum;
        this.filterNum = filterNum;
        this.insertedNum = insertedNum;
        this.deletedNum = deletedNum;
        this.bucketSize = bucketSize;
        this.expansionRate = expansionRate;
        this.maxIteration = maxIteration;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getBucketNum() {
        return bucketNum;
    }

    public Integer getFilterNum() {
        return filterNum;
    }

    public Integer getInsertedNum() {
        return insertedNum;
    }

    public Integer getDeletedNum() {
        return deletedNum;
    }

    public Integer getBucketSize() {
        return bucketSize;
    }

    public Integer getExpansionRate() {
        return expansionRate;
    }

    public Integer getMaxIteration() {
        return maxIteration;
    }
}
