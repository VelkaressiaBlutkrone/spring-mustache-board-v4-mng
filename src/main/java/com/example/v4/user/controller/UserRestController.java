package com.example.v4.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.v4.global.dto.Dto;
import com.example.v4.user.entity.User;
import com.example.v4.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {

    final UserService userService;

    @GetMapping("/user/info")
    public Dto.User userInfo(@RequestParam("writerId") Integer param) {
        User user = userService.userInfo(param).orElseThrow();

        return Dto.User.from(user);
    }

}
