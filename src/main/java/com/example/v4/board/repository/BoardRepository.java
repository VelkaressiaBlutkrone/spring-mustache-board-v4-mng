package com.example.v4.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.v4.board.entity.Board;

/**
 * 게시글 리포지토리 (Repository)
 *
 * 역할:
 * - 데이터베이스의 'board_tb' 테이블에 접근하여 게시글 데이터의 영속성(CRUD)을 관리합니다.
 * - Spring Data JPA의 JpaRepository를 상속받아 복잡한 JDBC 코딩 없이 인터페이스 선언만으로 DB 작업을
 * 수행합니다.
 */
@Repository // 스프링 컨테이너에 빈(Bean)으로 등록합니다.
public interface BoardRepository extends JpaRepository<Board, Integer> {

    /**
     * JpaRepository<Board, Integer> 상속의 의미:
     * 1. Board: 관리할 엔티티 클래스 (DB 테이블과 매핑됨)
     * 2. Integer: 해당 엔티티의 PK(Primary Key) 데이터 타입
     *
     * 상속만으로 findAll(), findById(), save(), delete() 등의 메서드를 자동으로 사용할 수 있습니다.
     */

    // 게시글 전체를 ID 기준 내림차순으로 조회
    List<Board> findAllByOrderByIdDesc();

    // 제목에 특정 단어가 포함된 게시글 검색 (ID 내림차순 정렬)
    List<Board> findByTitleContainingOrderByIdDesc(String keyword);
}
