package com.appi147.expensetracker.service;

import com.appi147.expensetracker.model.response.SiteWideInsight;
import com.appi147.expensetracker.repository.InsightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsightService {

    private final InsightRepository insightRepository;

    @Cacheable(cacheNames = "siteWideInsight")
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    public SiteWideInsight getInsights() {
        return insightRepository.getSiteWideInsight();
    }
}
