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

package io.github.dengliming.redismodule.redisgears.model;

import java.util.List;

public class ClusterInfo {

    private final String myId;
    private final String myRunId;
    private final List<Shard> shards;

    public ClusterInfo(String myId, String myRunId, List<Shard> shards) {
        this.myId = myId;
        this.myRunId = myRunId;
        this.shards = shards;
    }

    public String getMyId() {
        return myId;
    }

    public String getMyRunId() {
        return myRunId;
    }

    public List<Shard> getShards() {
        return shards;
    }

    public static class Shard {
        private String id;
        private String ip;
        private int port;
        private String unixSocket;
        private String runId;
        private int minHslot;
        private int maxHslot;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUnixSocket() {
            return unixSocket;
        }

        public void setUnixSocket(String unixSocket) {
            this.unixSocket = unixSocket;
        }

        public String getRunId() {
            return runId;
        }

        public void setRunId(String runId) {
            this.runId = runId;
        }

        public int getMinHslot() {
            return minHslot;
        }

        public void setMinHslot(int minHslot) {
            this.minHslot = minHslot;
        }

        public int getMaxHslot() {
            return maxHslot;
        }

        public void setMaxHslot(int maxHslot) {
            this.maxHslot = maxHslot;
        }
    }
}
