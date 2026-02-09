package com.example.v4.reply.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.v4.board.entity.Board;
import com.example.v4.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "reply_tb")
@NoArgsConstructor
public class Reply {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String comment;

    @ToString.Exclude
    @ManyToOne
    private Board board;

    @ToString.Exclude
    @ManyToOne
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
