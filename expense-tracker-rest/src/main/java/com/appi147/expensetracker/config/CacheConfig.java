package com.appi147.expensetracker.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofHours(1))
                .recordStats();

        CaffeineCacheManager manager = new CaffeineCacheManager("paymentTypes", "categories", "subCategories", "siteWideInsight");
        manager.setCaffeine(caffeine);
        return manager;
    }
}
