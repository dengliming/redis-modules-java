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

package io.github.dengliming.redismodule.redisai;

import io.github.dengliming.redismodule.redisai.args.SetModelArgs;
import io.github.dengliming.redismodule.redisai.model.Model;
import io.github.dengliming.redismodule.redisai.model.Script;
import io.github.dengliming.redismodule.redisai.model.Tensor;
import org.junit.jupiter.api.Test;
import org.redisson.client.RedisException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author dengliming
 */
public class RedisAITest extends AbstractTest {

    @Test
    public void testTensor() {
        RedisAI redisAI = getRedisAI();
        // setTensor
        assertThat(redisAI.setTensor("tensor1", DataType.FLOAT, new int[]{2, 2}, null, new String[]{"1", "2", "3", "4"})).isTrue();

        // getTensor
        Tensor tensor = redisAI.getTensor("tensor1");
        assertThat(tensor).isNotNull();
        assertThat(tensor.getDataType()).isEqualTo(DataType.FLOAT);
        assertThat(tensor.getShape()).containsExactly(2, 2);
        assertThat(tensor.getValues()).isNotNull();
    }

    @Test
    public void testModel() throws Exception {
        RedisAI redisAI = getRedisAI();
        // Set Model
        byte[] blob = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("test_data/graph.pb").toURI()));
        assertThat(redisAI.setModel("model1", new SetModelArgs().backEnd(Backend.TF).device(Device.CPU)
                .inputs(Arrays.asList("a", "b")).outputs(Arrays.asList("mul")).blob(blob))).isTrue();

        // Get Model
        Model model = redisAI.getModel("model1");
        assertThat(model.getBackend()).isEqualTo(Backend.TF);
        assertThat(model.getDevice()).isEqualTo(Device.CPU);
        assertThat(model.getInputs()).containsExactly("a", "b");
        assertThat(model.getOutputs()).containsExactly("mul");
        assertThat(model.getBlob()).isEqualTo(blob);
    }

    @Test
    public void testScript() {
        RedisAI redisAI = getRedisAI();
        String key = "script1";
        String script = "def bar(a, b):\n" + "    return a + b\n";
        // Set Script
        assertThat(redisAI.setScript(key, Device.CPU, script)).isTrue();

        // Get Script
        Script scriptInfo = redisAI.getScript(key);
        assertThat(scriptInfo).isNotNull();
        assertThat(scriptInfo.getDevice()).isEqualTo(Device.CPU);
        assertThat(scriptInfo.getSource()).isEqualTo(script);
    }

    @Test
    public void testConfig() {
        RedisAI redisAI = getRedisAI();
        assertThat(redisAI.setBackendPath("/usr/lib/redis/modules/backends/")).isTrue();
        assertThrows(RedisException.class, () -> redisAI.loadBackend(Backend.TF, "notexist/redisai_tensorflow.so"));

        try {
            boolean r = redisAI.loadBackend(Backend.TF, "redisai_tensorflow/redisai_tensorflow.so");
            assertThat(r).isTrue();
        } catch (RedisException e) {
            // will throw error if backend already loaded
        }
    }

    @Test
    public void testInfo() {
        RedisAI redisAI = getRedisAI();
        String key = "tensor:info";
        String script = "def bar(a, b):\n" + "    return a + b\n";
        assertThat(redisAI.setScript(key, Device.CPU, script)).isTrue();
        // not exist
        Map<String, Object> infoMap;
        assertThrows(RedisException.class, () -> {
            // ERR cannot find run info for key
            redisAI.getInfo("not:exist");
        });

        // first inited info
        infoMap = redisAI.getInfo(key);
        assertThat(infoMap).isNotNull();
        assertThat(infoMap.get("key")).isEqualTo(key);
        assertThat(infoMap.get("device")).isEqualTo(Device.CPU.name());
        assertThat(infoMap.get("calls")).isEqualTo(0L);

        redisAI.setTensor("a1", DataType.FLOAT, new int[]{2}, null, new String[]{"2", "3"});
        redisAI.setTensor("b1", DataType.FLOAT, new int[]{2}, null, new String[]{"2", "3"});
        assertThat(redisAI.runScript(key, "bar", new String[]{"a1", "b1"}, new String[]{"c1"})).isTrue();

        // one model runs
        infoMap = redisAI.getInfo(key);
        assertThat(infoMap.get("calls")).isEqualTo(1L);

        // reset
        assertThat(redisAI.resetStat(key)).isTrue();
        infoMap = redisAI.getInfo(key);
        assertThat(infoMap.get("calls")).isEqualTo(0L);

        assertThrows(RedisException.class, () -> {
            // ERR cannot find run info for key
            redisAI.resetStat("not:exist");
        });
    }
}
