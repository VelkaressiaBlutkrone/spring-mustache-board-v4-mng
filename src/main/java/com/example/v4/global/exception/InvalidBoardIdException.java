package com.example.v4.global.exception;

public class InvalidBoardIdException extends RuntimeException {

    public InvalidBoardIdException(String message) {
        super(message);
    }
}
