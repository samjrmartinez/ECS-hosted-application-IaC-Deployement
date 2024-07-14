package com.sammartinez.example.domain.entity.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.sammartinez.example.AbstractBaseTest;
import org.junit.jupiter.api.Test;

class CustomEntityTest extends AbstractBaseTest {

  @Test
  void equalsWithNullIds() {
    var obj1 = CustomEntity.builder().build();
    var obj2 = CustomEntity.builder().build();
    assertEquals(obj1, obj2);
  }

  @Test
  void notEqualsWithMixedGuidelineIds() {
    var obj1 = CustomEntity.builder().id(MOCK_ID).build();
    var obj2 = CustomEntity.builder().build();
    assertNotEquals(obj1, obj2);
  }

  @Test
  void equalsWithGuidelineIds() {
    var obj1 = CustomEntity.builder().id(MOCK_ID).build();
    var obj2 = CustomEntity.builder().id(MOCK_ID).build();
    assertEquals(obj1, obj2);
  }

  @Test
  void notEqualsWithGuidelineIds() {
    var obj1 = CustomEntity.builder().id(MOCK_ID).build();
    var obj2 = CustomEntity.builder().id(-MOCK_ID).build();
    assertNotEquals(obj1, obj2);
  }

  @Test
  void equalsWithSame() {
    var obj1 = CustomEntity.builder().build();
    var obj2 = obj1;
    assertEquals(obj1, obj2);
  }

  @Test
  void notEqualsWithNull() {
    var obj1 = CustomEntity.builder().build();
    var obj2 = (CustomEntity) null;
    assertNotEquals(obj1, obj2);
  }

  @Test
  void notEqualsWithMixed() {
    var obj1 = CustomEntity.builder().build();
    var obj2 = new Object();
    assertNotEquals(obj1, obj2);
  }
}
