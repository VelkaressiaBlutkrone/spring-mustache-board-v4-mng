package com.example.v4.user.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.v4.user.entity.User;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
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
