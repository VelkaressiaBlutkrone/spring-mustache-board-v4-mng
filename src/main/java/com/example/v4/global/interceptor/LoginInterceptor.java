package com.example.v4.global.interceptor;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.example.v4.global.dto.SessionUser;
import com.example.v4.global.exception.RestServerErrorException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String SESSION_USER = "sessionUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        // 예외: /board/detail/숫자 패턴 -> 인증 체크 안 함
        if (uri.matches(".*/board/detail/\\d+$")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        SessionUser sessionUser = session != null ? (SessionUser) session.getAttribute(SESSION_USER) : null;

        if (sessionUser == null) {
            throw new RestServerErrorException("인증되지 않았습니다");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception {

    }

}
