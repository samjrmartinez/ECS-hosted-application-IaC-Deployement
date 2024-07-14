package com.sammartinez.example.service.custom;

import com.sammartinez.example.service.AbstractServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

class CustomServiceTest extends AbstractServiceTest {

  @InjectMocks
  CustomService customService;

  @BeforeEach
  protected void setUp() {
    super.setUp(customService);
  }

}
