package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Category;
import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.dto.category.CategoryListResponse;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.connecter.digitalguiljabiback.exception.CategoryNameDuplicatedException;
import com.connecter.digitalguiljabiback.exception.category.CategoryNotFoundException;
import com.connecter.digitalguiljabiback.repository.CategoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {
  
  private final CategoryRepository categoryRepository;
  private final EntityManager em;

  //카테고리 추가
  public Category add(AddCategoryRequest addCategoryRequest) throws CategoryNotFoundException, CategoryNameDuplicatedException {
    String name = addCategoryRequest.getName();
    Long parentPk = addCategoryRequest.getParentCategoryPk();

    //같은 부모 밑에 같은 이름의 카테고리가 있는지 확인
    if (parentPk != null) {
      //해당하는 카테고리가 있는지 확인
      categoryRepository.findById(parentPk)
        .orElseThrow(() -> new CategoryNotFoundException("해당하는 부모 카테고리가 없습니다"));

      //같은 레벨에 같은 이름의 카테고리가 존재하는지 확인
      for (Category c: categoryRepository.findChildren(parentPk)) {
        if (c.getName().equals(name))
          throw new CategoryNameDuplicatedException("동일한 이름의 카테고리가 같은 레벨에 존재합니다");
      }

    } else {
      //최상위 카테고리에 중복name있는지 확인
      for (Category fa: categoryRepository.findFirstAncestor()) {
        if (fa.getName().equals(name))
          throw new CategoryNameDuplicatedException("동일한 이름의 카테고리가 같은 레벨에 존재합니다");
      }
    }

    //카테고리 추가
    Category category = Category.makeCategory(name);
    Category savedCategory = addNewCategory(category, parentPk);

    return savedCategory;
  }

  private Category addNewCategory(Category category, Long parentPk) {
    Category savedCategory = categoryRepository.save(category);

    connectGraphInsert(savedCategory.getPk(), parentPk);

    return savedCategory;
  }

  //카테고리 추가했을 때 그래프 연결
  private void connectGraphInsert(Long currPk, Long parentPk) {

    //parent가 있으면 거기 밑으로 연결
    if (parentPk != null)
      categoryRepository.addConnect(currPk, parentPk);
    else
      categoryRepository.addConnect(currPk, currPk); //부모가 null이면 가장 최상위로 연결
  }

  public CategoryListResponse getAncestorList() {
    List<Category> ancestor = categoryRepository.findFirstAncestor();

    List<CategoryResponse> responseList = convertDto(ancestor);

    CategoryListResponse listDto = CategoryListResponse.builder()
      .cnt(responseList.size())
      .list(responseList)
      .build();

    return listDto;
  }

  private List<CategoryResponse> convertDto(List<Category> categoryList) {
    List<CategoryResponse> dto = new ArrayList<>();
    for (Category category: categoryList) {
      CategoryResponse response = CategoryResponse.builder()
        .pk(category.getPk())
        .name(category.getName())
        .build();

      dto.add(response);
    }

    return dto;
  }

  //직속 자식 찾기
  @Transactional(readOnly = true)
  public CategoryListResponse getChildren(Long categoryPk) throws CategoryNotFoundException {
    categoryRepository.findById(categoryPk).orElseThrow(() -> new CategoryNotFoundException("해당하는 pk의 카테고리가 존재하지 않습니다"));

    List<Category> children = categoryRepository.findChildren(categoryPk);

    List<CategoryResponse> responseList = convertDto(children);

    CategoryListResponse listDto = CategoryListResponse.builder()
      .cnt(responseList.size())
      .list(responseList)
      .build();

    return listDto;
  }

  //내 모든 조상 찾기(나 포함)
  @Transactional(readOnly = true)
  public CategoryListResponse getMyAncestor(Long categoryPk) throws CategoryNotFoundException {
    categoryRepository.findById(categoryPk).orElseThrow(() -> new CategoryNotFoundException("해당하는 카테고리가 존재하지 않습니다"));

    List<Category> children = categoryRepository.findMyAncestor(categoryPk)
      .orElseGet(() -> new ArrayList<>());

    List<CategoryResponse> responseList = convertDto(children);

    CategoryListResponse listDto = CategoryListResponse.builder()
      .cnt(responseList.size())
      .list(responseList)
      .build();

    return listDto;
  }

  //name은 절대 null이거나 ""면 안됨
  public Category editCategoryName(Long categoryPk, String name) {
    Category category = categoryRepository.findById(categoryPk)
      .orElseThrow(() -> new CategoryNotFoundException("해당하는 pk의 카테고리가 존재하지 않습니다"));

    category.updateName(name);

    return category;
  }
  
  //자식까지 싹 이동
  public void moveCategory(Long categoryPk, Long parentPk) throws CategoryNameDuplicatedException, CategoryNotFoundException {
    Category category = categoryRepository.findById(categoryPk)
      .orElseThrow(() -> new CategoryNotFoundException("해당하는 pk의 카테고리가 존재하지 않습니다"));

    //같은 부모 밑에 같은 이름의 카테고리가 있는지 확인
    if (parentPk != null) {
      //해당하는 카테고리가 있는지 확인
      categoryRepository.findById(parentPk)
        .orElseThrow(() -> new CategoryNotFoundException("해당하는 부모 카테고리가 없습니다"));

      //같은 레벨에 같은 이름의 카테고리가 존재하는지 확인
      for (Category c: categoryRepository.findChildren(parentPk)) {
        if (c.getName().equals(category.getName()))
          throw new CategoryNameDuplicatedException("동일한 이름의 카테고리가 같은 레벨에 존재합니다");
      }

    } else {
      //최상위 카테고리에 중복name있는지 확인
      for (Category fa: categoryRepository.findFirstAncestor()) {
        if (fa.getName().equals(category.getName()))
          throw new CategoryNameDuplicatedException("동일한 이름의 카테고리가 같은 레벨에 존재합니다");
      }
    }

    categoryRepository.updateConnect(categoryPk, parentPk);
  }

  //자식까지 삭 삭제
  public void delete(Long categoryPk) throws CategoryNotFoundException {
    categoryRepository.findById(categoryPk)
      .orElseThrow(() -> new CategoryNotFoundException("해당하는 pk의 카테고리가 존재하지 않습니다"));

    categoryRepository.deleteConnect(categoryPk);

    em.flush();
    em.clear();
  }
}
