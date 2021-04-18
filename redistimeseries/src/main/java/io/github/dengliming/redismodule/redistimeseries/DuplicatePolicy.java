/*
 * Copyright 2020-2021 dengliming.
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
 * DUPLICATE POLICY
 *
 * @author dengliming
 */
public enum DuplicatePolicy {
    /**
     * an error will occur for any out of order sample
     */
    BLOCK,
    /**
     * ignore the new value
     */
    FIRST,
    /**
     * override with latest value
     */
    LAST,
    /**
     * only override if the value is lower than the existing value
     */
    MIN,
    /**
     * only override if the value is higher than the existing value
     */
    MAX,
    /**
     * If a previous sample exists, add the new sample to it so that the updated value is equal to (previous + new).
     * If no previous sample exists, set the updated value equal to the new value.
     */
    SUM;
}
