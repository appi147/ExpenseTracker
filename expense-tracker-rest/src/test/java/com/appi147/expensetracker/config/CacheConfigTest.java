package com.appi147.expensetracker.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CacheConfig.class)
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCacheManagerContainsExpectedCaches() {
        assertThat(cacheManager.getCacheNames())
                .containsExactlyInAnyOrder("paymentTypes", "categories", "subCategories", "siteWideInsight");
    }

    @Test
    void testCacheManagerTypeAndConfig() {
        assertThat(cacheManager).isInstanceOf(CaffeineCacheManager.class);
        CaffeineCacheManager manager = (CaffeineCacheManager) cacheManager;
        assertThat(manager.getCache("categories")).isNotNull();
    }
}

