package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.dto.NameRequest;
import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.dto.category.CategoryListResponse;
import com.connecter.digitalguiljabiback.dto.category.MoveCategoryRequest;
import com.connecter.digitalguiljabiback.exception.CategoryNameDuplicatedException;
import com.connecter.digitalguiljabiback.exception.category.CategoryNotFoundException;
import com.connecter.digitalguiljabiback.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@Tag(name = "CategoryController", description = "카테고리 관련 API입니다")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class CategoryController {

  private final CategoryService categoryService;

  //[관리자]카테고리 추가
  @PostMapping("/admin/categories")
  public ResponseEntity addCategory(@RequestBody @Valid AddCategoryRequest addCategoryRequest) throws CategoryNotFoundException, CategoryNameDuplicatedException {
    categoryService.add(addCategoryRequest);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  //최상위 카테고리 조회
  @GetMapping("/categories/root")
  public ResponseEntity<CategoryListResponse> getFirstCategory() {
    CategoryListResponse ancestors = categoryService.getAncestorList();

    return ResponseEntity.ok(ancestors);
  }

  //직속 자식 찾기
  @GetMapping("/categories/{categoryPk}/children")
  public ResponseEntity<CategoryListResponse> getChildren(@PathVariable Long categoryPk) throws NoSuchElementException {
    CategoryListResponse ancestors = categoryService.getChildren(categoryPk);

    return ResponseEntity.ok(ancestors);
  }

  //내 조상 모두 찾아 먼 순서대로 내림차순 정렬(나 포함)
  @GetMapping("/categories/{categoryPk}/ancestor")
  public ResponseEntity<CategoryListResponse> getMyAncestor(@PathVariable Long categoryPk) throws NoSuchElementException {
    CategoryListResponse ancestors = categoryService.getMyAncestor(categoryPk);

    return ResponseEntity.ok(ancestors);
  }

  //카테고리 이름만 바꾸기
  @PatchMapping("/categories/{categoryPk}")
  public ResponseEntity<CategoryListResponse> updateCategoryName(@PathVariable Long categoryPk, @RequestBody NameRequest request) throws NoSuchElementException {
    categoryService.editCategoryName(categoryPk, request.getName());

    return ResponseEntity.ok().build();
  }

  //카테고리 이동
  @PatchMapping("/categories/{categoryPk}/move")
  public ResponseEntity<CategoryListResponse> moveCategory(@PathVariable Long categoryPk, @RequestBody MoveCategoryRequest request) throws NoSuchElementException {
    categoryService.moveCategory(categoryPk, request.getParentCategoryPk());

    return ResponseEntity.ok().build();
  }

  //카테고리 삭제
  @DeleteMapping("/categories/{categoryPk}")
  public ResponseEntity deleteCategoryAndDescendant(@PathVariable Long categoryPk) throws NoSuchElementException {
    categoryService.delete(categoryPk);

    return ResponseEntity.ok().build();
  }

}
