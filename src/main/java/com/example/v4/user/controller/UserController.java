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

@Controller
@RequiredArgsConstructor
public class UserController {

    private static final String SESSION_USER = "sessionUser";

    private final UserService service;

    @GetMapping("/join-form")
    public String joinForm() {
        return "user/join-form";
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "user/login-form";
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(@Valid UserRequestDto.Join joinDto, BindingResult br) {
        if (br.hasErrors()) {
            throw new UserValidationException(br, joinDto, "user/join-form");
        }
        return service.join(joinDto);
    }

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

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        if (session.getAttribute(SESSION_USER) != null) {
            session.removeAttribute(SESSION_USER);
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
    }

}
