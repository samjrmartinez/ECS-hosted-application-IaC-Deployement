package com.sammartinez.example.api.config;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Service
@RequiredArgsConstructor
public class RestApiConfiguration {

  private final Environment environment;

  private List<String> getOriginList() {
    return Arrays.asList(environment.getRequiredProperty("spring.webapp.cors.origins").split(";"));
  }

  public FilterRegistrationBean<CorsFilter> getCorsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOriginPatterns(getOriginList());
    config.addAllowedHeader("*");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("HEAD");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("TRACE");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("PATCH");
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new CorsFilter(source));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
    return bean;
  }
}
