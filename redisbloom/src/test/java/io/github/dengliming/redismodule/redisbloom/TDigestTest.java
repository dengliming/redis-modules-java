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

package io.github.dengliming.redismodule.redisbloom;

import io.github.dengliming.redismodule.redisbloom.model.TDigestInfo;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author dengliming
 */
public class TDigestTest extends AbstractTest {

    @Test
    public void testCreate() {
        TDigest tDigest = getRedisBloomClient().getTDigest("t-digest");
        assertThat(tDigest.create(100)).isTrue();
        TDigestInfo tDigestInfo = tDigest.getInfo();
        assertThat(tDigestInfo).isNotNull();
        assertThat(tDigestInfo.getCompression()).isEqualTo(100);
        tDigest.delete();

        // compression negative/zero value
        assertThatThrownBy(() -> tDigest.create(0)).hasMessageContaining("compression parameter needs to be a positive integer");
        assertThatThrownBy(() -> tDigest.create(-1)).hasMessageContaining("compression parameter needs to be a positive integer");
    }

    @Test
    public void testAdd() {
        TDigest tDigest = getRedisBloomClient().getTDigest("t-digest");
        assertThat(tDigest.create(100)).isTrue();
        List<AbstractMap.SimpleEntry<Double, Double>> vals = new ArrayList<>();
        vals.add(new AbstractMap.SimpleEntry(1500.0, 1.0));
        assertThat(tDigest.add(vals)).isTrue();
    }

    @Test
    public void testMinAndMax() {
        TDigest tDigest = getRedisBloomClient().getTDigest("t-digest");
        assertThat(tDigest.create(100)).isTrue();
        List<AbstractMap.SimpleEntry<Double, Double>> vals = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            vals.add(new AbstractMap.SimpleEntry(i, 1.0));
        }
        assertThat(tDigest.add(vals)).isTrue();

        assertThat(tDigest.getMin()).isEqualTo(1.0d);
        assertThat(tDigest.getMax()).isEqualTo(100.0d);
    }

    @Test
    public void testQuantile() {
        TDigest tDigest = getRedisBloomClient().getTDigest("t-digest");
        assertThat(tDigest.create(500)).isTrue();

        List<AbstractMap.SimpleEntry<Double, Double>> vals = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            vals.add(new AbstractMap.SimpleEntry(i * 0.01, 1.0));
        }

        assertThat(tDigest.add(vals)).isTrue();

        assertThat(tDigest.getQuantile(0.01)).isCloseTo(1.0, Offset.offset(0.01));
        assertThat(tDigest.getQuantile(0.99)).isCloseTo(99.0, Offset.offset(0.01));
    }

    @Test
    public void testCdf() {
        TDigest tDigest = getRedisBloomClient().getTDigest("t-digest");
        assertThat(tDigest.create(500)).isTrue();

        List<AbstractMap.SimpleEntry<Double, Double>> vals = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            vals.add(new AbstractMap.SimpleEntry(i, 1.0));
        }

        assertThat(tDigest.add(vals)).isTrue();

        assertThat(tDigest.getCdf(1.0)).isCloseTo(0.01, Offset.offset(0.01));
        assertThat(tDigest.getCdf(99.0)).isCloseTo(0.99, Offset.offset(0.01));
    }

    @Test
    public void testReset() {
        TDigest tDigest = getRedisBloomClient().getTDigest("t-digest");
        assertThat(tDigest.create(100)).isTrue();

        List<AbstractMap.SimpleEntry<Double, Double>> vals = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            vals.add(new AbstractMap.SimpleEntry(i, 1.0));
        }

        assertThat(tDigest.add(vals)).isTrue();

        TDigestInfo tDigestInfo = tDigest.getInfo();
        assertThat(tDigestInfo).isNotNull();
        assertThat(tDigestInfo.getUnmergedNodes()).isEqualTo(100);

        assertThat(tDigest.reset()).isTrue();
        tDigestInfo = tDigest.getInfo();
        assertThat(tDigestInfo.getUnmergedNodes()).isEqualTo(0);
    }

    @Test
    public void testMerge() {
        TDigest fromDigest = getRedisBloomClient().getTDigest("t-from-digest");
        TDigest toDigest = getRedisBloomClient().getTDigest("t-to-digest");
        assertThat(fromDigest.create(100)).isTrue();
        assertThat(toDigest.create(100)).isTrue();

        List<AbstractMap.SimpleEntry<Double, Double>> vals = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            vals.add(new AbstractMap.SimpleEntry(1.0, 1.0));
        }

        assertThat(fromDigest.add(vals)).isTrue();

        vals.clear();
        for (int i = 1; i <= 100; i++) {
            vals.add(new AbstractMap.SimpleEntry(1.0, 10.0));
        }
        assertThat(toDigest.add(vals)).isTrue();

        assertThat(fromDigest.mergeTo("t-to-digest")).isTrue();

        TDigestInfo toDigestInfo = toDigest.getInfo();
        assertThat(toDigestInfo).isNotNull();
        assertThat(toDigestInfo.getMergedWeight() + toDigestInfo.getUnmergedWeight()).isEqualTo(1100);
    }
}
