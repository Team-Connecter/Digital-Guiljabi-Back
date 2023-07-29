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
@RequestMapping("/api/v1/category")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping("/")
  public ResponseEntity addCategory(@RequestBody AddCategoryRequest addCategoryRequest) {
    categoryService.add(addCategoryRequest);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/fisrt")
  public ResponseEntity<CategoryListResponse> getFirstCategory() {
    CategoryListResponse ancestors = categoryService.getAncestorList();

    return ResponseEntity.ok(ancestors);
  }




}
