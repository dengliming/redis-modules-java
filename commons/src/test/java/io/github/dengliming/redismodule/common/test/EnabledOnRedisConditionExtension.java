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

import io.github.dengliming.redismodule.common.util.TestSettings;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.StringUtils;
import org.redisson.config.Config;

import java.util.Optional;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

public class EnabledOnRedisConditionExtension implements ExecutionCondition {

    private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled("@EnabledOnRedisCondition is not present");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Optional<EnabledOnRedisCondition> optional = AnnotationUtils.findAnnotation(context.getElement(),
                EnabledOnRedisCondition.class);

        if (!optional.isPresent()) {
            return ENABLED_BY_DEFAULT;
        }

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + TestSettings.host() + ":" + TestSettings.port());
        RedisConditionClient c = new RedisConditionClient(config);

        RedisConditions conditions = c.getRedisConditions("RedisConditions");
        c.shutdown();

        EnabledOnRedisCondition condition = optional.get();
        String command = condition.command();
        String module = condition.module();
        long[] moduleVersions = condition.moduleVersions();
        if (StringUtils.isNotBlank(command)) {
            boolean hasCommand = conditions.hasCommand(command);
            if (!hasCommand) {
                return disabled("Disabled, command " + command);
            }
        }

        if (StringUtils.isNotBlank(module) && moduleVersions.length > 0) {
            boolean includeModuleVersion = conditions.includeModuleVersion(module, moduleVersions);
            if (!includeModuleVersion) {
                return disabled(String.format("Disabled, module: %s", module));
            }
        }
        return ENABLED_BY_DEFAULT;
    }
}
