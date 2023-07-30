package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.dto.category.CategoryListResponse;
import com.connecter.digitalguiljabiback.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "CategoryController", description = "카테고리 관련 API입니다")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping("/admin")
  public ResponseEntity addCategory(@RequestBody AddCategoryRequest addCategoryRequest) {
    categoryService.add(addCategoryRequest);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/categories/root")
  public ResponseEntity<CategoryListResponse> getFirstCategory() {
    CategoryListResponse ancestors = categoryService.getAncestorList();

    return ResponseEntity.ok(ancestors);
  }

  @GetMapping("/{categoryPk}/children") //직속 자식 찾기
  public ResponseEntity<CategoryListResponse> getChildren(@PathVariable Long categoryPk) {
    CategoryListResponse ancestors = categoryService.getChildren(categoryPk);

    return ResponseEntity.ok(ancestors);
  }

  @GetMapping("/{categoryPk}/ancestor") //내 조상 모두 찾아 먼 순서대로 내림차순 정렬(나 포함 ㄴㄴ)
  public ResponseEntity<CategoryListResponse> getMyAncestor(@PathVariable Long categoryPk) {
    CategoryListResponse ancestors = categoryService.getMyAncestor(categoryPk);

    return ResponseEntity.ok(ancestors);
  }


}
