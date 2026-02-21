package com.auditready.studentsystem.controller;

import com.auditready.studentsystem.dto.ApiResponse;
import com.auditready.studentsystem.entity.AuditLog;
import com.auditready.studentsystem.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Auditing", description = "Endpoints for viewing system audit logs")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/logs")
    @Operation(summary = "Get all system audit logs")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAllLogs() {
        return ResponseEntity.ok(ApiResponse.success(auditService.getAllLogs(), "Audit logs fetched successfully"));
    }

    @GetMapping("/student/{id}")
    @Operation(summary = "Get audit logs for a specific student")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getStudentLogs(@PathVariable Long id) {
        return ResponseEntity
                .ok(ApiResponse.success(auditService.getStudentLogs(id), "Student audit logs fetched successfully"));
    }
}
