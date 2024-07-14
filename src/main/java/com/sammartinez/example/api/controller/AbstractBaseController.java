package com.sammartinez.example.api.controller;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.sammartinez.example.api.exception.CustomValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

public abstract class AbstractBaseController {

  protected ResponseEntity<Void> ok() {
    return ResponseEntity.status(OK).build();
  }

  protected <T> ResponseEntity<T> ok(T value) {
    return ok(value, null);
  }

  protected <T> ResponseEntity<T> ok(T value, @Nullable HttpHeaders responseHeaders) {
    return Optional.ofNullable(responseHeaders)
        .map(headers -> ResponseEntity.status(OK).headers(responseHeaders).body(value))
        .orElse(ResponseEntity.status(OK).body(value));
  }

  protected <T> ResponseEntity<T> conflict(T value) {
    return ResponseEntity.status(CONFLICT).body(value);
  }

  protected ResponseEntity<Void> conflict() {
    return ResponseEntity.status(CONFLICT).build();
  }

  protected ResponseEntity<Map<String, Object>> validationError(
          CustomValidationException validationException) {
    return validationError(validationException.getLocalMessage(), validationException.getMessage());
  }

  protected ResponseEntity<Map<String, Object>> validationError(String error) {
    return validationError(null, error);
  }

  protected ResponseEntity<Map<String, Object>> validationError(
      @Nullable String code, String error) {
    var errorMap = new HashMap<String, Object>();
    errorMap.put("error", error);
    if (isNoneBlank(code)) {
      errorMap.put("code", code);
    }
    return badRequest(errorMap);
  }

  protected ResponseEntity<Void> badRequest() {
    return ResponseEntity.status(BAD_REQUEST).build();
  }

  protected <T> ResponseEntity<T> badRequest(T value) {
    return ResponseEntity.status(BAD_REQUEST).body(value);
  }

  protected <T> ResponseEntity<T> notFound(T value) {
    return ResponseEntity.status(NOT_FOUND).body(value);
  }
}
