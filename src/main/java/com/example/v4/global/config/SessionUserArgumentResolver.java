package com.example.v4.global.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.v4.global.annotation.LoginUser;
import com.example.v4.global.dto.SessionUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * {@link LoginUser} 어노테이션이 붙은 메서드 파라미터에 SessionUser를 주입하는 ArgumentResolver.
 *
 * <p>
 * 기능:
 * <ul>
 * <li>@LoginUser가 붙고 SessionUser 타입인 파라미터만 처리</li>
 * <li>HttpSession에서 "sessionUser" 키로 저장된 객체를 조회하여 반환</li>
 * <li>세션에 사용자 정보가 없으면 null 반환</li>
 * </ul>
 */
@Component
public class SessionUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String SESSION_USER = "sessionUser";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && SessionUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpSession session = request != null ? request.getSession(false) : null;
        if (session == null) {
            return null;
        }
        return session.getAttribute(SESSION_USER);
    }
}
