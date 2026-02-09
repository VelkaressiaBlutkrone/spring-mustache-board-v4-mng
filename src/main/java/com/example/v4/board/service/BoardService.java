package com.example.v4.board.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.v4.board.dto.BoardReponseDto;
import com.example.v4.board.dto.BoardRequestDto;
import com.example.v4.board.entity.Board;
import com.example.v4.board.mapper.BoardMapper;
import com.example.v4.board.repository.BoardRepository;
import com.example.v4.global.dto.Dto;
import com.example.v4.global.dto.SessionUser;
import com.example.v4.global.exception.BoardAccessDeniedException;
import com.example.v4.global.exception.BoardNotFoundException;
import com.example.v4.global.exception.InvalidBoardIdException;
import com.example.v4.global.resetclient.RestClients;

import lombok.RequiredArgsConstructor;

/**
 * 게시글 목록·상세·등록·수정·삭제를 담당하는 서비스.
 *
 * <p>내용에 포함된 YouTube URL을 iframe 임베드로 변환하여 저장한다.
 * 작성자 이름은 내부 REST API로 조회한다.
 */
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper mapper;
    private final BoardRepository repository;
    private final RestClients rc;

    /**
     * 게시글 목록을 최신순으로 조회한다.
     *
     * @param su 로그인 사용자 (null 가능, 수정 버튼 노출 여부 결정)
     * @return 조회된 게시글 DTO 목록
     */
    public List<BoardReponseDto> list(SessionUser su) {
        return repository.findAllByOrderByIdDesc().stream().map(board -> {
            Dto.User user = rc.get("/api/user/info?writerId={writerId}", Dto.User.class, board.getWriterId());
            return mapper.toResponseDto(board, user.name(), su);
        }).toList();
    }

    /**
     * ID로 게시글 DTO를 조회한다.
     *
     * @param id 게시글 ID (문자열)
     * @param su 로그인 사용자 (null 가능)
     * @return 게시글 DTO Optional
     */
    public Optional<BoardReponseDto> boardDto(String id, SessionUser su) {
        int boardId = parseBoardId(id);
        return repository.findById(boardId).stream().map(board -> {
            Dto.User user = rc.get("/api/user/info?writerId={writerId}", Dto.User.class, board.getWriterId());
            return mapper.toResponseDto(board, user.name(), su);
        }).findFirst();
    }

    @Transactional
    public void save(String boardId, Integer userId, BoardRequestDto dto) {
        dto.setContent(jsoupYouTubeParser(dto.getContent()));
        Integer parsedId = boardId != null ? parseBoardId(boardId) : null;
        repository.save(mapper.toBoard(parsedId, userId, dto));
    }

    public void update(String id, BoardRequestDto dto) {
        int boardId = parseBoardId(id);
        Board board = repository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
        board.setTitle(dto.getTitle());
    }

    @Transactional
    public void delete(String id) {
        int boardId = parseBoardId(id);
        Board delItem = repository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
        repository.delete(delItem);
    }

    /**
     * 게시글 상세 정보와 수정 가능 여부를 반환한다.
     *
     * @param id 게시글 ID
     * @param user 로그인 사용자 (null 가능)
     * @return 상세 DTO와 수정 가능 여부
     * @throws BoardNotFoundException 게시글 미존재 시
     */
    public BoardDetailResult getBoardDetail(String id, SessionUser user) {
        BoardReponseDto board = boardDto(id, user)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        boolean isModify = user != null && board.getWriteId() != null
                && user.id().toString().equals(board.getWriteId());
        return new BoardDetailResult(board, isModify);
    }

    /**
     * 수정 폼용 게시글 조회 (DB 조회는 서비스 내부에서 수행)
     */
    public BoardReponseDto getBoardForUpdateForm(String id, SessionUser user) {
        return boardDto(id, user).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
    }

    /**
     * 게시글 등록 (로그인 사용자만, DB 저장은 서비스 내부에서 수행)
     */
    public void insert(BoardRequestDto dto, SessionUser user) {
        if (user == null) {
            throw new BoardAccessDeniedException("로그인이 필요합니다.");
        }
        save(null, user.id(), dto);
    }

    /**
     * 작성자 본인일 때만 수정 (DB 조회/저장은 서비스 내부에서 수행)
     */
    @Transactional
    public void updateBoardIfOwner(String boardId, SessionUser user, BoardRequestDto dto) {
        if (user == null) {
            throw new BoardAccessDeniedException("로그인이 필요합니다.");
        }
        BoardReponseDto board = boardDto(boardId, user)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        if (!String.valueOf(user.id()).equals(board.getWriteId())) {
            throw new BoardAccessDeniedException("수정 권한이 없습니다.");
        }
        save(boardId, user.id(), dto);
    }

    /**
     * 작성자 본인일 때만 삭제 (DB 조회/삭제는 서비스 내부에서 수행)
     */
    @Transactional
    public void deleteBoardIfOwner(String boardId, SessionUser user) {
        if (user == null) {
            throw new BoardAccessDeniedException("로그인이 필요합니다.");
        }
        BoardReponseDto board = boardDto(boardId, user)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        if (!String.valueOf(user.id()).equals(board.getWriteId())) {
            throw new BoardAccessDeniedException("삭제 권한이 없습니다.");
        }
        delete(boardId);
    }

    /**
     * Summernote 내용에서 YouTube URL을 HTML iframe 임베드로 변환
     * - 지원 형식: youtube.com/watch?v=ID, youtu.be/ID
     */
    public String jsoupYouTubeParser(String content) {
        if (content == null || content.isBlank()) {
            return content;
        }

        Document doc = Jsoup.parse(content);

        // 1. YouTube 링크인 <a> 태그를 iframe으로 교체
        Elements youtubeLinks = doc.select("a[href*='youtube.com'], a[href*='youtu.be']");

        for (Element link : youtubeLinks) {
            String videoId = extractYouTubeVideoId(link.attr("href"));

            if (videoId != null) {
                Element iframe = doc.createElement("iframe");
                iframe.attr("src", "https://www.youtube.com/embed/" + videoId);
                iframe.attr("width", "560");
                iframe.attr("height", "315");
                iframe.attr("frameborder", "0");
                iframe.attr("allowfullscreen", "");
                link.replaceWith(iframe);
            }
        }

        // 2. 텍스트 내 YouTube URL을 iframe으로 교체
        String html = doc.body().html();

        String iframeReplacement = "<iframe src=\"https://www.youtube.com/embed/$1\" width=\"560\" height=\"315\" frameborder=\"0\" allowfullscreen></iframe>";

        html = html.replaceAll("https?://(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_-]{11})", iframeReplacement);
        html = html.replaceAll("https?://(?:www\\.)?youtu\\.be/([a-zA-Z0-9_-]{11})", iframeReplacement);

        return html;
    }

    private int parseBoardId(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidBoardIdException("잘못된 게시글 ID입니다.");
        }
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new InvalidBoardIdException("잘못된 게시글 ID 형식입니다.");
        }
    }

    private String extractYouTubeVideoId(String url) {
        if (url == null || url.isBlank())
            return null;
        Pattern watchPattern = Pattern.compile("(?:youtube\\.com/watch\\?v=)([a-zA-Z0-9_-]{11})");
        Pattern shortPattern = Pattern.compile("(?:youtu\\.be/)([a-zA-Z0-9_-]{11})");
        Matcher m = watchPattern.matcher(url);

        if (m.find())
            return m.group(1);

        m = shortPattern.matcher(url);
        return m.find() ? m.group(1) : null;
    }

    /** 상세 화면 결과 (게시글 + 수정 가능 여부) */
    public record BoardDetailResult(BoardReponseDto board, boolean isModify) {
    }
}
