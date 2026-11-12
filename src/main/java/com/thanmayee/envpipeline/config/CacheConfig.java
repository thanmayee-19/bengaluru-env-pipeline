package com.thanmayee.envpipeline.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thanmayee.envpipeline.cache.LRUCache;
import com.thanmayee.envpipeline.entity.Reading;

@Configuration
public class CacheConfig {

    @Bean
    public LRUCache<String, List<Reading>> latestReadingCache() {
        return new LRUCache<>(100);
    }
}
