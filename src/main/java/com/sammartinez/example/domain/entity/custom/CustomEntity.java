package com.sammartinez.example.domain.entity.custom;

import com.sammartinez.example.domain.entity.AbstractEqualsHashCodeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Custom")
public class CustomEntity extends AbstractEqualsHashCodeEntity {

  @Column(name = "CreatedBy")
  private Integer createdBy;

  @Column(name = "Title", nullable = false)
  private String title;

  @Column(name = "Description", nullable = false)
  private String description;

  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
