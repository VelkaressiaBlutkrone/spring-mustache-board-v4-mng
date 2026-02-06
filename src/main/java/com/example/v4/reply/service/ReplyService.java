package com.example.v4.reply.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.v4.board.entity.Board;
import com.example.v4.board.repository.BoardRepository;
import com.example.v4.global.dto.SessionUser;
import com.example.v4.reply.dto.ReplyRequestDto;
import com.example.v4.reply.entity.Reply;
import com.example.v4.reply.repository.ReplyRepository;
import com.example.v4.user.entity.User;
import com.example.v4.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {

    final ReplyRepository repository;
    final BoardRepository boardRepository;
    final UserRepository userRepository;

    @Transactional
    public Result save(ReplyRequestDto dto, SessionUser user) {
        if (user == null) {
            return new Result(false, "로그인이 필요합니다.");
        }
        if (dto.getBoardId() == null || dto.getBoardId().isBlank()) {
            return new Result(false, "게시글 정보가 없습니다.");
        }
        if (dto.getComment() == null || dto.getComment().isBlank()) {
            return new Result(false, "댓글 내용을 입력해주세요.");
        }

        Board board = boardRepository.findById(Integer.parseInt(dto.getBoardId())).orElse(null);
        User replyUser = userRepository.findById(user.id()).orElse(null);

        if (board == null || replyUser == null) {
            return new Result(false, "저장에 실패했습니다.");
        }

        Reply reply = new Reply();
        reply.setBoard(board);
        reply.setUser(replyUser);
        reply.setComment(dto.getComment().trim());
        repository.save(reply);

        return new Result(true, dto.getBoardId());
    }

    @Transactional
    public Result delete(Integer replyId, SessionUser user) {
        if (user == null) {
            return new Result(false, null);
        }

        Reply reply = repository.findById(replyId).orElse(null);
        if (reply == null) {
            return new Result(false, null);
        }

        if (!reply.getUser().getId().equals(user.id())) {
            return new Result(false, null);
        }

        String boardId = String.valueOf(reply.getBoard().getId());
        repository.delete(reply);
        return new Result(true, boardId);
    }

    public record Result(boolean isSuccess, String message) {
    }
}
