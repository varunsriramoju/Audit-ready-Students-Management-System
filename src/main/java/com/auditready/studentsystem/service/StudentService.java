package com.auditready.studentsystem.service;

import com.auditready.studentsystem.dto.StudentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    List<StudentDto> getAllStudents();

    StudentDto getStudentById(Long id);

    StudentDto getStudentByEmail(String email);

    StudentDto createStudent(StudentDto studentDto);

    StudentDto updateStudent(Long id, StudentDto studentDto);

    void deleteStudent(Long id);

    Page<StudentDto> getAllStudents(Pageable pageable);

    Page<StudentDto> searchStudents(String name, String email, Pageable pageable);
}
