package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class RefreshToken {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pk;

  private String refreshToken;

  @ManyToOne
  @JoinColumn(name = "user_pk")
  private Users user;

  public static RefreshToken makeRefreshToken(String refreshToken, Users user) {
    RefreshToken newRefresh = new RefreshToken();
    newRefresh.refreshToken = refreshToken;
    newRefresh.user = user;

    return newRefresh;
  }
}
