package com.example.v4.global.exception;

import lombok.Getter;

/**
 * 로그인 시 아이디 또는 비밀번호가 올바르지 않을 때 발생하는 예외.
 */
@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final Object dto;

    public InvalidCredentialsException(String message) {
        this(message, null);
    }

    public InvalidCredentialsException(String message, Object dto) {
        super(message);
        this.dto = dto;
    }
}
