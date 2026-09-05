/*
 * Copyright 2024 dengliming.
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

package io.github.dengliming.redismodule.vectorset.model;

/**
 * One element returned by VSIM or VLINKS.
 */
public class Similarity {
    private final String element;
    private final Double score;
    private final String attributes;

    public Similarity(String element, Double score, String attributes) {
        this.element = element;
        this.score = score;
        this.attributes = attributes;
    }

    public String getElement() {
        return element;
    }

    /**
     * @return similarity from 1 (identical) to 0 (opposite), or null when scores were not requested
     */
    public Double getScore() {
        return score;
    }

    /**
     * @return the element's JSON attributes, or null when not requested or absent
     */
    public String getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "Similarity{element='" + element + "', score=" + score + ", attributes=" + attributes + '}';
    }
}
