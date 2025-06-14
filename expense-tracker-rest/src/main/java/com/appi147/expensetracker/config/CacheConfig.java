package com.appi147.expensetracker.config;

import com.appi147.expensetracker.model.TimedLoginResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, TimedLoginResponse> userLoginCache() {
        return Caffeine.newBuilder()
                .expireAfter(new Expiry<String, TimedLoginResponse>() {
                    @Override
                    public long expireAfterCreate(String key, TimedLoginResponse value, long currentTime) {
                        long now = Instant.now().getEpochSecond();
                        long durationSec = value.expiryEpochSeconds() - now;
                        return TimeUnit.SECONDS.toNanos(Math.max(durationSec, 1)); // prevent 0 or negative
                    }

                    @Override
                    public long expireAfterUpdate(String key, TimedLoginResponse value, long currentTime, long currentDuration) {
                        return expireAfterCreate(key, value, currentTime);
                    }

                    @Override
                    public long expireAfterRead(String key, TimedLoginResponse value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .maximumSize(100)
                .build();
    }
}
