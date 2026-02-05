package com.audit.student_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<>() {
            @Override
            @SuppressWarnings("null")
            public @NonNull Optional<String> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                    return Optional.of("SYSTEM");
                }

                String name = authentication.getName();
                if (name == null || name.isBlank()) {
                    return Optional.of("SYSTEM");
                }

                return Optional.of(name);
            }
        };
    }
}