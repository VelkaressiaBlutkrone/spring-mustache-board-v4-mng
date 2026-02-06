package com.example.v4.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.v4.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String name);

    Optional<User> findById(Integer id);
}
