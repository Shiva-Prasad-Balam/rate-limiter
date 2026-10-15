package com.project.ratelimit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogMessage {

    private String clientId;
    private String endpoint;
    private String httpMethod;
    private long timestamp;
    private String status;
    private String payload;

}
