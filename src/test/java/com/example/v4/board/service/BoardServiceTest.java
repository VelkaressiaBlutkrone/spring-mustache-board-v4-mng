package com.example.v4.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.v4.board.dto.BoardReponseDto;
import com.example.v4.board.dto.BoardRequestDto;
import com.example.v4.board.entity.Board;
import com.example.v4.board.mapper.BoardMapper;
import com.example.v4.board.repository.BoardRepository;
import com.example.v4.global.dto.Dto;
import com.example.v4.global.dto.SessionUser;
import com.example.v4.global.exception.BoardAccessDeniedException;
import com.example.v4.global.exception.BoardNotFoundException;
import com.example.v4.global.resetclient.RestClients;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 서비스 단위 테스트")
class BoardServiceTest {

    @Mock
    private BoardMapper mapper;

    @Mock
    private BoardRepository repository;

    @Mock
    private RestClients rc;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("insert - 로그인 사용자가 있으면 게시글이 저장된다")
    void insert_로그인사용자면_게시글이저장된다() {
        // given
        SessionUser user = new SessionUser(1, "user1", "user1@email.com");
        BoardRequestDto dto = new BoardRequestDto("제목", "내용");
        Board board = Board.builder()
                .id(1)
                .title("제목")
                .content("내용")
                .writerId(1)
                .build();
        given(mapper.toBoard(isNull(), eq(1), any(BoardRequestDto.class))).willReturn(board);
        given(repository.save(any(Board.class))).willReturn(board);

        // when
        boardService.insert(dto, user);

        // then
        // insert 내부에서 save 호출 후 예외 없이 완료되면 성공
    }

    @Test
    @DisplayName("insert - 로그인 사용자가 없으면 예외를 던진다")
    void insert_로그인사용자없으면_예외를던진다() {
        // given
        BoardRequestDto dto = new BoardRequestDto("제목", "내용");

        // when & then
        assertThatThrownBy(() -> boardService.insert(dto, null))
                .isInstanceOf(BoardAccessDeniedException.class)
                .hasMessage("로그인이 필요합니다.");
    }

    @Test
    @DisplayName("updateBoardIfOwner - 작성자 본인이면 수정된다")
    void updateBoardIfOwner_작성자본인이면_수정된다() {
        // given
        SessionUser user = new SessionUser(1, "user1", "user1@email.com");
        BoardRequestDto dto = new BoardRequestDto("수정 제목", "수정 내용");
        Board board = Board.builder()
                .id(1)
                .title("원본")
                .content("내용")
                .writerId(1)
                .build();
        BoardReponseDto responseDto = new BoardReponseDto("1", "원본", "내용", "1", "user1", new ArrayList<>());

        given(repository.findById(1)).willReturn(Optional.of(board));
        given(rc.get(anyString(), eq(Dto.User.class), anyInt())).willReturn(new Dto.User(1, "user1"));
        given(mapper.toResponseDto(any(Board.class), anyString(), eq(user))).willReturn(responseDto);
        given(mapper.toBoard(eq(1), eq(1), any(BoardRequestDto.class))).willReturn(board);
        given(repository.save(any(Board.class))).willReturn(board);

        // when
        boardService.updateBoardIfOwner("1", user, dto);

        // then
        // 예외 없이 완료되면 성공
    }

    @Test
    @DisplayName("updateBoardIfOwner - 작성자가 아니면 예외를 던진다")
    void updateBoardIfOwner_작성자아니면_예외를던진다() {
        // given
        SessionUser differentUser = new SessionUser(2, "user2", "user2@email.com");
        BoardRequestDto dto = new BoardRequestDto("수정 제목", "수정 내용");
        Board board = Board.builder()
                .id(1)
                .title("원본")
                .content("내용")
                .writerId(1)
                .build();
        BoardReponseDto responseDto = new BoardReponseDto("1", "원본", "내용", "1", "user1", new ArrayList<>());

        given(repository.findById(1)).willReturn(Optional.of(board));
        given(rc.get(anyString(), eq(Dto.User.class), anyInt())).willReturn(new Dto.User(1, "user1"));
        given(mapper.toResponseDto(any(Board.class), anyString(), eq(differentUser))).willReturn(responseDto);

        // when & then
        assertThatThrownBy(() -> boardService.updateBoardIfOwner("1", differentUser, dto))
                .isInstanceOf(BoardAccessDeniedException.class)
                .hasMessage("수정 권한이 없습니다.");
    }

    @Test
    @DisplayName("updateBoardIfOwner - 로그인하지 않았으면 예외를 던진다")
    void updateBoardIfOwner_로그인없으면_예외를던진다() {
        // given
        BoardRequestDto dto = new BoardRequestDto("수정 제목", "수정 내용");

        // when & then
        assertThatThrownBy(() -> boardService.updateBoardIfOwner("1", null, dto))
                .isInstanceOf(BoardAccessDeniedException.class)
                .hasMessage("로그인이 필요합니다.");
    }

