package com.ntt.report.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI reportOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Report Service API")
                .version("v1")
                .description("Product movement report endpoints."));
  }
}
