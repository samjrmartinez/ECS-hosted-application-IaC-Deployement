package com.sammartinez.example.domain.entity;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuperBuilder
@AllArgsConstructor
public class AbstractEqualsHashCodeEntity extends AbstractCustomEntity {

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    } else if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    return new EqualsBuilder().appendSuper(super.equals(obj)).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().appendSuper(super.hashCode()).toHashCode();
  }
}
