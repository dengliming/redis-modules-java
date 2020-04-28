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
import org.junit.Test;
import org.redisson.client.RedisException;

import java.util.List;

import static org.junit.Assert.*;


/**
 * @author dengliming
 */
public class RedisTimeSeriesTest extends AbstractTest {

    @Test
    public void testCreate() {
        RedisTimeSeries redisTimeSeries = redisTimeSeriesClient.getRedisTimeSeries();
        assertTrue(redisTimeSeries.create("Series-1", new TimeSeriesOptions()
                .retentionTime(0L)
                .unCompressed()
                .labels(new Label("a", "1"))));

        assertTrue(redisTimeSeries.alter("Series-1", new TimeSeriesOptions()
                .retentionTime(0L)
                .labels(new Label("b", "1"))));
    }

    @Test
    public void testAdd() {
        RedisTimeSeries redisTimeSeries = redisTimeSeriesClient.getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertEquals(timestamp, redisTimeSeries.add(new Sample("temperature:2:32", timestamp, 26), new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue());


        List<Long> result = redisTimeSeries.add(new Sample("temperature:2:32", timestamp + 1, 26),
                new Sample("temperature:2:32", timestamp + 2, 45));
        assertNotNull(result);
        assertEquals(timestamp + 1, result.get(0).longValue());
        assertEquals(timestamp + 2, result.get(1).longValue());
    }

    @Test
    public void testIncrDecrBy() {
        RedisTimeSeries redisTimeSeries = redisTimeSeriesClient.getRedisTimeSeries();
        long timestamp = System.currentTimeMillis();
        assertEquals(timestamp, redisTimeSeries.add(new Sample("temperature:2:32", timestamp, 26), new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue());


        assertEquals(timestamp, redisTimeSeries.incrBy("temperature:2:33", 13, timestamp, new TimeSeriesOptions()
                .retentionTime(6000L)
                .unCompressed()
                .labels(new Label("sensor_id", "2"), new Label("area_id", "32"))).longValue());

        assertEquals(timestamp + 1, redisTimeSeries.incrBy("temperature:2:33", 13, timestamp + 1).longValue());

        assertEquals(timestamp + 3, redisTimeSeries.decrBy("temperature:2:33", 13, timestamp + 3).longValue());
    }

    @Test
    public void testRule() {
        RedisTimeSeries redisTimeSeries = redisTimeSeriesClient.getRedisTimeSeries();
        assertTrue(redisTimeSeries.create("Series-1", new TimeSeriesOptions()
                .retentionTime(0L)
                .unCompressed()
                .labels(new Label("a", "1"))));

        assertTrue(redisTimeSeries.create("Series-2", new TimeSeriesOptions()
                .retentionTime(0L)
                .unCompressed()
                .labels(new Label("a", "1"))));

        assertTrue(redisTimeSeries.createRule("Series-1", "Series-2", Aggregation.COUNT, 100));

        try {
            assertFalse(redisTimeSeries.createRule("Series-1", "Series-2", Aggregation.COUNT, 100));
            Assert.fail();
        } catch(RedisException e) {
        }

        assertTrue(redisTimeSeries.deleteRule("Series-1", "Series-2"));
    }
}
