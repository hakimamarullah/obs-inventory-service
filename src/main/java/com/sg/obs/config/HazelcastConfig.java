package com.sg.obs.config;


import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    public static final String ITEM_CACHE = "itemCache";
    public static final String ORDER_CACHE = "orderCache";
    public static final String PAGED_ITEM_CACHE = "pagedItemCache";
    public static final String PAGED_ORDER_CACHE = "pagedOrderCache";

    @Bean
    public HazelcastInstance hazelcastInstance() {
        HazelcastInstance existing = Hazelcast.getHazelcastInstanceByName("embedded-hazelcast");
        if (existing != null) {
            return existing;
        }
        Config config = new Config();
        config.setInstanceName("embedded-hazelcast");

        // Configure for embedded single-node setup
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(5701).setPortAutoIncrement(true);

        // Disable clustering for embedded mode
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(false);
        joinConfig.getAutoDetectionConfig().setEnabled(false);

        // Add cache configurations with TTL
        addCacheConfigurations(config);

        config.getJetConfig().setEnabled(true);

        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

    private void addCacheConfigurations(Config config) {
        // Item cache - 10 minutes TTL
        MapConfig itemCache = new MapConfig();
        itemCache
                .setName(ITEM_CACHE)
                .setTimeToLiveSeconds(600)
                .setMaxIdleSeconds(180)
                .getEvictionConfig()
                .setEvictionPolicy(com.hazelcast.config.EvictionPolicy.LFU)
                .setMaxSizePolicy(com.hazelcast.config.MaxSizePolicy.FREE_HEAP_SIZE)
                .setSize(500);

        MapConfig pagedCache = new MapConfig();
        pagedCache
                .setName(PAGED_ITEM_CACHE)
                .setTimeToLiveSeconds(600)
                .setMaxIdleSeconds(180)
                .getEvictionConfig()
                .setEvictionPolicy(com.hazelcast.config.EvictionPolicy.LFU)
                .setMaxSizePolicy(com.hazelcast.config.MaxSizePolicy.FREE_HEAP_SIZE)
                .setSize(500);

        MapConfig pagedOrderCache = new MapConfig();
        pagedOrderCache
                .setName(PAGED_ORDER_CACHE)
                .setTimeToLiveSeconds(600)
                .setMaxIdleSeconds(180)
                .getEvictionConfig()
                .setEvictionPolicy(com.hazelcast.config.EvictionPolicy.LFU)
                .setMaxSizePolicy(com.hazelcast.config.MaxSizePolicy.FREE_HEAP_SIZE)
                .setSize(500);

        config.addMapConfig(itemCache);
        config.addMapConfig(pagedCache);
        config.addMapConfig(pagedOrderCache);

        // Order cache - 10 minutes TTL
        MapConfig orderCache = new MapConfig();
        orderCache
                .setName(ORDER_CACHE)
                .setTimeToLiveSeconds(60)
                .setMaxIdleSeconds(300)
                .getEvictionConfig()
                .setEvictionPolicy(com.hazelcast.config.EvictionPolicy.LRU)
                .setMaxSizePolicy(com.hazelcast.config.MaxSizePolicy.FREE_HEAP_SIZE)
                .setSize(500);
        config.addMapConfig(orderCache);
    }
}
