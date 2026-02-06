package com.example.v4.user.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.v4.global.exception.UserDuplicationException;
import com.example.v4.user.dto.UserRequestDto.Join;
import com.example.v4.user.dto.UserRequestDto.Login;
import com.example.v4.user.entity.User;
import com.example.v4.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public Optional<User> userInfoByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> userInfoByName(String userName) {
        return repository.findByUserName(userName);
    }

    public Optional<User> userInfo(Integer id) {
        return repository.findById(id);
    }

    public ResponseEntity<Void> join(Join joinDto) {
        boolean isPresent = repository.findByUserName(joinDto.username()).isPresent();

        if (isPresent) {
            throw new UserDuplicationException("이미 존재하는 사용자입니다.");
        }

        User user = User.builder()
                .userName(joinDto.username())
                .password(joinDto.password())
                .email(joinDto.email())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(user);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/login-form")).build();
    }

    public Optional<User> authenticate(Login loginDto) {
        return repository.findByUserName(loginDto.username())
                .filter(user -> user.getPassword().equals(loginDto.password()));
    }
}
