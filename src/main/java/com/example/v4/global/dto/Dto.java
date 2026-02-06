package com.example.v4.global.dto;

public class Dto {
    public record User(Integer id, String name) {
        public static User from(com.example.v4.user.entity.User user) {
            return new User(user.getId(), user.getUserName());
        }
    }
}
