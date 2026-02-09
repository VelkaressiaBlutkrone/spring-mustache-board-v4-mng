package com.example.v4.user.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.v4.global.exception.UserDuplicationException;
import com.example.v4.user.dto.UserRequestDto.Join;
import com.example.v4.user.dto.UserRequestDto.Login;
import com.example.v4.user.entity.User;
import com.example.v4.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 회원 조회·가입·인증을 담당하는 서비스.
 *
 * <p>비밀번호는 BCrypt로 해싱하여 저장·검증한다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일로 회원을 조회한다.
     *
     * @param email 이메일
     * @return 회원 Optional
     */
    public Optional<User> userInfoByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * 사용자명으로 회원을 조회한다.
     *
     * @param userName 사용자명
     * @return 회원 Optional
     */
    public Optional<User> userInfoByName(String userName) {
        return repository.findByUserName(userName);
    }

    /**
     * ID로 회원을 조회한다.
     *
     * @param id 회원 ID
     * @return 회원 Optional
     */
    public Optional<User> userInfo(Integer id) {
        return repository.findById(id);
    }

    /**
     * 회원가입을 처리한다. 비밀번호는 BCrypt로 해싱하여 저장한다.
     *
     * @param joinDto 회원가입 요청 DTO
     * @return 로그인 폼으로 리다이렉트 응답
     * @throws UserDuplicationException 사용자명 중복 시
     */
    public ResponseEntity<Void> join(Join joinDto) {
        boolean isPresent = repository.findByUserName(joinDto.username()).isPresent();

        if (isPresent) {
            throw new UserDuplicationException("이미 존재하는 사용자입니다.");
        }

        User user = User.builder()
                .userName(joinDto.username())
                .password(passwordEncoder.encode(joinDto.password()))
                .email(joinDto.email())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(user);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/login-form")).build();
    }

    /**
     * 로그인 인증을 수행한다. 사용자명과 비밀번호가 일치하면 회원을 반환한다.
     *
     * @param loginDto 로그인 요청 DTO
     * @return 인증 성공 시 회원 Optional, 실패 시 empty
     */
    public Optional<User> authenticate(Login loginDto) {
        return repository.findByUserName(loginDto.username())
                .filter(user -> passwordEncoder.matches(loginDto.password(), user.getPassword()));
    }
}
