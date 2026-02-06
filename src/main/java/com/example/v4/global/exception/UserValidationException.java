package com.example.v4.global.exception;

import org.springframework.validation.BindingResult;

import lombok.Getter;

/**
 * User 폼(회원가입/로그인) 검증 실패 시 사용하는 예외.
 * BindingResult, 폼 데이터, 뷰 정보를 담아 GlobalExceptionHandler에서 처리한다.
 */
@Getter
public class UserValidationException extends RuntimeException {

    private final BindingResult bindingResult;
    private final Object dto;
    private final String viewName;

    public UserValidationException(BindingResult bindingResult, Object dto, String viewName) {
        super("검증 실패: " + bindingResult.getAllErrors());
        this.bindingResult = bindingResult;
        this.dto = dto;
        this.viewName = viewName;
    }
}
