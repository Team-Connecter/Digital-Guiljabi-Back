package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Category;
import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.dto.category.CategoryListResponse;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.connecter.digitalguiljabiback.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryService {
  
  private final CategoryRepository categoryRepository;

  @Transactional
  public void add(AddCategoryRequest addCategoryRequest) {
    String name = addCategoryRequest.getName();
    Long parentPk = addCategoryRequest.getParentCategoryPk();
    log.info("@@name: " + name);


    //같은 부모 밑에 같은 이름의 카테고리가 있는지 확인
    //@TODO
    
    //카테고리 추가
    Category category = Category.makeCategory(name);
    Category saved = addNewCategory(category, parentPk);
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
    List<Category> ancestor = categoryRepository.findFirstAncestor()
      .orElseGet(() -> new ArrayList<>());

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
  public CategoryListResponse getChildren(Long categoryPk) {
    List<Category> children = categoryRepository.findChildren(categoryPk)
      .orElseGet(() -> new ArrayList<>());

    List<CategoryResponse> responseList = convertDto(children);

    CategoryListResponse listDto = CategoryListResponse.builder()
      .cnt(responseList.size())
      .list(responseList)
      .build();

    return listDto;
  }

  //내 모든 조상 찾기
  public CategoryListResponse getMyAncestor(Long categoryPk) {
    List<Category> children = categoryRepository.findMyAncestor(categoryPk)
      .orElseGet(() -> new ArrayList<>());

    List<CategoryResponse> responseList = convertDto(children);

    CategoryListResponse listDto = CategoryListResponse.builder()
      .cnt(responseList.size())
      .list(responseList)
      .build();

    return listDto;
  }
}
