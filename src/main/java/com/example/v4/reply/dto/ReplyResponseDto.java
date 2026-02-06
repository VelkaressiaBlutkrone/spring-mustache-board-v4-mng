package com.example.v4.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplyResponseDto {
    private Integer id;

    private String comment;

    private Integer replyWriterId;

    private String replyWriterNm;

    private boolean isReplyWriter;
}
