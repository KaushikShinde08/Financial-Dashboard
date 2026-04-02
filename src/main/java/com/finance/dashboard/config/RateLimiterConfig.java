package com.finance.dashboard.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Provides per-IP token-bucket rate limiters for each API path group.
 *
 * Limits:
 *   /api/auth/login  → 5 requests / minute  (brute-force protection)
 *   /api/records/**  → 30 requests / minute
 *   /api/dashboard/**→ 60 requests / minute
 */
@Component
public class RateLimiterConfig {

    private final Map<String, Bucket> loginBuckets     = new ConcurrentHashMap<>();
    private final Map<String, Bucket> recordsBuckets   = new ConcurrentHashMap<>();
    private final Map<String, Bucket> dashboardBuckets = new ConcurrentHashMap<>();

    public Bucket resolveLoginBucket(String ip) {
        return loginBuckets.computeIfAbsent(ip, k -> buildBucket(5, Duration.ofMinutes(1)));
    }

    public Bucket resolveRecordsBucket(String ip) {
        return recordsBuckets.computeIfAbsent(ip, k -> buildBucket(30, Duration.ofMinutes(1)));
    }

    public Bucket resolveDashboardBucket(String ip) {
        return dashboardBuckets.computeIfAbsent(ip, k -> buildBucket(60, Duration.ofMinutes(1)));
    }

    private Bucket buildBucket(long capacity, Duration refillPeriod) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, refillPeriod)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
