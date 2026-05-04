package com.cognizant.authenticationService.exception;

import lombok.Getter;

@Getter
public class CustomDownStreamException extends RuntimeException {
    private final int status;
    private final String cleanJson;

    public CustomDownStreamException(int status, String cleanJson) {
        super(cleanJson);
        this.status = status;
        this.cleanJson = cleanJson;
    }
}
