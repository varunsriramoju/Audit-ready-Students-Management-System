package com.audit.student_system.controller;

import com.audit.student_system.dto.StudentRequestDTO;
import com.audit.student_system.dto.StudentResponseDTO;
import com.audit.student_system.entity.Student;
import com.audit.student_system.response.ApiResponse;
import com.audit.student_system.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /* =======================
       GET ALL STUDENTS
       ======================= */
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {

        List<StudentResponseDTO> students = studentService.getAllStudents()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(students);
    }

    /* =======================
       GET STUDENT BY ID
       ======================= */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @PathVariable @NonNull Long id) {

        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(toResponseDTO(student));
    }

    /* =======================
       CREATE STUDENT
       ======================= */
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        Student student = toEntity(requestDTO);
        Objects.requireNonNull(student, "Student must not be null");
        Student savedStudent = studentService.createStudent(student);

        return new ResponseEntity<>(
                toResponseDTO(savedStudent),
                HttpStatus.CREATED
        );
    }

    /* =======================
       UPDATE STUDENT
       ======================= */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        Student student = toEntity(requestDTO);
        Objects.requireNonNull(student, "Student must not be null");
        Student updatedStudent = studentService.updateStudent(id, student);

        return ResponseEntity.ok(toResponseDTO(updatedStudent));
    }

    /* =======================
       DELETE STUDENT
       ======================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable @NonNull Long id) {

        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    /* =======================
       PAGINATION
       ======================= */
    @GetMapping("/page")
    public ResponseEntity<Page<StudentResponseDTO>> getStudentsWithPagination(
            @NonNull Pageable pageable) {

        Objects.requireNonNull(pageable, "Pageable must not be null");
        Page<StudentResponseDTO> page = studentService.getAllStudents(pageable)
                .map(this::toResponseDTO);

        return ResponseEntity.ok(page);
    }

    /* =======================
       SEARCH + PAGINATION
       ======================= */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<StudentResponseDTO>>> searchStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @NonNull Pageable pageable) {

        Objects.requireNonNull(pageable, "Pageable must not be null");
        Page<StudentResponseDTO> result = studentService.searchStudents(name, email, pageable)
                .map(this::toResponseDTO);

        ApiResponse<Page<StudentResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Students fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    /* =======================
       MAPPER METHODS
       ======================= */
    private StudentResponseDTO toResponseDTO(Student student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDepartment(student.getDepartment());
        dto.setYear(student.getYear());
        dto.setAddress(student.getAddress());
        dto.setCgpa(student.getCgpa());
        dto.setCreatedBy(student.getCreatedBy());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedBy(student.getUpdatedBy());
        dto.setUpdatedAt(student.getUpdatedAt());
        return dto;
    }

    private Student toEntity(StudentRequestDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDepartment(dto.getDepartment());
        student.setYear(dto.getYear());
        student.setAddress(dto.getAddress());
        student.setCgpa(dto.getCgpa());
        return student;
    }
}