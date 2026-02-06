package com.example.v4.board.mapper;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.example.v4.board.dto.BoardReponseDto;
import com.example.v4.board.dto.BoardRequestDto;
import com.example.v4.board.entity.Board;
import com.example.v4.global.dto.SessionUser;
import com.example.v4.reply.dto.ReplyResponseDto;

@Component
public class BoardMapper {
    public Board toBoard(Integer boardId, Integer userId, BoardRequestDto dto) {
        return Board.builder()
                .id(boardId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .writerId(userId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public BoardReponseDto toResponseDto(Board board, String writerName, SessionUser su) {
        return new BoardReponseDto(
                String.valueOf(board.getId()),
                board.getTitle(),
                board.getContent(),
                board.getWriterId().toString(),
                writerName,
                board.getReplies().stream().map(reply -> {
                    var user = reply.getUser();
                    return new ReplyResponseDto(
                            reply.getId(),
                            reply.getComment(),
                            user.getId(),
                            user.getUserName(),
                            su == null ? false : user.getId().equals(su.id()));
                }).toList());
    }
}
