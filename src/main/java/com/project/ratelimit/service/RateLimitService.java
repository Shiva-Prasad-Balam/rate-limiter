package com.project.ratelimit.service;


public interface RateLimitService {

    boolean isRateLimitExceeded(String clientIp);

}
