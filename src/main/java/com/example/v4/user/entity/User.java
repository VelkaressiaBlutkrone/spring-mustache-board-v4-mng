package com.example.v4.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "user_tb")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    private String userName;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_password", nullable = false, length = 100)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
