package com.sammartinez.example.api.config;

import static java.util.Objects.requireNonNull;

import com.sammartinez.example.domain.entity.AbstractCustomEntity;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {"com.sammartinez.example.domain.repository"},
    entityManagerFactoryRef = "customEntityManagerFactory",
    transactionManagerRef = "customTransactionManager")
@EntityScan(basePackages = {"com.sammartinez.custom.domain.entity"})
@RequiredArgsConstructor
public class CustomJpaConfiguration {

  @Primary
  @Bean
  @ConfigurationProperties("spring.datasource.custom")
  public DataSourceProperties customDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean(name = "customDataSource")
  @ConfigurationProperties("spring.datasource.custom.configuration")
  public DataSource dataSource() {
    return customDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Primary
  @Bean(name = "customEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("customDataSource") DataSource dataSource,
      EntityManagerFactoryBuilder builder) {
    return builder.dataSource(dataSource).packages(AbstractCustomEntity.class).build();
  }

  @Primary
  @Bean(name = "customTransactionManager")
  public PlatformTransactionManager transactionManager(
      @Qualifier("customEntityManagerFactory")
          LocalContainerEntityManagerFactoryBean customEntityManagerFactory) {
    return new JpaTransactionManager(requireNonNull(customEntityManagerFactory.getObject()));
  }
}
