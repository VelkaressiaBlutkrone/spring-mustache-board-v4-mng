package com.example.v4.global.exception;

public class UserDuplicationException extends RuntimeException {

    public UserDuplicationException(String message) {
        super(message);
    }
}
