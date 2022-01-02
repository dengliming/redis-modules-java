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

package io.github.dengliming.redismodule.examples.redisgears;

import io.github.dengliming.redismodule.redisgears.RedisGears;
import io.github.dengliming.redismodule.redisgears.client.RedisGearsClient;
import org.redisson.config.Config;

public class Examples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedisGearsClient redisGearsClient = new RedisGearsClient(config);

        RedisGears redisGears = redisGearsClient.getRedisGears();
        redisGears.pyExecute("GB().run()", false);
        redisGearsClient.shutdown();
    }
}
