package com.example.v4.global.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.v4.global.dto.SessionUser;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 모든 뷰에서 isLogin, user, setCookie를 사용할 수 있도록
 * 요청마다 세션 사용자 정보를 Request Attribute에 설정하는 인터셉터.
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    private static final String SESSION_USER = "sessionUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        SessionUser user = session != null ? (SessionUser) session.getAttribute(SESSION_USER) : null;

        request.setAttribute("isLogin", user != null);

        if (user != null) {
            request.setAttribute("user", user);
            Cookie cookie = new Cookie(SESSION_USER, user.userName());
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24); // 1일 유효
            request.setAttribute("setCookie", cookie);
        }

        return true;
    }
}
