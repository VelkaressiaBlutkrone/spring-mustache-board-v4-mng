package com.example.v4.global.exception;

import org.springframework.http.HttpStatusCode;

public class RestClientErrorException extends RuntimeException {

    public RestClientErrorException(String message) {
        super(message);
    }

    public RestClientErrorException(HttpStatusCode statusCode) {
        super("Client error: " + statusCode);
    }
}
