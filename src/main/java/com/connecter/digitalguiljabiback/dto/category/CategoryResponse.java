package com.connecter.digitalguiljabiback.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CategoryResponse {
  private Long pk;
  private String name;
}
