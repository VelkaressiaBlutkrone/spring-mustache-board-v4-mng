package com.example.v4.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * Spring MVC 설정.
 *
 * <p>
 * 등록된 구성:
 * <ul>
 * <li>{@link SessionUserArgumentResolver}:
 * {@link com.example.v4.global.annotation.LoginUser} 어노테이션이
 * 붙은 SessionUser 파라미터에 세션의 로그인 사용자 정보를 자동 주입</li>
 * <li>{@link LoginUserInterceptor}: 모든 뷰에서 isLogin, user, setCookie 사용 가능하도록
 * 설정</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final SessionUserArgumentResolver sessionUserArgumentResolver;
    private final LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(sessionUserArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor)
                .addPathPatterns("/**");
    }
}
