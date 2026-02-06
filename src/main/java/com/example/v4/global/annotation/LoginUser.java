package com.example.v4.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link com.example.v4.global.dto.SessionUser}를 컨트롤러 메서드 파라미터로 주입하기 위한 어노테이션.
 *
 * <p>
 * 기능:
 * <ul>
 * <li>HttpSession에서 "sessionUser" 키로 저장된 SessionUser 객체를 자동 추출</li>
 * <li>로그인하지 않은 경우 null 반환 (required=false 동작)</li>
 * <li>HandlerMethodArgumentResolver를 통해 세션 조회 및 캐스팅 로직을 컨트롤러에서 제거</li>
 * </ul>
 *
 * <p>
 * 사용 예:
 *
 * <pre>{@code
 * @GetMapping("/")
 * public String board(@LoginUser SessionUser user, Model model) {
 *     if (user != null) {
 *         model.addAttribute("isLogin", true);
 *     }
 *     return "index";
 * }
 * }</pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
