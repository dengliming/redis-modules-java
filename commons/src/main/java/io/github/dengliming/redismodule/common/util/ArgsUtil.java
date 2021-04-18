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

package io.github.dengliming.redismodule.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author dengliming
 */
public final class ArgsUtil {

    public static Object[] append(Object key, Object... items) {
        RAssert.notNull(key, "Key must not be null");
        RAssert.notEmpty(items, "Items must not be empty");

        Object[] args = new Object[items.length + 1];
        args[0] = key;
        System.arraycopy(items, 0, args, 1, items.length);
        return args;
    }

    public static Object[] append(Object key, Map<?, ?> params) {
        RAssert.notNull(key, "Key must not be null");
        RAssert.notEmpty(params, "Params must not be empty");

        List<Object> args = new ArrayList<>(params.size() * 2 + 1);
        args.add(key);
        params.forEach((k, v) -> {
            args.add(k);
            args.add(v);
        });
        return args.toArray();
    }
}
