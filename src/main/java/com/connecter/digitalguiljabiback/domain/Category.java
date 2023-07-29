package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY) // 자기 참조 설정
    @JoinColumn(name = "parent_pk", referencedColumnName = "pk")
    private Category parentCategory;

    @NotNull
    @Column(name = "category_name")
    private String name;

    public static Category makeCategory(Category category, String name) {
        Category newCategory = new Category();

        // 전달받은 category 객체가 영속 상태인지 확인
        if (category != null && Hibernate.isInitialized(category)) {
            newCategory.parentCategory = category;
        } else {
            newCategory.parentCategory = null;
        }

        newCategory.name  = name;
        return newCategory;
    }
}
