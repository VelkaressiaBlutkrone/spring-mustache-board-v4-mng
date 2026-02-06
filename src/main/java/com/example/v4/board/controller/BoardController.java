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
import com.example.v4.global.dto.SessionUser;
import com.example.v4.global.exception.BoardValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {

    final BoardService boardService;

    @GetMapping("/")
    public String board(@LoginUser SessionUser user, HttpServletRequest req) {
        List<BoardReponseDto> models = boardService.list(user);
        req.setAttribute("models", models);
        return "index";
    }

    @GetMapping("/board/detail/{id}")
    public String detail(@PathVariable("id") String id, @LoginUser SessionUser user, HttpServletRequest req) {
        BoardService.BoardDetailResult result = boardService.getBoardDetail(id, user);

        req.setAttribute("model", result.board());
        req.setAttribute("isModify", result.isModify());
        return "board/detail";
    }

    @GetMapping("/board/save-form")
    public String saveForm() {
        return "board/save-form";
    }

    @GetMapping("/board/update-form/{id}")
    public String updateForm(@PathVariable("id") String id, HttpServletRequest req, @LoginUser SessionUser user) {
        BoardReponseDto dto = boardService.getBoardForUpdateForm(id, user);
        req.setAttribute("model", dto);
        return "board/update-form";
    }

    @PostMapping("/board/insert")
    public String insert(@Valid BoardRequestDto dto, BindingResult br, @LoginUser SessionUser user) {
        if (br.hasErrors()) {
            throw new BoardValidationException(br, dto, "board/save-form", null);
        }
        boardService.insert(dto, user);
        return "redirect:/";
    }

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable("id") String boardId, @Valid BoardRequestDto reqDto, BindingResult br,
            @LoginUser SessionUser user) {
        if (br.hasErrors()) {
            throw new BoardValidationException(br, reqDto, "board/update-form", boardId);
        }
        boardService.updateBoardIfOwner(boardId, user, reqDto);
        return "redirect:/board/detail/" + boardId;
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable("id") String boardId, @LoginUser SessionUser user) {
        boardService.deleteBoardIfOwner(boardId, user);
        return "redirect:/";
    }
}
