package com.connecter.digitalguiljabiback.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCategoryRequest {
  private String name;
  
  //null가능 - null이면 가장 최상위 부모
  private Long parentCategoryPk;
}
