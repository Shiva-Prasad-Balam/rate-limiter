package com.project.ratelimit.service.Impl;

import com.project.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private static final int MAX_REQUESTS = 5; // Maximum allowed requests
    private static final long TIME_WINDOW = 60000; // Time window in milliseconds (1 minute)

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isRateLimitExceeded(String clientIp) {
        // Implement your rate limiting logic here

        // Will store for each ip the times
        // Then for the max hit count it'll be the size of the List of time
        // eviction will be if the time difference in > allowed time window

        long windowStartTimeStamp = System.currentTimeMillis() - TIME_WINDOW; // 1 minute window

        redisTemplate.opsForZSet().removeRangeByScore(clientIp, 0, windowStartTimeStamp);

        Long countRequestCount = redisTemplate.opsForZSet().zCard(clientIp);

        if (countRequestCount != null && countRequestCount >= MAX_REQUESTS) {
            return true; // Rate limit exceeded
        }

        long currentTimeStamp = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(clientIp, String.valueOf(currentTimeStamp), currentTimeStamp);

        redisTemplate.expire(clientIp, Duration.ofSeconds(60));

        return false;
    }

}
