package com.letstogether.event.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class TestContainerConfig {
  @Container
  protected static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest");


  @DynamicPropertySource
  static void postgresqlProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s", postgresqlContainer.getHost(), postgresqlContainer.getFirstMappedPort(), postgresqlContainer.getDatabaseName()));
    registry.add("spring.r2dbc.username", postgresqlContainer::getUsername);
    registry.add("spring.r2dbc.password", postgresqlContainer::getPassword);
    registry.add("spring.flyway.url", () -> String.format("jdbc:postgresql://%s:%d/%s", postgresqlContainer.getHost(), postgresqlContainer.getFirstMappedPort(), postgresqlContainer.getDatabaseName()));
    registry.add("spring.flyway.user", postgresqlContainer::getUsername);
    registry.add("spring.flyway.password", postgresqlContainer::getPassword);
    // Указываем Flyway локации обеих директорий миграций: основной и тестовой
//    registry.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/test_migration");
  }

  @BeforeEach
  void resetDatabase() {
    Flyway.configure()
      .dataSource(postgresqlContainer.getJdbcUrl(), postgresqlContainer.getUsername(), postgresqlContainer.getPassword())
      .cleanDisabled(false)
      .load()
      .clean();
    Flyway.configure()
      .dataSource(postgresqlContainer.getJdbcUrl(), postgresqlContainer.getUsername(), postgresqlContainer.getPassword())
      .cleanDisabled(false)
      .locations("classpath:db/migration", "classpath:db/test_migration")
      .load()
      .migrate();
  }
}
