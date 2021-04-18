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

import io.github.dengliming.redismodule.redistimeseries.protocol.Keywords;

import java.util.List;

/**
 * @author dengliming
 */
public class TimeSeriesOptions {

    private long retentionTime;
    private boolean unCompressed;
    private Label[] labels;
    private DuplicatePolicy duplicatePolicy;
    private boolean isAdd;

    public TimeSeriesOptions retentionTime(long retentionTime) {
        this.retentionTime = retentionTime;
        return this;
    }

    public TimeSeriesOptions unCompressed() {
        this.unCompressed = true;
        return this;
    }

    public TimeSeriesOptions labels(Label... labels) {
        this.labels = labels;
        return this;
    }

    public TimeSeriesOptions duplicatePolicy(DuplicatePolicy duplicatePolicy) {
        this.duplicatePolicy = duplicatePolicy;
        return this;
    }

    public TimeSeriesOptions isAdd(boolean add) {
        this.isAdd = add;
        return this;
    }

    public void build(List<Object> args) {
        if (retentionTime > 0) {
            args.add(Keywords.RETENTION);
            args.add(retentionTime);
        }

        if (unCompressed) {
            args.add(Keywords.UNCOMPRESSED);
        }

        if (duplicatePolicy != null) {
            args.add(isAdd ? Keywords.ON_DUPLICATE : Keywords.DUPLICATE_POLICY);
            args.add(duplicatePolicy.name());
        }

        if (labels != null) {
            args.add(Keywords.LABELS);
            for (Label label : labels) {
                args.add(label.getKey());
                args.add(label.getValue());
            }
        }
    }
}
