package com.project.ratelimit.filters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        log.info("Request received from IP: {}", clientIp);

        response.setStatus(429);
        response.getWriter().write("Rate limit exceeded. Please try again later.");
        return false;
    }

}
