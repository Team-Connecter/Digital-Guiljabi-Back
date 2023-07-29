package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "clo_catogory")
public class CloCategory {
  @Id
  @ManyToOne
  @JoinColumn(name = "ancestor_pk", referencedColumnName = "pk")
  private Category ancestor;

  @Id
  @ManyToOne
  @JoinColumn(name = "descendant_pk", referencedColumnName = "pk")
  private Category descendant;

  private int depth;
}
