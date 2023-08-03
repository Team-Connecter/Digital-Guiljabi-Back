package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Category;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.dto.category.CategoryListResponse;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.connecter.digitalguiljabiback.exception.CategoryNameDuplicatedException;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryServiceTest {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private BoardService boardService;
  @Autowired
  private CategoryService categoryService;

  private final String name1 = "카테고리1";
  private final String name2 = "카테고리2";
  private final String name3 = "카테고리3";
  private final String name4 = "카테고리4";

  @BeforeAll
  void setUp() {

  }
  
  //카테고리 등록 테스트(조상)
  @DisplayName("카테고리 생성")
  @Transactional
  @Order(1)
  @Test
  void createCategory() {
    Category category = categoryService.add(new AddCategoryRequest(name1, null));

    CategoryListResponse ancestorList = categoryService.getAncestorList();

    //조상목록에 조회되는지 확인
    boolean isAncestor = false;
    for (CategoryResponse cr: ancestorList.getList()) {
      if (cr.getName() == name1) {
        isAncestor = true;
        break;
      }
    }

    assertTrue(isAncestor);

    //조상이 있는지 확인 - 조상이 없어야함, 나만 조회되어야함
    CategoryListResponse myAncestor = categoryService.getMyAncestor(category.getPk());

    assertEquals(1, myAncestor.getList().size());
  }

  //같은 이름의 카테고리 생성 테스트 - conflict 발생
  @DisplayName("같은 이름의 카테고리 생성")
  @Transactional
  @Order(2)
  @Test
  void duplicatedNameCategory() {
    categoryService.add(new AddCategoryRequest(name1, null));

    assertThrows(CategoryNameDuplicatedException.class, () -> {
      categoryService.add(new AddCategoryRequest(name1, null));
    });
  }
  
  //카테고리 부모 밑에 등록 + 부모조회
  @DisplayName("자식 카테고리 등록 테스트")
  @Transactional
  @Order(3)
  @Test
  void addChildCategory() {
    Category parent = categoryService.add(new AddCategoryRequest(name1, null));
    Category child = categoryService.add(new AddCategoryRequest(name2, parent.getPk()));

    //부모의 직속 자식 카테고리 조회
    CategoryListResponse children = categoryService.getChildren(parent.getPk());
    boolean isChild = false;
    for (CategoryResponse cr: children.getList()) {
      if (cr.getName() == name2) {
        isChild = true;
        break;
      }
    }

    assertTrue(isChild);

    //자식의 부모 카테고리 조회
    CategoryListResponse myAncestor = categoryService.getMyAncestor(child.getPk());
    boolean isParent = false;
    for (CategoryResponse cr: myAncestor.getList()) {
      if (cr.getName() == name1) {
        isParent = true;
        break;
      }
    }

    assertTrue(isParent);

  }

  //카테고리 이름 수정 테스트
  @DisplayName("카테고리 이름(만) 수정 테스트")
  @Transactional
  @Order(4)
  @Test
  void editCategoryName() {
    Category category = categoryService.add(new AddCategoryRequest(name1, null));
    categoryService.editCategoryName(category.getPk(), name2);

    CategoryListResponse ancestorList = categoryService.getAncestorList();

    boolean isChangedName = false;
    for (CategoryResponse cr: ancestorList.getList()) {
      String categoryName = cr.getName();
      assertNotEquals(name1, categoryName);

      if (categoryName.equals(name2))
        isChangedName = true;
    }

    assertTrue(isChangedName);
  }


  //카테고리 이동 테스트
  @DisplayName("카테고리 이동 테스트")
  @Transactional
  @Order(5)
  @Test
  void moveCategoryName() {
    // -- category1 - category3
    // |
    // -- category2 - category4
    Category category1 = categoryService.add(new AddCategoryRequest(name1, null));
    Category category2 = categoryService.add(new AddCategoryRequest(name2, null));
    Category category3 = categoryService.add(new AddCategoryRequest(name3, category1.getPk()));
    Category category4 = categoryService.add(new AddCategoryRequest(name4, category2.getPk()));

    //[1] 1을 4 밑으로 이동시킬거임
    categoryService.moveCategory(category1.getPk(), category4.getPk());
    
    //1의 조상이 2, 4인지 확인
    CategoryListResponse myAncestor = categoryService.getMyAncestor(category1.getPk());
    boolean isPerant2 = false;
    boolean isPerant4 = false;
    for (CategoryResponse cr: myAncestor.getList()) {
      if (cr.getPk() == category2.getPk())
        isPerant2 = true;

      if (cr.getPk() == category4.getPk())
        isPerant4 = true;
    }

    assertTrue(isPerant2);
    assertTrue(isPerant4);

    //1의 자손이 3인지 확인
    CategoryListResponse children = categoryService.getChildren(category1.getPk());
    boolean isChild3 = false;
    for (CategoryResponse cr: children.getList()) {
      if (cr.getPk() == category3.getPk()) {
        isChild3 = true;
        break;
      }
    }
    assertTrue(isChild3);


    //[2] 1을 다시 조상으로 보낼거임
    categoryService.moveCategory(category1.getPk(), null);
    
    //1이 최고조상인지 확인
    CategoryListResponse ancestorList = categoryService.getAncestorList();
    boolean isAncestor = false;
    for (CategoryResponse cr: ancestorList.getList()) {
      if (cr.getPk() == category1.getPk()) {
        isAncestor = true;
        break;
      }
    }
    assertTrue(isAncestor);
    
    //1의 자손이 3인지 확인
    children = categoryService.getChildren(category1.getPk());
    isChild3 = false;
    for (CategoryResponse cr: children.getList()) {
      if (cr.getPk() == category3.getPk()) {
        isChild3 = true;
        break;
      }
    }
    assertTrue(isChild3);

    //4의 자손에 1이 없는지 확인
    children = categoryService.getChildren(category4.getPk());
    boolean isChild1 = false;
    for (CategoryResponse cr: children.getList()) {
      if (cr.getPk() == category1.getPk()) {
        isChild3 = true;
        break;
      }
    }
    assertFalse(isChild1);

  }
  
  //카테고리 삭제 테스트
  
  //카테고리 이등 테스트

}