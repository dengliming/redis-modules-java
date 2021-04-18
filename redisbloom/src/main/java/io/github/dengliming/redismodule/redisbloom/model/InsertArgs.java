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

import io.github.dengliming.redismodule.redisbloom.protocol.Keywords;

import java.util.List;

/**
 * @author dengliming
 */
public class InsertArgs {
    /**
     * The number of entries you intend to add to the filter
     */
    private int capacity;
    /**
     * If specified, should be followed by the the error ratio of the newly created filter if it does not yet exist
     */
    private double errorRatio;
    /**
     * If a new sub-filter is created, its size will be the size of the current filter multiplied by expansion . Default expansion value is 2
     */
    private int expansion;
    /**
     * If specified, indicates that the filter should not be created if it does not already exist
     */
    private boolean noCreate;
    /**
     * Prevents the filter from creating additional sub-filters if initial capacity is reached
     */
    private boolean nonScaling;

    public InsertArgs() {
    }

    public InsertArgs(int capacity, double errorRatio, int expansion) {
        this.capacity = capacity;
        this.errorRatio = errorRatio;
        this.expansion = expansion;
    }

    public InsertArgs(int capacity, double errorRatio, int expansion, boolean noCreate, boolean nonScaling) {
        this.capacity = capacity;
        this.errorRatio = errorRatio;
        this.expansion = expansion;
        this.noCreate = noCreate;
        this.nonScaling = nonScaling;
    }

    public int getCapacity() {
        return capacity;
    }

    public InsertArgs setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public double getErrorRatio() {
        return errorRatio;
    }

    public InsertArgs setErrorRatio(double errorRatio) {
        this.errorRatio = errorRatio;
        return this;
    }

    public int getExpansion() {
        return expansion;
    }

    public InsertArgs setExpansion(int expansion) {
        this.expansion = expansion;
        return this;
    }

    public boolean getNoCreate() {
        return noCreate;
    }

    public InsertArgs setNoCreate(boolean noCreate) {
        this.noCreate = noCreate;
        return this;
    }

    public boolean getNonScaling() {
        return nonScaling;
    }

    public InsertArgs setNonScaling(boolean nonScaling) {
        this.nonScaling = nonScaling;
        return this;
    }

    public void build(List<Object> args) {
        if (getCapacity() > 0) {
            args.add(Keywords.CAPACITY);
            args.add(getCapacity());
        }
        if (getErrorRatio() > 0) {
            args.add(Keywords.ERROR);
            args.add(getErrorRatio());
        }
        if (getExpansion() > 0) {
            args.add(Keywords.EXPANSION);
            args.add(getExpansion());
        }
        if (getNoCreate()) {
            args.add(Keywords.NOCREATE);
        }
        if (getNonScaling()) {
            args.add(Keywords.NONSCALING);
        }
    }
}
