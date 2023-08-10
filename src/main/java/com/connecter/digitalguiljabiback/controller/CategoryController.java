package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.dto.NameRequest;
import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.dto.category.CategoryListResponse;
import com.connecter.digitalguiljabiback.dto.category.MoveCategoryRequest;
import com.connecter.digitalguiljabiback.exception.CategoryNameDuplicatedException;
import com.connecter.digitalguiljabiback.exception.category.CategoryNotFoundException;
import com.connecter.digitalguiljabiback.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@Tag(name = "카테고리", description = "카테고리 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class CategoryController {

  private final CategoryService categoryService;

  @Operation(summary = "카테고리 추가", description = """
    [관리자]<br>
    parentCategoryPk는 추가하는 카테고리의 부모 카테고리입니다. 최상위 카테고리로 만들고싶다면 parentCategoryPk를 null로 넣어주세요.<br>
    201: 성공<br>
    403: 권한없음<br>
    404: 해당 pk의 부모 카테고리가 존재하지 않음<br>
    409: 같은 레벨에 같은 이름의 카테고리가 존재함
    """)
  @PostMapping("/admin/categories")
  public ResponseEntity addCategory(@RequestBody @Valid AddCategoryRequest addCategoryRequest) throws CategoryNotFoundException, CategoryNameDuplicatedException {
    categoryService.add(addCategoryRequest);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
  
  @Operation(summary = "최상위 카테고리 조회", description = """
    [모두 접근가능]<br>200: 성공
    """)
  @GetMapping("/categories/root")
  public ResponseEntity<CategoryListResponse> getFirstCategory() {
    CategoryListResponse ancestors = categoryService.getAncestorList();

    return ResponseEntity.ok(ancestors);
  }

  @Operation(summary = "직속 자식 카테고리 조회", description = """
    [모두 접근가능] 현재 카테고리의 직속 자식 카테고리를 조회합니다.<br>
    바로 아래 레벨의 카테고리들만 조회됩니다. 손주 x 아들 ㅇ<br>
    200: 성공<br>
    404: 해당 pk의 부모 카테고리가 존재하지 않음
    """)
  @GetMapping("/categories/{categoryPk}/children")
  public ResponseEntity<CategoryListResponse> getChildren(@PathVariable Long categoryPk) throws NoSuchElementException {
    CategoryListResponse ancestors = categoryService.getChildren(categoryPk);

    return ResponseEntity.ok(ancestors);
  }

  @Operation(summary = "내 조상 카테고리 모두 조회", description = """
    [모두 접근가능] 현재 카테고리의 조상을 모두 조회하여 먼 순서대로 내림차순으로 정렬합니다.(나 포함)<br>
    200: 성공<br>
    404: 해당 pk의 카테고리가 존재하지 않음
    """)
  @GetMapping("/categories/{categoryPk}/ancestor")
  public ResponseEntity<CategoryListResponse> getMyAncestor(@PathVariable Long categoryPk) throws NoSuchElementException {
    CategoryListResponse ancestors = categoryService.getMyAncestor(categoryPk);

    return ResponseEntity.ok(ancestors);
  }

  @Secured("ADMIN")
  @Operation(summary = "카테고리 이름 바꾸기", description = """
    [관리자] 현재 카테고리의 이름을 바꿉니다. 부모, 자식 관계는 그대로 유지됩니다.<br>
    200: 성공<br>
    404: 해당 pk의 카테고리가 존재하지 않음
    """)
  @PatchMapping("/categories/{categoryPk}")
  public ResponseEntity<CategoryListResponse> updateCategoryName(@PathVariable Long categoryPk, @RequestBody NameRequest request) throws NoSuchElementException {
    categoryService.editCategoryName(categoryPk, request.getName());

    return ResponseEntity.ok().build();
  }

  @Secured("ADMIN")
  @Operation(summary = "카테고리 이동", description = """
    [관리자] 현재 카테고리를 이동합니다. 하위카테고리(자손)은 그대로 유지하고, 부모만 변경합니다.<br>
    최상위 카테고리로 만들고 싶다면 parent를 null로 주면 됩니다<br>
    200: 성공<br>
    404: 해당 pk의 카테고리 or 부모 카테고리가 존재하지 않음
    409: 같은 레벨에 같은 이름의 카테고리가 존재함
    """)
  @PatchMapping("/categories/{categoryPk}/move")
  public ResponseEntity<CategoryListResponse> moveCategory(@PathVariable Long categoryPk, @RequestBody MoveCategoryRequest request) throws NoSuchElementException {
    categoryService.moveCategory(categoryPk, request.getParentCategoryPk());

    return ResponseEntity.ok().build();
  }

  @Secured("ADMIN")
  @Operation(summary = "카테고리 삭제", description = """
    [관리자] 현재 카테고리를 삭제합니다. 딸린 자손들도 모두 삭제되므로 주의하세요!<br>
    200: 성공<br>
    404: 해당 pk의 카테고리 or 부모 카테고리가 존재하지 않음
    """)
  @DeleteMapping("/categories/{categoryPk}")
  public ResponseEntity deleteCategoryAndDescendant(@PathVariable Long categoryPk) throws NoSuchElementException {
    categoryService.delete(categoryPk);

    return ResponseEntity.ok().build();
  }

}
