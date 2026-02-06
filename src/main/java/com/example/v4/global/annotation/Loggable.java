package com.example.v4.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AOP 로깅을 적용할 메서드/클래스를 표시하는 어노테이션.
 *
 * <p>
 * {@link com.example.v4.global.aop.LoggingAspect}에서 사용.
 * 어노테이션 적용 시 해당 메서드만 선택적으로 로깅 가능.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
}
