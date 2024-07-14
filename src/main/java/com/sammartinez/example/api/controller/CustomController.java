package com.sammartinez.example.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.flogger.Flogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Flogger
public class CustomController extends AbstractBaseController {

  @GetMapping
  public ResponseEntity<String> sayHello() {
    return ok("Hello World");
  }
}
