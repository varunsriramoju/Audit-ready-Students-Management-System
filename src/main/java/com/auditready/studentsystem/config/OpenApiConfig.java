package com.auditready.studentsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Audit-Ready Student Management API")
                        .version("1.0.0")
                        .description(
                                "Professional API documentation for the Student Management System with full audit tracking.")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
