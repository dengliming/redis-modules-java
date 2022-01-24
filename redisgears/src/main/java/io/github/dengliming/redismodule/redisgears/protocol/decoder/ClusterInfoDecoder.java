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

package io.github.dengliming.redismodule.redisgears.protocol.decoder;

import io.github.dengliming.redismodule.redisgears.model.ClusterInfo;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.List;
import java.util.stream.Collectors;

public class ClusterInfoDecoder implements MultiDecoder<ClusterInfo> {

    @Override
    public ClusterInfo decode(List<Object> parts, State state) {
        if (parts.size() < 5) {
            return null;
        }

        List<List<Object>> shards = (List<List<Object>>) parts.get(4);
        return new ClusterInfo((String) parts.get(1), (String) parts.get(3), shards.stream()
                .map(it -> buildShard(it))
                .collect(Collectors.toList()));
    }

    private ClusterInfo.Shard buildShard(List<Object> shardFields) {
        ClusterInfo.Shard shard = new ClusterInfo.Shard();
        for (int i = 0; i < shardFields.size(); i += 2) {
            Object v = shardFields.get(i + 1);
            switch ((String) shardFields.get(i)) {
                case "id":
                    shard.setId(v.toString());
                    break;
                case "ip":
                    shard.setIp(v.toString());
                    break;
                case "port":
                    shard.setPort(Integer.parseInt(v.toString()));
                    break;
                case "unixSocket":
                    shard.setUnixSocket(v.toString());
                    break;
                case "runid":
                    shard.setRunId(v.toString());
                    break;
                case "minHslot":
                    shard.setMinHslot(Integer.parseInt(v.toString()));
                    break;
                case "maxHslot":
                    shard.setMaxHslot(Integer.parseInt(v.toString()));
                    break;
                default:
                    break;
            }
        }
        return shard;
    }
}
