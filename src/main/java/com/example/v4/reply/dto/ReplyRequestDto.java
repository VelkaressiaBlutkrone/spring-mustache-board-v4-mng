package com.example.v4.reply.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReplyRequestDto {
    private Integer id;
    private String boardId;
    private String comment;
}
