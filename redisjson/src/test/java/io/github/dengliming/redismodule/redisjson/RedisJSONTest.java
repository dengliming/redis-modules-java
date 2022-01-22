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

package io.github.dengliming.redismodule.redisjson;

import io.github.dengliming.redismodule.redisjson.args.GetArgs;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class RedisJSONTest extends AbstractTest {

    @Test
    public void testSet() {
        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", "\"bar\""))).isEqualTo("OK");
        assertThat(redisJSON.set(key, SetArgs.Builder.nx(".", "\"bar\""))).isNull();
        assertThat(redisJSON.set(key, SetArgs.Builder.xx(".", "\"bar\""))).isEqualTo("OK");
        assertThat(redisJSON.del(key, ".")).isEqualTo(1);
    }

    @Test
    public void testGet() {
        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("name", "lisi");
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)))).isEqualTo("OK");

        Map<String, Object> actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        assertThat(actual).isNotNull().containsEntry("name", "lisi");

        assertThat(redisJSON.getType(key, ".")).isEqualTo(Object.class);

        assertThat(redisJSON.mget(".", Map.class, key, "null")).hasSize(2);
    }

    @Test
    public void testIncrBy() {
        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("name", "lisi");
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)))).isEqualTo("OK");

        assertThat(redisJSON.incrBy(key, ".id", 1)).isEqualTo("2");
        Map<String, Object> actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        assertThat(actual).isNotNull().containsEntry("id", 2.0);
    }

    @Test
    public void testIncrMultBy() {
        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("name", "lisi");
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)))).isEqualTo("OK");

        assertThat(redisJSON.incrBy(key, ".id", 1)).isEqualTo("2");
        Map<String, Object> actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        assertThat(actual).isNotNull().containsEntry("id", 2.0);

        assertThat(redisJSON.multBy(key, ".id", 2)).isEqualTo("4");
        actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        assertThat(actual).isNotNull().containsEntry("id", 4.0);
    }

    @Test
    public void testStr() {
        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("name", "lisi");
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)))).isEqualTo("OK");

        assertThat(redisJSON.strAppend(key, ".name", "123")).isEqualTo(7);
        Map<String, Object> actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        assertThat(actual).isNotNull().containsEntry("name", "lisi123");

        assertThat(redisJSON.strLen(key, ".name")).isEqualTo("lisi123".length());
    }

    @Test
    public void testArr() {
        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("names", new ArrayList<>());
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)))).isEqualTo("OK");

        assertThat(redisJSON.arrAppend(key, ".names", "zhansan")).isEqualTo(1);
        assertThat(redisJSON.arrAppend(key, ".names", "lisi")).isEqualTo(2);

        Map<String, Object> actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        assertThat(actual).isNotNull();
        List<String> actualList = (List<String>) actual.get("names");
        assertThat(actualList).containsExactly("zhansan", "lisi");

        assertThat(redisJSON.arrIndex(key, ".names", "lisi", 0, 2)).isEqualTo(1);

        assertThat(redisJSON.arrLen(key, ".names")).isEqualTo(2);

        assertThat(redisJSON.arrTrim(key, ".names", 1, 1)).isEqualTo(1);

        actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        actualList = (List<String>) actual.get("names");
        assertThat(actualList).containsExactly("lisi");

        assertThat(redisJSON.arrInsert(key, ".names", 0, "wangwu")).isEqualTo(2);
        actual = redisJSON.get(key, Map.class, new GetArgs().path(".").indent("\t").newLine("\n").space(" "));
        actualList = (List<String>) actual.get("names");
        assertThat(actualList).containsExactly("wangwu", "lisi");

        assertThat(redisJSON.arrLen(key, ".names")).isEqualTo(2);
        assertThat(redisJSON.arrPop(key, ".names", String.class, 0)).isEqualTo("wangwu");
        assertThat(redisJSON.arrLen(key, ".names")).isEqualTo(1);
    }

    @Test
    public void testObjLen() {
        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("names", new ArrayList<>());
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)))).isEqualTo("OK");

        assertThat(redisJSON.objLen(key, ".")).isEqualTo(2);
        assertThat(redisJSON.objLen("not exist", ".")).isEqualTo(0);
    }

    @Test
    public void testObjKeys() {
        Map<String, Object> m = new HashMap<>();
        m.put("a", Arrays.asList(3));
        Map<String, Object> nestedMap = new HashMap<>();
        Map<String, Object> a = new HashMap<>();
        a.put("b", 2);
        a.put("c", 1);
        nestedMap.put("a", a);
        m.put("nested", nestedMap);

        RedisJSON redisJSON = getRedisJSON();
        String key = "foo";
        assertThat(redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)))).isEqualTo("OK");

        assertThat(redisJSON.objKeys(key, ".")).containsExactly("a", "nested");
        assertThat(redisJSON.objKeys(key, "$..a")).containsExactly(null, Arrays.asList("b", "c"));
    }

    @Test
    public void testPipelining() {
        String key = "foo";
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("names", new ArrayList<>());

        RedisJSONBatch batch = getRedisJSONBatch();
        RedisJSON redisJSON = batch.getRedisJSON();
        redisJSON.setAsync(key, SetArgs.Builder.create(".", GsonUtils.toJson(m)));
        redisJSON.objLenAsync(key, ".");
        redisJSON.objLenAsync("not exist", ".");
        BatchResult res = batch.execute();
        assertThat(res.getResponses().size()).isEqualTo(3);
        assertThat(res.getResponses().get(0)).isEqualTo("OK");
        assertThat(res.getResponses().get(1)).isEqualTo(2L);
        assertThat(res.getResponses().get(2)).isEqualTo(0L);
    }
}
