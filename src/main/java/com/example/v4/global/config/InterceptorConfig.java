package com.example.v4.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.v4.global.interceptor.LoginInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * HandlerInterceptor 등록을 담당하는 설정.
 * <p>
 * 등록된 인터셉터:
 * <ul>
 * <li>{@link LoginInterceptor}: 로그인 필요 경로에 대한 인증 체크</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login-form", "/login", "/join-form", "/join", "/board/detail/**",
                        "/api/**");
    }
}
