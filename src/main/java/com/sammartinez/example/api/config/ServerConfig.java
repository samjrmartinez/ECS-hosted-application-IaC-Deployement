package com.sammartinez.example.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class ServerConfig {

  private final RestApiConfiguration restApiConfiguration;

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter() {
    return restApiConfiguration.getCorsFilter();
  }
}
