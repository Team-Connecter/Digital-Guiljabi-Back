package com.connecter.digitalguiljabiback.dto.category;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCategoryRequest {
  @NotNull
  private String name;
  
  //null가능 - null이면 가장 최상위 부모
  private Long parentCategoryPk;
}
