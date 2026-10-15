package com.project.ratelimit.filters;

import com.project.ratelimit.config.RabbitMQConfig;
import com.project.ratelimit.model.AuditLogMessage;
import com.project.ratelimit.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = extractClientIp(request);
        String endpoint = request.getRequestURI();
        String httpMethod = request.getMethod();
        String queryString = request.getQueryString();
        log.info("Request received from IP: {}, Endpoint: {}, Method: {}, Query String: {}", clientIp, endpoint, httpMethod, queryString);

        boolean isExceeded  = rateLimitService.isRateLimitExceeded(clientIp);
        String status = isExceeded ? "BLOCKED" : "ALLOWED";

        // Create an audit log message
        AuditLogMessage auditLogMessage = AuditLogMessage.builder()
                .clientId(clientIp)
                .endpoint(endpoint)
                .httpMethod(httpMethod)
                .timestamp(System.currentTimeMillis())
                .status(status)
                .payload(queryString != null ? queryString : "")
                .build();

        // Publish asynchronously to RabbitMQ exchange
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY, auditLogMessage);

        if (isExceeded) {
            response.setStatus(429);
            response.setContentType("text/plain;charset=UTF-8");

            PrintWriter writer = response.getWriter();
            writer.write("Rate limit exceeded. Please try again later.");
            writer.flush();
            return false;
        }

        return true;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}