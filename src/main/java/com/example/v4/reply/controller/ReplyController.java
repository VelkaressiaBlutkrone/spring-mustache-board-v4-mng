package com.example.v4.reply.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.v4.global.annotation.LoginUser;
import com.example.v4.global.dto.SessionUser;
import com.example.v4.reply.dto.ReplyRequestDto;
import com.example.v4.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    final ReplyService service;

    @PostMapping("/reply/save")
    public String saveReply(ReplyRequestDto dto, @LoginUser SessionUser user) {
        ReplyService.Result result = service.save(dto, user);
        if (result.isSuccess()) {
            return "redirect:/board/detail/" + result.message();
        }
        return "redirect:/board/detail/" + (dto.getBoardId() != null ? dto.getBoardId() : "");
    }

    @PostMapping("/reply/{id}/delete")
    public String deleteReply(@PathVariable("id") Integer replyId, @LoginUser SessionUser user) {
        ReplyService.Result result = service.delete(replyId, user);
        if (result.isSuccess()) {
            return "redirect:/board/detail/" + result.message();
        }
        return "redirect:/";
    }

}
