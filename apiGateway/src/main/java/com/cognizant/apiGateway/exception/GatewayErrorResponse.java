package com.cognizant.apiGateway.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
public class GatewayErrorResponse {
    String timestamp;
    int status;
    String error;
    String message;
    String path;

    // Explicit constructor to ensure Lombok generates it correctly
    public GatewayErrorResponse(String timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
