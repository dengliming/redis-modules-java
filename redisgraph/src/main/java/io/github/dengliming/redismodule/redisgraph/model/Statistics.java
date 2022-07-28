/*
 * Copyright 2022 dengliming.
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

package io.github.dengliming.redismodule.redisgraph.model;

public class Statistics {
    private int nodesCreated;
    private int nodesDeleted;
    private int indicesCreated;
    private int indicesDeleted;
    private int labelsAdded;
    private int propertiesSet;
    private int relationshipsCreated;
    private int relationshipsDeleted;
    private boolean cachedExecution;
    private String queryIntervalExecutionTime;

    public int getNodesCreated() {
        return nodesCreated;
    }

    public int getNodesDeleted() {
        return nodesDeleted;
    }

    public int getIndicesCreated() {
        return indicesCreated;
    }

    public int getIndicesDeleted() {
        return indicesDeleted;
    }

    public int getLabelsAdded() {
        return labelsAdded;
    }

    public int getPropertiesSet() {
        return propertiesSet;
    }

    public int getRelationshipsCreated() {
        return relationshipsCreated;
    }

    public int getRelationshipsDeleted() {
        return relationshipsDeleted;
    }

    public boolean isCachedExecution() {
        return cachedExecution;
    }

    public String getQueryIntervalExecutionTime() {
        return queryIntervalExecutionTime;
    }

    public void setNodesCreated(int nodesCreated) {
        this.nodesCreated = nodesCreated;
    }

    public void setNodesDeleted(int nodesDeleted) {
        this.nodesDeleted = nodesDeleted;
    }

    public void setIndicesCreated(int indicesCreated) {
        this.indicesCreated = indicesCreated;
    }

    public void setIndicesDeleted(int indicesDeleted) {
        this.indicesDeleted = indicesDeleted;
    }

    public void setLabelsAdded(int labelsAdded) {
        this.labelsAdded = labelsAdded;
    }

    public void setPropertiesSet(int propertiesSet) {
        this.propertiesSet = propertiesSet;
    }

    public void setRelationshipsCreated(int relationshipsCreated) {
        this.relationshipsCreated = relationshipsCreated;
    }

    public void setRelationshipsDeleted(int relationshipsDeleted) {
        this.relationshipsDeleted = relationshipsDeleted;
    }

    public void setCachedExecution(boolean cachedExecution) {
        this.cachedExecution = cachedExecution;
    }

    public void setQueryIntervalExecutionTime(String queryIntervalExecutionTime) {
        this.queryIntervalExecutionTime = queryIntervalExecutionTime;
    }
}
