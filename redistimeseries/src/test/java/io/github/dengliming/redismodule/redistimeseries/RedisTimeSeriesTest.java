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

package io.github.dengliming.redismodule.redistimeseries;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.redisson.client.RedisException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static io.github.dengliming.redismodule.redistimeseries.Sample.Value;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dengliming
 */
public class RedisTimeSeriesTest extends AbstractTest {

    @Test
    public void testCreate() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        assertThat(redisTimeSeries.create("Series-1", new TimeSeriesOptions()
                .retentionTime(0L)
                .unCompressed()
                .duplicatePolicy(DuplicatePolicy.MAX)
                .labels(new Label("a", "1")))).isTrue();

        assertThat(redisTimeSeries.alter("Series-1", new TimeSeriesOptions()
                .retentionTime(0L)
                .labels(new Label("b", "1")))).isTrue();
    }

    @Test
    public void testAdd() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertThat(redisTimeSeries.add(new Sample("temperature:2:32", Value.of(timestamp, 26)), new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .duplicatePolicy(DuplicatePolicy.MAX)
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue()).isEqualTo(timestamp);

        List<Long> result = redisTimeSeries.add(new Sample("temperature:2:32", Value.of(timestamp + 1, 26)),
                new Sample("temperature:2:32", Value.of(timestamp + 2, 45)));
        assertThat(result).isNotNull();
        assertThat(result.get(0).longValue()).isEqualTo(timestamp + 1);
        assertThat(result.get(1).longValue()).isEqualTo(timestamp + 2);
    }

    @Test
    public void testAddOnDuplicate() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertThat(redisTimeSeries.add(new Sample("temperature:2:32", Value.of(timestamp, 26)), new TimeSeriesOptions()
                .retentionTime(6000L)).longValue()).isEqualTo(timestamp);

        assertThat(redisTimeSeries.add(new Sample("temperature:2:32", Value.of(timestamp, 32)), new TimeSeriesOptions()
                .retentionTime(6000L)
                .duplicatePolicy(DuplicatePolicy.MIN)).longValue()).isEqualTo(timestamp);
        List<Value> values = redisTimeSeries.range("temperature:2:32", 0, timestamp);
        assertThat(values).hasSize(1);
        assertThat((int) values.get(0).getValue()).isEqualTo(26);
    }

    @Test
    public void testIncrDecrBy() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertThat(redisTimeSeries.add(new Sample("temperature:2:32", Value.of(timestamp, 26)), new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue()).isEqualTo(timestamp);

        assertThat(redisTimeSeries.incrBy("temperature:2:33", 13, timestamp, new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue()).isEqualTo(timestamp);

        assertThat(redisTimeSeries.incrBy("temperature:2:33", 13, timestamp + 1).longValue()).isEqualTo(timestamp + 1);

        assertThat(redisTimeSeries.decrBy("temperature:2:33", 13, timestamp + 3).longValue()).isEqualTo(timestamp + 3);
    }

    @Test
    public void testRule() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        assertThat(redisTimeSeries.create("Series-1", new TimeSeriesOptions()
                .retentionTime(0L)
                .unCompressed()
                .labels(new Label("a", "1")))).isTrue();

        assertThat(redisTimeSeries.create("Series-2", new TimeSeriesOptions()
                .retentionTime(0L)
                .unCompressed()
                .labels(new Label("a", "1")))).isTrue();

        assertThat(redisTimeSeries.createRule("Series-1", "Series-2", Aggregation.COUNT, 100)).isTrue();

        try {
            assertThat(redisTimeSeries.createRule("Series-1", "Series-2", Aggregation.COUNT, 100)).isFalse();
            Assert.fail();
        } catch (RedisException e) {
        }

        assertThat(redisTimeSeries.deleteRule("Series-1", "Series-2")).isTrue();
    }

    @Test
    public void testRange() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertThat(redisTimeSeries.incrBy("temperature:2:33", 13, timestamp, new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue()).isEqualTo(timestamp);

        assertThat(redisTimeSeries.incrBy("temperature:2:33", 13, timestamp + 1).longValue()).isEqualTo(timestamp + 1);

        assertThat(redisTimeSeries.decrBy("temperature:2:33", 13, timestamp + 3).longValue()).isEqualTo(timestamp + 3);

        List<Value> values = redisTimeSeries.range("temperature:2:33", timestamp, timestamp + 1);
        assertThat(values).hasSize(2);

        List<TimeSeries> timeSeries = redisTimeSeries.mrange(timestamp, timestamp + 1, new RangeOptions()
                .max(3).withLabels(), "sensor_id=2");
        assertThat(timeSeries).hasSize(1);
        assertThat(timeSeries.get(0).getKey()).isEqualTo("temperature:2:33");

        // Sensor exists, but no data in range
        timeSeries = redisTimeSeries.mrange(0, 100, new RangeOptions()
                .max(3).withLabels(), "sensor_id=2");
        assertThat(timeSeries).hasSize(1);
        assertThat(timeSeries.get(0).getValues()).isEmpty();

        timeSeries = redisTimeSeries.mrange(timestamp, timestamp + 1, new RangeOptions()
                .max(3).withLabels(), "sensor_id=1");
        assertThat(timeSeries).isEmpty();
    }

    @Test
    public void testAggregations() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        String sensor = "temperature:sum";

        assertThat(redisTimeSeries.incrBy(sensor, 10, timestamp, new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()).longValue()).isEqualTo(timestamp);

        assertThat(redisTimeSeries.incrBy(sensor, 20, timestamp + 1).longValue()).isEqualTo(timestamp + 1);

        List<Value> values = redisTimeSeries.range(sensor, timestamp, timestamp + 1);
        assertThat(values).hasSize(2);

        List<Value> sum = redisTimeSeries.range(sensor, timestamp, timestamp + 10, new RangeOptions()
                .aggregationType(Aggregation.SUM, 60000L));

        assertThat(sum).hasSize(1);
        // Timestamp trimmed to timeBucket (minutes)
        assertThat(sum.get(0).getTimestamp()).isEqualTo(Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.MINUTES).toEpochMilli());
        assertThat(sum.get(0).getValue()).isEqualTo(40.0d);
    }

    @Ignore("Only for redis timeseries > 1.6.0")
    public void testAggregationsAlign() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long from = 1L;
        long to = 10000L;
        long timeBucket = 3000L;

        String sensor = "temperature:sum:align";
        TimeSeriesOptions options = new TimeSeriesOptions().unCompressed();

        /*
           TS:  1000 | 2000 | 3000 | 4000
           VAL: 1    | 1    | 10   | 10
           BT : -------------------|-----
         */

        assertThat(redisTimeSeries.add(new Sample(sensor, Value.of(1000L, 1.0d)), options).longValue()).isEqualTo(1000L);
        assertThat(redisTimeSeries.add(new Sample(sensor, Value.of(2000L, 1.0d)), options).longValue()).isEqualTo(2000L);
        assertThat(redisTimeSeries.add(new Sample(sensor, Value.of(3000L, 10.0d)), options).longValue()).isEqualTo(3000L);
        assertThat(redisTimeSeries.add(new Sample(sensor, Value.of(4000L, 10.0d)), options).longValue()).isEqualTo(4000L);

        List<Value> values = redisTimeSeries.range(sensor, from, to);
        assertThat(values).hasSize(4);

        List<Value> start = redisTimeSeries.range(sensor, from, to, new RangeOptions()
                .aggregationType(Aggregation.SUM, timeBucket, Align.START));

        assertThat(start).hasSize(2);
        assertThat(start.get(0).getTimestamp()).isEqualTo(from);
        assertThat(start.get(0).getValue()).isEqualTo(12.0d);

        assertThat(start.get(1).getTimestamp()).isEqualTo(from + timeBucket);
        assertThat(start.get(1).getValue()).isEqualTo(10.0d);

        List<Value> end = redisTimeSeries.range(sensor, from, to, new RangeOptions()
                .aggregationType(Aggregation.SUM, timeBucket, Align.END));

        assertThat(end).hasSize(2);

        assertThat(end.get(0).getTimestamp()).isEqualTo(1000L);
        assertThat(end.get(0).getValue()).isEqualTo(12.0d);

        assertThat(end.get(1).getTimestamp()).isEqualTo(4000L);
        assertThat(end.get(1).getValue()).isEqualTo(10.0d);
    }

    @Test
    public void testQueryIndex() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertThat(redisTimeSeries.incrBy("temperature:2:33", 13, timestamp, new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue()).isEqualTo(timestamp);

        List<String> keys = redisTimeSeries.queryIndex("sensor_id=2");
        assertThat(keys.get(0)).isEqualTo("temperature:2:33");
    }

    @Test
    public void testGet() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertThat(redisTimeSeries.incrBy("temperature:2:33", 13, timestamp,
                new TimeSeriesOptions().labels(new Label("label1", "test"), new Label("label2", "test1"))).longValue()).isEqualTo(timestamp);
        assertThat(redisTimeSeries.incrBy("temperature:3:33", 14, timestamp,
                new TimeSeriesOptions().labels(new Label("label1", "test"))).longValue()).isEqualTo(timestamp);
        assertThat(redisTimeSeries.incrBy("temperature:2:34", 14, timestamp + 2).longValue()).isEqualTo(timestamp + 2);

        Value value = null;
        try {
            value = redisTimeSeries.get("temperature:2:3");
            Assert.fail();
        } catch (RedisException ignore) {
            //  TSDB: the key does not exist
        }
        value = redisTimeSeries.get("temperature:2:33");
        assertThat(value.getTimestamp()).isEqualTo(timestamp);

        List<TimeSeries> timeSeries = redisTimeSeries.mget(true, "label1=test");
        assertThat(timeSeries).hasSize(2);

        timeSeries = redisTimeSeries.mget(true, "label2=test1");
        assertThat(timeSeries).hasSize(1);
        assertThat(timeSeries.get(0).getKey()).isEqualTo("temperature:2:33");
    }

    @Test
    public void testInfo() {
        RedisTimeSeries redisTimeSeries = getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertThat(redisTimeSeries.incrBy("temperature:2:33", 13, timestamp,
                new TimeSeriesOptions().labels(new Label("label1", "test"), new Label("label2", "test1"))).longValue()).isEqualTo(timestamp);
        assertThat(redisTimeSeries.create("Series-2", new TimeSeriesOptions()
                .retentionTime(0L)
                .unCompressed()
                .labels(new Label("a", "1")))).isTrue();
        assertThat(redisTimeSeries.createRule("temperature:2:33", "Series-2", Aggregation.COUNT, 100)).isTrue();
        Map<String, Object> map = redisTimeSeries.info("temperature:2:33");
        assertThat((long) map.get("totalSamples")).isEqualTo(1);
        assertThat((List<List<Object>>) map.get("labels")).hasSize(2);
        assertThat(((List<List<Object>>) map.get("labels")).get(0)).contains("label1");
    }
}
