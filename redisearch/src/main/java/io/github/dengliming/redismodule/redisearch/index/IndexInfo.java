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

package io.github.dengliming.redismodule.redisearch.index;

import io.github.dengliming.redismodule.redisearch.index.schema.Schema;

/**
 * @author dengliming
 */
public class IndexInfo {
    private Schema schema;
    private IndexOptions options;
    private String indexName;
    private long docNum;
    private long termNum;
    private long recordNum;
    private double invertedIndexSizeMb;
    private double invertedCapMb;
    private double invertedCapOvh;
    private double offsetVectorsSzMb;
    private double skipIndexSizeMb;
    private double scoreIndexSizeMb;
    private double recordsPerDocAvg;
    private double bytesPerRecordAvg;
    private double offsetsPerTermAvg;
    private double offsetBitsPerRecordAvg;


}
