package com.example.v4.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 검증 실패 시 리다이렉트할 뷰 정보를 지정하는 어노테이션.
 * <p>ValidationHandler AOP에서 BindingResult.hasErrors() 시 해당 정보로 ValidationException을 던진다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateOnError {

    /**
     * 검증 실패 시 표시할 뷰 이름 (예: "board/save-form", "user/join-form")
     */
    String viewName();

    /**
     * 경로 변수 파라미터 이름 (예: "boardId").
     * 게시글 수정 폼처럼 경로 변수가 필요한 경우 해당 메서드 파라미터 이름을 지정한다.
     * 비어 있으면 null로 처리된다.
     */
    String pathVariable() default "";
}
