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

package io.github.dengliming.redismodule.examples.redisjson;

import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.RedisJSONBatch;
import io.github.dengliming.redismodule.redisjson.args.GetArgs;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.client.RedisJSONClient;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import org.redisson.api.BatchResult;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PipeliningExamples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedisJSONClient redisJSONClient = new RedisJSONClient(config);

        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("names", new ArrayList<>());
        RedisJSONBatch batch = redisJSONClient.createRedisJSONBatch();
        RedisJSON redisJSON = batch.getRedisJSON();
        redisJSON.setAsync(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)));
        redisJSON.objLenAsync(key, ".");
        redisJSON.objLenAsync("not exist", ".");
        BatchResult res = batch.execute();
        // 3
        System.out.println(res.getResponses().size());
        // "OK"
        System.out.println(res.getResponses().get(0));
        // 2L
        System.out.println(res.getResponses().get(1));
        // 0L
        System.out.println(res.getResponses().get(2));
        redisJSONClient.shutdown();
    }
}
