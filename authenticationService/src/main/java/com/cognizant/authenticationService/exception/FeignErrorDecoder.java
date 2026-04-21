package com.cognizant.authenticationService.exception;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        // This 'body' is just the clean JSON from the User Service
        String body = "";
        try {
            if (response.body() != null) {
                body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            return new Exception("Failed to read body");
        }

        // Return a custom exception that your GlobalExceptionHandler can catch
        return new CustomDownStreamException(response.status(), body);
    }
}
