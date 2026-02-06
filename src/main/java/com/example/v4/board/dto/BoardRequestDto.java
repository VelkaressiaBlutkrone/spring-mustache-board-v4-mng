package com.example.v4.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 요청 DTO (Data Transfer Object)
 * 역할: 클라이언트(브라우저)에서 서버로 게시글 작성 또는 수정 데이터를 전송할 때 사용하는 객체입니다.
 * 특징: Entity를 직접 사용하지 않고 별도의 DTO를 두어 입력값 검증(Validation)과 데이터 구조를 분리합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {

    /**
     * 게시글 제목
     * - @NotBlank: null, 빈 문자열(""), 공백(" ")을 허용하지 않습니다.
     * - @Size: 문자열의 길이를 최대 100자로 제한합니다.
     */
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
    public String title;

    /**
     * 게시글 내용
     * - @NotBlank: 필수 입력 항목입니다.
     * - @Size: 최대 1000자까지 입력 가능합니다.
     */
    @NotBlank(message = "내용은 필수입니다.")
    public String content;
}
