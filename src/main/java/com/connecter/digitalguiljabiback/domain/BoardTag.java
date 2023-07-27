package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board_tag")
public class BoardTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_content_pk", referencedColumnName = "pk")
    private BoardContent boardContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_pk", referencedColumnName = "pk")
    private Tag tag;

}
