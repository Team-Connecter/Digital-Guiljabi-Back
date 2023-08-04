package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
public class RefreshToken {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pk;

  @NotNull
  private String refreshToken;

  @NotNull
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
