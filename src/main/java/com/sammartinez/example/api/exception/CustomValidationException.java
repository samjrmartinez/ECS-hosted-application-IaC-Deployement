package com.sammartinez.example.api.exception;

import java.io.Serial;
import java.util.function.Supplier;
import lombok.Getter;

@Getter
public class CustomValidationException extends RuntimeException {

  @Serial private static final long serialVersionUID = -2253240759648422722L;
  private final String localMessage;

  public CustomValidationException(Throwable exception, String localMessage) {
    super(exception.getMessage(), exception.getCause());
    this.localMessage = localMessage;
  }

  public static void wrapValidation(Runnable runnable, String localMessage) {
    wrapValidation(
        () -> {
          runnable.run();
          return null;
        },
        localMessage);
  }

  public static <T> void wrapValidation(Supplier<T> supplier, String localMessage) {
    try {
      supplier.get();
    } catch (RuntimeException e) {
      throw new CustomValidationException(e, localMessage);
    }
  }
}
