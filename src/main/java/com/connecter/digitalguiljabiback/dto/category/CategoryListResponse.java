package com.connecter.digitalguiljabiback.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CategoryListResponse {
  private int cnt;
  private List<CategoryResponse> list;
}
