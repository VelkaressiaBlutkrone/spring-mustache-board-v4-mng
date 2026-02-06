package com.example.v4.global.exception;

public class BoardNotFoundException extends RuntimeException {

    public BoardNotFoundException(String message) {
        super(message);
    }
}
