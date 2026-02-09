package com.example.v4.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.v4.global.exception.UserDuplicationException;
import com.example.v4.user.dto.UserRequestDto.Join;
import com.example.v4.user.dto.UserRequestDto.Login;
import com.example.v4.user.entity.User;
import com.example.v4.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("회원 서비스 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("join - 존재하지 않는 사용자명이면 회원가입에 성공한다")
    void join_존재하지않는사용자명이면_회원가입성공한다() {
        // given
        Join joinDto = new Join("newuser", "password123", "new@email.com");
        given(repository.findByUserName("newuser")).willReturn(Optional.empty());
        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
        given(repository.save(any(User.class))).willAnswer(inv -> {
            User u = inv.getArgument(0);
            return User.builder()
                    .id(1)
                    .userName(u.getUserName())
                    .password(u.getPassword())
                    .email(u.getEmail())
                    .createdAt(u.getCreatedAt())
                    .build();
        });

        // when
        ResponseEntity<Void> result = userService.join(joinDto);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(result.getHeaders().getLocation()).hasPath("/login-form");
    }

    @Test
    @DisplayName("join - 중복 사용자명이면 예외를 던진다")
    void join_중복사용자명이면_예외를던진다() {
        // given
        Join joinDto = new Join("duplicate", "password123", "dup@email.com");
        User existingUser = User.builder()
                .id(1)
                .userName("duplicate")
                .password("encoded")
                .email("dup@email.com")
                .createdAt(LocalDateTime.now())
                .build();
        given(repository.findByUserName("duplicate")).willReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> userService.join(joinDto))
                .isInstanceOf(UserDuplicationException.class)
                .hasMessage("이미 존재하는 사용자입니다.");
    }

    @Test
    @DisplayName("authenticate - 일치하는 사용자명과 비밀번호가 주어지면 회원을 반환한다")
    void authenticate_일치하는사용자명비밀번호면_회원을반환한다() {
        // given
        Login loginDto = new Login("testuser", "password123");
        User savedUser = User.builder()
                .id(1)
                .userName("testuser")
                .password("encodedPassword")
                .email("test@email.com")
                .createdAt(LocalDateTime.now())
                .build();
        given(repository.findByUserName("testuser")).willReturn(Optional.of(savedUser));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);

        // when
        Optional<User> actual = userService.authenticate(loginDto);

        // then
        assertThat(actual)
                .isPresent()
                .get()
                .extracting("userName", "email")
                .containsExactly("testuser", "test@email.com");
    }

    @Test
    @DisplayName("authenticate - 비밀번호가 불일치하면 empty를 반환한다")
    void authenticate_비밀번호불일치면_empty반환한다() {
        // given
        Login loginDto = new Login("testuser", "wrongPassword");
        User savedUser = User.builder()
                .id(1)
                .userName("testuser")
                .password("encodedPassword")
                .email("test@email.com")
                .createdAt(LocalDateTime.now())
                .build();
        given(repository.findByUserName("testuser")).willReturn(Optional.of(savedUser));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        // when
        Optional<User> actual = userService.authenticate(loginDto);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("authenticate - 존재하지 않는 사용자명이면 empty를 반환한다")
    void authenticate_존재하지않는사용자명이면_empty반환한다() {
        // given
        Login loginDto = new Login("unknown", "password123");
        given(repository.findByUserName("unknown")).willReturn(Optional.empty());

        // when
        Optional<User> actual = userService.authenticate(loginDto);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("userInfoByName - 존재하는 사용자명이면 해당 회원을 반환한다")
    void userInfoByName_존재하는사용자명이면_회원을반환한다() {
        // given
        User expected = User.builder()
                .id(1)
                .userName("findme")
                .password("encoded")
                .email("find@email.com")
                .createdAt(LocalDateTime.now())
                .build();
        given(repository.findByUserName("findme")).willReturn(Optional.of(expected));

        // when
        Optional<User> actual = userService.userInfoByName("findme");

        // then
        assertThat(actual)
                .isPresent()
                .get()
                .extracting("id", "userName")
                .containsExactly(1, "findme");
    }

    @Test
    @DisplayName("userInfoByName - 존재하지 않는 사용자명이면 empty를 반환한다")
    void userInfoByName_존재하지않으면_empty반환한다() {
        // given
        given(repository.findByUserName("nobody")).willReturn(Optional.empty());

        // when
        Optional<User> actual = userService.userInfoByName("nobody");

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("userInfo - 존재하는 ID로 조회하면 해당 회원을 반환한다")
    void userInfo_존재하는ID면_회원을반환한다() {
        // given
        User expected = User.builder()
                .id(1)
                .userName("user1")
                .password("encoded")
                .email("user1@email.com")
                .createdAt(LocalDateTime.now())
                .build();
        given(repository.findById(1)).willReturn(Optional.of(expected));

        // when
        Optional<User> actual = userService.userInfo(1);

        // then
        assertThat(actual)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("userName", "user1");
    }
}
