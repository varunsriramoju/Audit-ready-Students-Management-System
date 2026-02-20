package com.auditready.studentsystem.dto;

public record AuthResponse(
                String token,
                String username,
                String role,
                Long expiresAt) {
}
