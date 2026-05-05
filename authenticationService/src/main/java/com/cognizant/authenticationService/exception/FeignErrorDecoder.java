package com.cognizant.authenticationService.exception;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        String body = "";
        try {
            if (response.body() != null) {
                // Use the raw InputStream to ensure we get everything
                byte[] bodyBytes = response.body().asInputStream().readAllBytes();
                body = new String(bodyBytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            body = "{\"message\": \"Failed to read downstream body\"}";
        }

        // IMPORTANT: Log here to see if the Decoder itself is getting the data
        System.out.println("DEBUG DECODER: Status " + response.status() + " Body: " + body);

        return new CustomDownStreamException(response.status(), body);
    }
}