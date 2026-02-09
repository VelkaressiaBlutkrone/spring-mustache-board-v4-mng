package com.example.v4.user.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.v4.global.dto.SessionUser;
import com.example.v4.global.exception.UserValidationException;
import com.example.v4.user.dto.UserRequestDto;
import com.example.v4.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 회원가입·로그인·로그아웃을 담당하는 MVC 컨트롤러.
 *
 * <p>세션 기반 인증을 사용하며, 로그인 성공 시 SessionUser를 세션에 저장한다.
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private static final String SESSION_USER = "sessionUser";

    private final UserService service;

    /**
     * 회원가입 폼 페이지를 반환한다.
     *
     * @return 뷰 이름 "user/join-form"
     */
    @GetMapping("/join-form")
    public String joinForm() {
        return "user/join-form";
    }

    /**
     * 로그인 폼 페이지를 반환한다.
     *
     * @return 뷰 이름 "user/login-form"
     */
    @GetMapping("/login-form")
    public String loginForm() {
        return "user/login-form";
    }

    /**
     * 회원가입을 처리한다. 중복 사용자명이면 UserDuplicationException이 발생한다.
     *
     * @param joinDto 회원가입 요청 DTO
     * @param br 바인딩 결과 (검증 실패 시 UserValidationException)
     * @return 로그인 폼으로 리다이렉트
     */
    @PostMapping("/join")
    public ResponseEntity<Void> join(@Valid UserRequestDto.Join joinDto, BindingResult br) {
        if (br.hasErrors()) {
            throw new UserValidationException(br, joinDto, "user/join-form");
        }
        return service.join(joinDto);
    }

    /**
     * 로그인을 처리한다. 인증 성공 시 세션에 SessionUser를 저장하고 메인으로 이동한다.
     *
     * @param loginDto 로그인 요청 DTO
     * @param br 바인딩 결과 (검증 실패 시 UserValidationException)
     * @param session HTTP 세션 (SessionUser 저장용)
     * @return 메인 또는 로그인 폼으로 리다이렉트
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid UserRequestDto.Login loginDto, BindingResult br, HttpSession session) {
        if (br.hasErrors()) {
            throw new UserValidationException(br, loginDto, "user/login-form");
        }

        var userOpt = service.authenticate(loginDto);

        if (userOpt.isPresent()) {
            session.setAttribute(SESSION_USER, SessionUser.from(userOpt.get()));

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/login-form")).build();
    }

    /**
     * 로그아웃을 처리한다. 세션의 SessionUser를 제거하고 메인으로 리다이렉트한다.
     *
     * @param session HTTP 세션
     * @return 메인 페이지로 리다이렉트
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        if (session.getAttribute(SESSION_USER) != null) {
            session.removeAttribute(SESSION_USER);
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
    }

}
