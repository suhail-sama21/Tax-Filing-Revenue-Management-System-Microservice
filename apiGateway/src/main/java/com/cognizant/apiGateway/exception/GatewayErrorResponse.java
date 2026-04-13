package com.cognizant.apiGateway.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GatewayErrorResponse {
    String timestamp;
    int status;
    String error;
    String message;
    String path;
}
