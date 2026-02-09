package com.example.v4.user.repository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.v4.user.entity.User;

@DataJpaTest
@DisplayName("사용자 저장소")
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    @DisplayName("회원 저장 - 정상 저장 시 ID가 부여되고 저장된 값이 반환된다")
    void save_test() {
        // given
        User user = User.builder()
                .userName("testuser")
                .password("testpass")
                .email("test@email.com")
                .createdAt(java.time.LocalDateTime.now())
                .build();

        // when
        User savedUser = repository.save(user);

        // then
        org.assertj.core.api.Assertions.assertThat(savedUser).isNotNull();
        org.assertj.core.api.Assertions.assertThat(savedUser).extracting("userName").isEqualTo("testuser");
        org.assertj.core.api.Assertions.assertThat(savedUser).extracting("email").isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("findByUserName - 사용자명으로 조회 시 해당 회원을 반환한다")
    void findByUserName_test() {
        // given
        User user = User.builder()
                .userName("findUser")
                .password("1234")
                .email("find@email.com")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        repository.save(user);

        // when
        Optional<User> opUser = repository.findByUserName("findUser");

        // then
        org.assertj.core.api.Assertions.assertThat(opUser).isPresent();
        org.assertj.core.api.Assertions.assertThat(opUser.get())
                .extracting("userName").isEqualTo("findUser");
    }
}
