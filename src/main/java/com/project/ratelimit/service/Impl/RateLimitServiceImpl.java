package com.project.ratelimit.service.Impl;

import com.project.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW = 60000; // 1 minute in ms

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isRateLimitExceeded(String clientIp) {
        long currentTimeStamp = System.currentTimeMillis();
        long windowStartTimeStamp = currentTimeStamp - TIME_WINDOW;

        // 1. Remove timestamps older than window
        redisTemplate.opsForZSet().removeRangeByScore(clientIp, 0, windowStartTimeStamp);

        // 2. Count requests in current window
        Long countRequestCount = redisTemplate.opsForZSet().zCard(clientIp);

        if (countRequestCount != null && countRequestCount >= MAX_REQUESTS) {
            return true; // Rate limit exceeded
        }

        // 3. Add current request with UNIQUE member value
        String memberValue = currentTimeStamp + "-" + UUID.randomUUID();
        redisTemplate.opsForZSet().add(clientIp, memberValue, currentTimeStamp);

        // 4. Reset key TTL
        redisTemplate.expire(clientIp, Duration.ofSeconds(60));

        return false;
    }
}