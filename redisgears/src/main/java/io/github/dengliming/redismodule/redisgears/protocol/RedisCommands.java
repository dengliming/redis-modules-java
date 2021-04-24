/*
 * Copyright 2021 dengliming.
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

package io.github.dengliming.redismodule.redisgears.protocol;

import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.decoder.ObjectListReplayDecoder;

/**
 * @author dengliming
 */
public interface RedisCommands {

    RedisCommand RG_PYEXECUTE = new RedisCommand<>("RG.PYEXECUTE", new ObjectListReplayDecoder<>());
}
