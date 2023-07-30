package com.connecter.digitalguiljabiback.dto.board;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RejectRequest {
  @NotNull
  private String rejReason;
}
