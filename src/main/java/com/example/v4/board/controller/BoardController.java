package com.example.v4.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.v4.board.dto.BoardReponseDto;
import com.example.v4.board.dto.BoardRequestDto;
import com.example.v4.board.service.BoardService;
import com.example.v4.global.annotation.LoginUser;
import com.example.v4.global.annotation.ValidateOnError;
import com.example.v4.global.dto.SessionUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 게시글 목록·상세·등록·수정·삭제를 담당하는 MVC 컨트롤러.
 *
 * <p>로그인 사용자만 글 작성·수정·삭제 가능하며, 작성자 본인만 수정·삭제할 수 있다.
 */
@RequiredArgsConstructor
@Controller
public class BoardController {

    final BoardService boardService;

    /**
     * 게시글 목록 메인 페이지를 반환한다.
     *
     * @param user 로그인 사용자 (null 가능)
     * @param req 요청 객체 (models 속성 주입용)
     * @return 뷰 이름 "index"
     */
    @GetMapping("/")
    public String board(@LoginUser SessionUser user, HttpServletRequest req) {
        List<BoardReponseDto> models = boardService.list(user);
        req.setAttribute("models", models);
        return "index";
    }

    /**
     * 게시글 상세 페이지를 반환한다.
     *
     * @param id 게시글 ID
     * @param user 로그인 사용자 (null 가능)
     * @param req 요청 객체 (model, isModify 속성 주입용)
     * @return 뷰 이름 "board/detail"
     */
    @GetMapping("/board/detail/{id}")
    public String detail(@PathVariable("id") String id, @LoginUser SessionUser user, HttpServletRequest req) {
        BoardService.BoardDetailResult result = boardService.getBoardDetail(id, user);

        req.setAttribute("model", result.board());
        req.setAttribute("isModify", result.isModify());
        return "board/detail";
    }

    /**
     * 게시글 작성 폼 페이지를 반환한다.
     *
     * @return 뷰 이름 "board/save-form"
     */
    @GetMapping("/board/save-form")
    public String saveForm() {
        return "board/save-form";
    }

    /**
     * 게시글 수정 폼 페이지를 반환한다. 작성자 본인만 접근 가능하다.
     *
     * @param id 게시글 ID
     * @param req 요청 객체 (model 속성 주입용)
     * @param user 로그인 사용자 (권한 검증용)
     * @return 뷰 이름 "board/update-form"
     */
    @GetMapping("/board/update-form/{id}")
    public String updateForm(@PathVariable("id") String id, HttpServletRequest req, @LoginUser SessionUser user) {
        BoardReponseDto dto = boardService.getBoardForUpdateForm(id, user);
        req.setAttribute("model", dto);
        return "board/update-form";
    }

    /**
     * 게시글을 등록한다. 로그인 사용자만 가능하다.
     *
     * @param dto 게시글 요청 DTO
     * @param br 바인딩 결과 (검증 실패 시 ValidationHandler에서 ValidationException)
     * @param user 로그인 사용자 (필수)
     * @return 목록 페이지로 리다이렉트
     */
    @ValidateOnError(viewName = "board/save-form")
    @PostMapping("/board/insert")
    public String insert(@Valid BoardRequestDto dto, BindingResult br, @LoginUser SessionUser user) {
        boardService.insert(dto, user);
        return "redirect:/";
    }

    /**
     * 게시글을 수정한다. 작성자 본인만 가능하다.
     *
     * @param boardId 게시글 ID
     * @param reqDto 수정 요청 DTO
     * @param br 바인딩 결과 (검증 실패 시 ValidationHandler에서 ValidationException)
     * @param user 로그인 사용자 (작성자 검증용)
     * @return 상세 페이지로 리다이렉트
     */
    @ValidateOnError(viewName = "board/update-form", pathVariable = "boardId")
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable("id") String boardId, @Valid BoardRequestDto reqDto, BindingResult br,
            @LoginUser SessionUser user) {
        boardService.updateBoardIfOwner(boardId, user, reqDto);
        return "redirect:/board/detail/" + boardId;
    }

    /**
     * 게시글을 삭제한다. 작성자 본인만 가능하다.
     *
     * @param boardId 게시글 ID
     * @param user 로그인 사용자 (작성자 검증용)
     * @return 목록 페이지로 리다이렉트
     */
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable("id") String boardId, @LoginUser SessionUser user) {
        boardService.deleteBoardIfOwner(boardId, user);
        return "redirect:/";
    }
}
