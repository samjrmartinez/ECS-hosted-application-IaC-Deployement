package com.sammartinez.example.api.controller;

import lombok.extern.flogger.Flogger;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Flogger
@SpringBootTest(classes = {CustomController.class})
class CustomControllerTest extends AbstractControllerTest {

  @Autowired private CustomController customController;

  @BeforeEach
  void setUp() {
    setUp(customController);
  }
}
