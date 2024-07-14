package com.sammartinez.example.api.config;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.text.StringEscapeUtils.escapeCsv;
import static org.apache.commons.text.StringEscapeUtils.escapeEcmaScript;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml3;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;
import static org.apache.commons.text.StringEscapeUtils.escapeXSI;
import static org.apache.commons.text.StringEscapeUtils.escapeXml10;
import static org.apache.commons.text.StringEscapeUtils.escapeXml11;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

import com.sammartinez.example.api.exception.CustomValidationException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.flogger.Flogger;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Flogger
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final HttpServletRequest request;

  public static final String GENERIC_MESSAGE_FORMAT =
      "Oops! Error! Issue was logged, reported and will be reviewed.";

  private static final String CONTROLLER_EXCEPTION_MESSAGE_FORMAT =
      "API Exception: %s, IP: %s, Method: %s, URL: %s, Headers: %s, Referrer: %s";

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(value = {Exception.class})
  String unknownException(Exception ex, ServletWebRequest webRequest) {
    logMessageWithCause(ex, webRequest);
    return GENERIC_MESSAGE_FORMAT;
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(value = {CustomValidationException.class})
  ResponseEntity<CustomValidationException> handleIncentiveValidationException(
          CustomValidationException validationException, ServletWebRequest webRequest) {
    return handleReturnObjectException(validationException, webRequest, BAD_REQUEST);
  }

  @ResponseStatus(NOT_ACCEPTABLE)
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex,
      @Nullable HttpHeaders headers,
      @Nullable HttpStatusCode status,
      @Nullable WebRequest request) {
    var message = extractValidationExceptionMessages(ex);
    var validationException = new CustomValidationException(ex, message);
    return new ResponseEntity<>(
        new CustomValidationException(validationException, message), NOT_ACCEPTABLE);
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
    logMessageWithCause(validationException, webRequest);

    return new ResponseEntity<>(
        new CustomValidationException(validationException, message), status);
  }

  private void logMessageWithCause(Exception ex, ServletWebRequest webRequest) {
    log.atSevere().withCause(ex).log(
        CONTROLLER_EXCEPTION_MESSAGE_FORMAT,
        ex.getMessage(),
        getIpAddressFromRequest(),
        webRequest.getHttpMethod(),
        webRequest.getRequest().getRequestURI(),
        getHeaderInformation(webRequest),
        webRequest.getRequest().getRemoteHost());
  }

  private String getIpAddressFromRequest() {
    final String[] proxyHeaderKeys = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
    };

    for (String header : proxyHeaderKeys) {
      var clientIP = getIpFromProxyHeader(header);
      if (clientIP != null) {
        return clientIP;
      }
    }
    return escapeAll(request.getRemoteAddr());
  }

  private String getIpFromProxyHeader(String header) {
    String ip = request.getHeader(header);
    return (isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) ? escapeAll(ip) : null;
  }

  private String getHeaderInformation(ServletWebRequest webRequest) {
    var headerNameValue = new StringBuilder();
    webRequest
        .getHeaderNames()
        .forEachRemaining(name -> headerNameValue.append(getNameAndValue(webRequest, name)));
    return headerNameValue.toString();
  }

  private String getNameAndValue(ServletWebRequest webRequest, String name) {
    return !name.equalsIgnoreCase("Authorization")
        ? format("{\"%s\": \"%s\" }", name, webRequest.getHeader(name))
        : EMPTY;
  }

  public String escapeAll(String dirty) {
    if (dirty == null) {
      return EMPTY;
    }
    dirty = escapeCsv(dirty);
    dirty = escapeEcmaScript(dirty);
    dirty = escapeHtml3(dirty);
    dirty = escapeHtml4(dirty);
    dirty = escapeJava(dirty);
    dirty = escapeJson(dirty);
    dirty = escapeXml10(dirty);
    dirty = escapeXml11(dirty);
    dirty = escapeXSI(dirty);
    return dirty;
  }
}
