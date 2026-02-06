package com.example.v4.global.dto;

import java.io.Serializable;

import com.example.v4.user.entity.User;

public record SessionUser(
        Integer id, // 사용자 PK 값
        String userName, // 사용자 이름
        String email // 사용자 이메일
) implements Serializable {

    // 직렬화 버전 UID (세션 정보가 변경될 때 문제 방지)
    private static final long serialVersionUID = 1L;

    /**
     * User 엔티티로부터 SessionUser를 생성하는 변환 메서드.
     *
     * @param user 세션에 저장할 User 엔티티 객체
     * @return SessionUser 인스턴스
     */
    public static SessionUser from(User user) {
        // User 객체의 id, userName, email만 발췌하여 새 SessionUser 생성
        return new SessionUser(user.getId(), user.getUserName(), user.getEmail());
    }
}
