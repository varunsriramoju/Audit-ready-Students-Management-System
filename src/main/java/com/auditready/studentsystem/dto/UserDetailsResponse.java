package com.auditready.studentsystem.dto;

public record UserDetailsResponse(
        String username,
        String role,
        String email) {
}
