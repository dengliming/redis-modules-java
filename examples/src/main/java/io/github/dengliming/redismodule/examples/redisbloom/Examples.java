package io.github.dengliming.redismodule.examples.redisbloom;

import io.github.dengliming.redismodule.redisbloom.BloomFilter;
import io.github.dengliming.redismodule.redisbloom.CountMinSketch;
import io.github.dengliming.redismodule.redisbloom.CuckooFilter;
import io.github.dengliming.redismodule.redisbloom.TopKFilter;
import io.github.dengliming.redismodule.redisbloom.client.RedisBloomClient;
import io.github.dengliming.redismodule.redisbloom.model.CountMinSketchInfo;
import org.redisson.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Examples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedisBloomClient redisBloomClient = new RedisBloomClient(config);

        BloomFilter bloomFilter = redisBloomClient.getRBloomFilter("bf");
        bloomFilter.create(0.1d, 100);
        bloomFilter.madd(new String[] {"a", "b", "c"});


        TopKFilter topKFilter = redisBloomClient.getTopKFilter("topk_add");
        topKFilter.reserve(1, 2000, 7, 0.925d);
        topKFilter.add("test");
        Map<String, Integer> itemIncrement = new HashMap<>();
        itemIncrement.put("test", 3);
        topKFilter.incrby(itemIncrement);
        List<String> allItems = topKFilter.list();

        CountMinSketch countMinSketch = redisBloomClient.getCountMinSketch("cms_add");
        countMinSketch.create(10, 10);
        CountMinSketchInfo countMinSketchInfo = countMinSketch.getInfo();

        CuckooFilter cuckooFilter = redisBloomClient.getCuckooFilter("cf_insert");
        List<Boolean> result = cuckooFilter.insert(-1L, false, "a");

        redisBloomClient.shutdown();
    }
}
