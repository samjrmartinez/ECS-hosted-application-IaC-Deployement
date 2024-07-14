package com.sammartinez.example.api.controller;

import static com.sammartinez.example.api.config.GlobalExceptionHandler.GENERIC_MESSAGE_FORMAT;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sammartinez.example.AbstractBaseTest;
import java.util.stream.Collectors;

import com.sammartinez.example.api.exception.CustomValidationException;
import org.junit.runner.RunWith;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

@RunWith(SpringRunner.class)
abstract class AbstractControllerTest extends AbstractBaseTest {

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper()
          .findAndRegisterModules()
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  protected static final String MESSAGE_TAG_NAME = "$.localMessage";

  protected MockMvc mockMvc;

  protected void setUp(final AbstractBaseController controllerUnderTest) {
    assertNotNull(controllerUnderTest);
    mockMvc =
        standaloneSetup(controllerUnderTest)
            .defaultRequest(get("/").accept(APPLICATION_JSON).contentType(APPLICATION_JSON))
            .defaultRequest(put("/").accept(APPLICATION_JSON).contentType(APPLICATION_JSON))
            .defaultRequest(post("/").accept(APPLICATION_JSON).contentType(APPLICATION_JSON))
            .setControllerAdvice(ControllerExceptionHandler.class)
            .setMessageConverters(
                new StringHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter(OBJECT_MAPPER))
            .build();
  }

  @RestControllerAdvice
  private static class ControllerExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    String unknownException(Exception ex, ServletWebRequest webRequest) {
      return GENERIC_MESSAGE_FORMAT;
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(value = {CustomValidationException.class})
    ResponseEntity<CustomValidationException> handleIncentiveValidationException(
            CustomValidationException validationException, ServletWebRequest webRequest) {
      return handleReturnObjectException(validationException, webRequest, BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomValidationException> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
      var message = extractValidationExceptionMessages(ex);
      var validationException = new CustomValidationException(ex, message);
      return new ResponseEntity<>(
          new CustomValidationException(validationException, message),
          HttpStatus.NOT_ACCEPTABLE);
    }

    private String extractValidationExceptionMessages(MethodArgumentNotValidException ex) {
      return ex.getBindingResult().getAllErrors().stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.joining(";"));
    }

    private ResponseEntity<CustomValidationException> handleReturnObjectException(
            CustomValidationException validationException,
        ServletWebRequest webRequest,
        HttpStatus status) {
      String message = requireNonNull(validationException.getLocalMessage());

      return new ResponseEntity<>(
          new CustomValidationException(validationException, message), status);
    }
  }
}
