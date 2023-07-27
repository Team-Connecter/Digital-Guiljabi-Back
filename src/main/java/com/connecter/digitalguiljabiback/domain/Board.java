package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private Users user;

    @NotNull
    @CreationTimestamp
    @Column(name = "create_datetime")
    @Builder.Default
    private LocalDate createDatetime = LocalDate.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private BoardStatus status = BoardStatus.WAITING;

    @NotNull
    private String title;

    @CreationTimestamp
    @Column(name = "update_datetime")
    private LocalDate updateDatetime;

    @NotNull
    @Builder.Default
    @Column(name = "thumbnail_url", length = 99999)
    private String thumbnailUrl = null;

    @Lob
    @Column(length = 999999999)
    private String introduction;

    private String source;

    private String reason;

    @NotNull
    @Builder.Default
    @Column(name = "report_cnt")
    private Long reportCnt = 0L;

    @NotNull
    @Builder.Default
    @Column(name = "like_cnt")
    private Long likeCnt = 0L;

    @NotNull
    @Builder.Default
    @Column(name = "bookmark_cnt")
    private Long bookmarkCnt = 0L;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardContent> contents = new ArrayList<>();


}
