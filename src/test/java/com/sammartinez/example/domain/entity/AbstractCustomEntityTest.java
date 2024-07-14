package com.sammartinez.example.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.sammartinez.example.AbstractBaseTest;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;

class AbstractCustomEntityTest extends AbstractBaseTest {

  @Test
  void equalsWithNullIds() {
    var obj1 = TestCustomEntity.builder().build();
    var obj2 = TestCustomEntity.builder().build();
    assertEquals(obj1, obj2);
  }

  @Test
  void equalsWithSame() {
    var obj1 = TestCustomEntity.builder().build();
    var obj2 = obj1;
    assertEquals(obj1, obj2);
  }

  @SuperBuilder
  static class TestCustomEntity extends AbstractCustomEntity {}
}
