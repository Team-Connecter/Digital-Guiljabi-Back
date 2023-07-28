package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board_content")
public class BoardContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_pk", referencedColumnName = "pk")
    private Board board;

    private String title;


    @Column(name = "img_url")
    private String imgUrl;

    private String content;


    public static BoardContent makeBoardContent(Board board, String title, String imgUrl, String content) {
        BoardContent boardContent = new BoardContent();
        boardContent.board = board;
        boardContent.title = title;
        boardContent.imgUrl = imgUrl;
        boardContent.content = content;

        return boardContent;
    }
}
