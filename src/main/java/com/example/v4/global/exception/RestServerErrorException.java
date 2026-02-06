package com.example.v4.global.exception;

public class RestServerErrorException extends RuntimeException {

    public RestServerErrorException(String message) {
        super(message);
    }
}
