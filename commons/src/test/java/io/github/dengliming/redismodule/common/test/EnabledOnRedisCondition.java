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

package io.github.dengliming.redismodule.common.test;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @EnabledOnRedisCondition} is used to signal that the annotated test class or test method is only <em>enabled</em>if the
 * specified redis condition is available.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ExtendWith(EnabledOnRedisConditionExtension.class)
public @interface EnabledOnRedisCondition {

    /**
     * Name of the Redis command to be available.
     *
     * @return
     */
    String command() default "";

    /**
     * Name of the Redis module to be available.
     *
     * @return
     */
    String module() default "";

    /**
     * Version of the Redis module to be available.
     *
     * @return
     */
    long[] moduleVersions() default {};
}