    @Test
    @DisplayName("deleteBoardIfOwner - 작성자 본인이면 삭제된다")
    void deleteBoardIfOwner_작성자본인이면_삭제된다() {
        // given
        SessionUser user = new SessionUser(1, "user1", "user1@email.com");
        Board board = Board.builder()
                .id(1)
                .title("제목")
                .content("내용")
                .writerId(1)
                .build();
        BoardReponseDto responseDto = new BoardReponseDto("1", "제목", "내용", "1", "user1", new ArrayList<>());

        given(repository.findById(1)).willReturn(Optional.of(board));
        given(rc.get(anyString(), eq(Dto.User.class), anyInt())).willReturn(new Dto.User(1, "user1"));
        given(mapper.toResponseDto(any(Board.class), anyString(), eq(user))).willReturn(responseDto);

        // when
        boardService.deleteBoardIfOwner("1", user);

        // then
        // 예외 없이 완료되면 성공 (delete 내부에서 repository.delete 호출)
    }

    @Test
    @DisplayName("deleteBoardIfOwner - 작성자가 아니면 예외를 던진다")
    void deleteBoardIfOwner_작성자아니면_예외를던진다() {
        // given
        SessionUser differentUser = new SessionUser(2, "user2", "user2@email.com");
        Board board = Board.builder()
                .id(1)
                .title("제목")
                .content("내용")
                .writerId(1)
                .build();
        BoardReponseDto responseDto = new BoardReponseDto("1", "제목", "내용", "1", "user1", new ArrayList<>());

        given(repository.findById(1)).willReturn(Optional.of(board));
        given(rc.get(anyString(), eq(Dto.User.class), anyInt())).willReturn(new Dto.User(1, "user1"));
        given(mapper.toResponseDto(any(Board.class), anyString(), eq(differentUser))).willReturn(responseDto);

        // when & then
        assertThatThrownBy(() -> boardService.deleteBoardIfOwner("1", differentUser))
                .isInstanceOf(BoardAccessDeniedException.class)
                .hasMessage("삭제 권한이 없습니다.");
    }

    @Test
    @DisplayName("getBoardDetail - 존재하지 않는 게시글이면 예외를 던진다")
    void getBoardDetail_존재하지않으면_예외를던진다() {
        // given
        SessionUser user = new SessionUser(1, "user1", "user1@email.com");
        given(repository.findById(999)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoardDetail("999", user))
                .isInstanceOf(BoardNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("getBoardDetail - 존재하는 게시글이면 상세와 수정 가능 여부를 반환한다")
    void getBoardDetail_존재하는게시글이면_상세와수정가능여부를반환한다() {
        // given
        SessionUser user = new SessionUser(1, "user1", "user1@email.com");
        Board board = Board.builder()
                .id(1)
                .title("제목")
                .content("내용")
                .writerId(1)
                .build();
        BoardReponseDto responseDto = new BoardReponseDto("1", "제목", "내용", "1", "user1", new ArrayList<>());

        given(repository.findById(1)).willReturn(Optional.of(board));
        given(rc.get(anyString(), eq(Dto.User.class), anyInt())).willReturn(new Dto.User(1, "user1"));
        given(mapper.toResponseDto(any(Board.class), anyString(), eq(user))).willReturn(responseDto);

        // when
        var result = boardService.getBoardDetail("1", user);

        // then
        assertThat(result.board()).isEqualTo(responseDto);
        assertThat(result.isModify()).isTrue();
    }

    @Test
    @DisplayName("jsoupYouTubeParser - YouTube URL이 포함되면 iframe으로 변환한다")
    void jsoupYouTubeParser_youtubeURL이면_iframe으로변환한다() {
        // given
        String content = "영상 링크: https://www.youtube.com/watch?v=dQw4w9WgXcQ";

        // when
        String result = boardService.jsoupYouTubeParser(content);

        // then
        assertThat(result).contains("iframe");
        assertThat(result).contains("youtube.com/embed/dQw4w9WgXcQ");
    }

    @Test
    @DisplayName("jsoupYouTubeParser - youtu.be 짧은 URL도 iframe으로 변환한다")
    void jsoupYouTubeParser_youtuBe짧은URL도_iframe으로변환한다() {
        // given
        String content = "https://youtu.be/dQw4w9WgXcQ";

        // when
        String result = boardService.jsoupYouTubeParser(content);

        // then
        assertThat(result).contains("iframe");
        assertThat(result).contains("youtube.com/embed/dQw4w9WgXcQ");
    }

    @Test
    @DisplayName("jsoupYouTubeParser - null 또는 빈 문자열이면 그대로 반환한다")
    void jsoupYouTubeParser_null이면_그대로반환한다() {
        // given & when
        String nullResult = boardService.jsoupYouTubeParser(null);
        String blankResult = boardService.jsoupYouTubeParser("   ");

        // then
        assertThat(nullResult).isNull();
        assertThat(blankResult).isEqualTo("   ");
    }
}
