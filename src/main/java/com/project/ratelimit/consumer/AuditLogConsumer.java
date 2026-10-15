package com.project.ratelimit.consumer;

import com.project.ratelimit.config.RabbitMQConfig;
import com.project.ratelimit.model.AuditLogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditLogConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeAuditLogMessage(AuditLogMessage message) {
        log.info("📥 [AUDIT LOG CONSUMED] IP: {} | Status: {} | Method: {} | Endpoint: {} | Time: {}",
                message.getClientId(),
                message.getStatus(),
                message.getHttpMethod(),
                message.getEndpoint(),
                message.getTimestamp());
        // Here you can implement the logic to process the audit log message,
        // such as saving it to a database or performing further analysis.
    }

}
