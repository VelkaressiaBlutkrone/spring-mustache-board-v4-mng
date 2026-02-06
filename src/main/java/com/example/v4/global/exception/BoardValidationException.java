package com.example.v4.global.exception;

import org.springframework.validation.BindingResult;

import com.example.v4.board.dto.BoardRequestDto;

import lombok.Getter;

/**
 * Board 폼 검증 실패 시 사용하는 예외.
 * BindingResult, 폼 데이터, 뷰 정보를 담아 GlobalExceptionHandler에서 처리한다.
 */
@Getter
public class BoardValidationException extends RuntimeException {

    private final BindingResult bindingResult;
    private final BoardRequestDto dto;
    private final String viewName;
    private final String boardId;

    public BoardValidationException(BindingResult bindingResult, BoardRequestDto dto, String viewName, String boardId) {
        super("검증 실패: " + bindingResult.getAllErrors());
        this.bindingResult = bindingResult;
        this.dto = dto;
        this.viewName = viewName;
        this.boardId = boardId;
    }
}
