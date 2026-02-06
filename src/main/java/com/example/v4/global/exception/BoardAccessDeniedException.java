package com.example.v4.global.exception;

public class BoardAccessDeniedException extends RuntimeException {

    public BoardAccessDeniedException(String message) {
        super(message);
    }
}
