package com.example.v4.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.v4.board.entity.Board;

@DataJpaTest
@DisplayName("게시글 저장소")
class BoardRepositoryTest {

    private static final Timestamp CREATED_AT = Timestamp.from(Instant.parse("2025-01-01T00:00:00Z"));

    @Autowired
    private BoardRepository repository;

    @Test
    @DisplayName("save - 게시글 저장 시 ID가 부여되고 저장된 값이 반환된다")
    void save_게시글저장시_ID가부여된다() {
        // given
        Board board = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .writerId(1)
                .createdAt(CREATED_AT)
                .build();

        // when
        Board savedBoard = repository.save(board);

        // then
        assertThat(savedBoard).isNotNull();
        assertThat(savedBoard.getId()).isNotNull();
        assertThat(savedBoard)
                .extracting("title", "content", "writerId")
                .containsExactly("테스트 제목", "테스트 내용", 1);
    }

    @Test
    @DisplayName("findAllByOrderByIdDesc - 여러 게시글이 있으면 ID 내림차순으로 반환한다")
    void findAllByOrderByIdDesc_여러게시글이있으면_최신순으로반환한다() {
        // given
        repository.save(Board.builder()
                .title("첫 번째")
                .content("내용1")
                .writerId(1)
                .createdAt(CREATED_AT)
                .build());

        repository.save(Board.builder()
                .title("두 번째")
                .content("내용2")
                .writerId(1)
                .createdAt(CREATED_AT)
                .build());

        // when
        var boards = repository.findAllByOrderByIdDesc();

        // then
        assertThat(boards).hasSizeGreaterThanOrEqualTo(2);
        assertThat(boards.get(0).getId()).isGreaterThan(boards.get(1).getId());
        assertThat(boards.get(0).getTitle()).isEqualTo("두 번째");
    }

    @Test
    @DisplayName("findById - 존재하는 ID로 조회하면 해당 게시글을 반환한다")
    void findById_존재하는ID면_게시글을반환한다() {
        // given
        Board board = repository.save(Board.builder()
                .title("조회 테스트")
                .content("내용")
                .writerId(1)
                .createdAt(CREATED_AT)
                .build());

        // when
        var found = repository.findById(board.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get())
                .extracting("title", "writerId")
                .containsExactly("조회 테스트", 1);
    }

    @Test
    @DisplayName("findByTitleContainingOrderByIdDesc - 제목에 키워드가 포함된 게시글만 반환한다")
    void findByTitleContainingOrderByIdDesc_키워드포함게시글만_반환한다() {
        // given
        repository.save(Board.builder()
                .title("Spring 학습")
                .content("내용1")
                .writerId(1)
                .createdAt(CREATED_AT)
                .build());

        repository.save(Board.builder()
                .title("Java 기본")
                .content("내용2")
                .writerId(1)
                .createdAt(CREATED_AT)
                .build());

        repository.save(Board.builder()
                .title("Spring Boot 실습")
                .content("내용3")
                .writerId(1)
                .createdAt(CREATED_AT)
                .build());

        // when
        var boards = repository.findByTitleContainingOrderByIdDesc("Spring");

        // then
        assertThat(boards).hasSize(2);
        assertThat(boards).allMatch(b -> b.getTitle().contains("Spring"));
    }
}
