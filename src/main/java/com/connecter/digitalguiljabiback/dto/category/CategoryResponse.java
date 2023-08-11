package com.connecter.digitalguiljabiback.dto.category;

import com.connecter.digitalguiljabiback.domain.board.BoardCategory;
import com.connecter.digitalguiljabiback.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CategoryResponse {
  private Long pk;
  private String name;

  public static List<CategoryResponse> convert(List<BoardCategory> boardCategories) {
      List<CategoryResponse> responses = new ArrayList<>();

      for(BoardCategory boardCategory: boardCategories) {
        Category category = boardCategory.getCategory();

        responses.add(
          CategoryResponse.builder()
            .name(category.getName())
            .pk(category.getPk())
            .build()
        );
      }

      return responses;
  }
}
