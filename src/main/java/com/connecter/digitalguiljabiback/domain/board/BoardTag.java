package com.connecter.digitalguiljabiback.domain.board;

import com.connecter.digitalguiljabiback.domain.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board_tag")
public class BoardTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_pk", referencedColumnName = "pk")
    private Board board;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_pk", referencedColumnName = "pk")
    private Tag tag;

    public static BoardTag makeBoardTag(Board board, Tag tag) {
        BoardTag boardTag = new BoardTag();
        boardTag.board = board;
        boardTag.tag = tag;

        return boardTag;
    }
}
