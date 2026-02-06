package com.example.v4.board.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.v4.reply.dto.ReplyResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 응답 DTO
 * 역할: 서버에서 클라이언트로 데이터를 전달할 때 사용하는 객체입니다.
 * 특징: DB Entity를 직접 노출하지 않고, 화면(View)에 필요한 데이터만 선별하여 전달하기 위해 사용합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardReponseDto {
    // 게시글 고유 ID
    String id;
    // 게시글 제목
    String title;
    // 게시글 내용
    String content;

    String writeId;

    String writeName;

    List<ReplyResponseDto> replies = new ArrayList<>();
}
