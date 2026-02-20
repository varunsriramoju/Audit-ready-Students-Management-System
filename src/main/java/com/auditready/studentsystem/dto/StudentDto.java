package com.auditready.studentsystem.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record StudentDto(
        Long id,

        @NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String name,

        @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") String email,

        @Size(max = 15, message = "Phone number must not exceed 15 characters") String phone,

        @Size(max = 100, message = "Department must not exceed 100 characters") String department,

        @Min(value = 1, message = "Year must be at least 1") @Max(value = 10, message = "Year must not be greater than 10") Integer year,

        @Size(max = 255, message = "Address must not exceed 255 characters") String address,

        @DecimalMin(value = "0.0", inclusive = true, message = "CGPA must be at least 0.0") @DecimalMax(value = "10.0", inclusive = true, message = "CGPA must not be greater than 10.0") Double cgpa,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy) {
}
