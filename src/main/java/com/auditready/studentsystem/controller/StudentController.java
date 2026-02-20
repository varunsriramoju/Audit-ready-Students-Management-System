package com.auditready.studentsystem.controller;

import com.auditready.studentsystem.dto.ApiResponse;
import com.auditready.studentsystem.dto.StudentDto;
import com.auditready.studentsystem.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "Endpoints for managing students")
public class StudentController {

    private final StudentService studentService;
    private final com.auditready.studentsystem.repository.UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all students", description = "Returns a list of all students (ADMIN/STAFF only)")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents(), "Students fetched successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or (hasRole('STUDENT') and @securityService.isOwnProfile(#id))")
    public ResponseEntity<ApiResponse<StudentDto>> getStudentById(@PathVariable Long id) {
        return ResponseEntity
                .ok(ApiResponse.success(studentService.getStudentById(id), "Student fetched successfully"));
    }

    @GetMapping("/my-profile")
    @Operation(summary = "Get current student profile")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentDto>> getMyProfile() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.auditready.studentsystem.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity
                .ok(ApiResponse.success(studentService.getStudentByEmail(user.getEmail()),
                        "Profile fetched successfully"));
    }

    @PostMapping
    @Operation(summary = "Create a new student")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentDto>> createStudent(@Valid @RequestBody StudentDto studentDto) {
        return new ResponseEntity<>(
                ApiResponse.success(studentService.createStudent(studentDto), "Student created successfully"),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing student")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<StudentDto>> updateStudent(@PathVariable Long id,
            @Valid @RequestBody StudentDto studentDto) {
        return ResponseEntity
                .ok(ApiResponse.success(studentService.updateStudent(id, studentDto), "Student updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Student deleted successfully"));
    }

    @GetMapping("/page")
    @Operation(summary = "Get students with pagination")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<StudentDto>>> getStudentsWithPagination(Pageable pageable) {
        return ResponseEntity
                .ok(ApiResponse.success(studentService.getAllStudents(pageable), "Students fetched successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search students with filters and pagination")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<StudentDto>>> searchStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(studentService.searchStudents(name, email, pageable),
                "Search results fetched successfully"));
    }

}
