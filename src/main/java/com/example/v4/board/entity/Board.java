package com.example.v4.board.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import com.example.v4.reply.entity.Reply;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "board_tb")
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer writerId;

    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    @OrderBy("id DESC")
    private final List<Reply> replies = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
}
