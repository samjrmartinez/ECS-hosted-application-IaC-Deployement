package com.sammartinez.example;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.MockitoAnnotations.openMocks;

import com.sammartinez.example.api.exception.CustomValidationException;
import lombok.extern.flogger.Flogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@Flogger
public abstract class AbstractBaseTest {

  protected static final Integer MOCK_ID = 1;
  protected static final String MOCK_STRING = "empty";

  protected AutoCloseable mockContext;

  @BeforeEach
  protected void initMocks() {
    mockContext = openMocks(this);
  }

  @AfterEach
  protected void closeMocks() throws Exception {
    mockContext.close();
  }

  protected Exception getMockIncentiveException() {
    return new CustomValidationException(new RuntimeException(EMPTY), EMPTY);
  }
}
