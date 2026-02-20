package com.auditready.studentsystem.service.impl;

import com.auditready.studentsystem.dto.StudentDto;
import com.auditready.studentsystem.entity.Student;
import com.auditready.studentsystem.exception.StudentNotFoundException;
import com.auditready.studentsystem.mapper.StudentMapper;
import com.auditready.studentsystem.repository.StudentRepository;
import com.auditready.studentsystem.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public List<StudentDto> getAllStudents() {
        log.info("Fetching all students");
        return studentRepository.findAll().stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto getStudentById(Long id) {
        log.info("Fetching student with id: {}", id);
        return studentRepository.findById(id)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
    }

    @Override
    public StudentDto getStudentByEmail(String email) {
        log.info("Fetching student with email: {}", email);
        return studentRepository.findByEmail(email)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with email: " + email));
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentDto studentDto) {
        log.info("Creating new student: {}", studentDto.name());
        Student student = studentMapper.toEntity(studentDto);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        log.info("Updating student with id: {}", id);
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        // updating fields
        existingStudent.setName(studentDto.name());
        existingStudent.setEmail(studentDto.email());
        existingStudent.setPhone(studentDto.phone());
        existingStudent.setDepartment(studentDto.department());
        existingStudent.setYear(studentDto.year());
        existingStudent.setAddress(studentDto.address());
        existingStudent.setCgpa(studentDto.cgpa());

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public Page<StudentDto> getAllStudents(Pageable pageable) {
        log.info("Fetching students with pagination: {}", pageable);
        return studentRepository.findAll(pageable)
                .map(studentMapper::toDto);
    }

    @Override
    public Page<StudentDto> searchStudents(String name, String email, Pageable pageable) {
        log.info("Searching students with name: {} and email: {}", name, email);
        boolean hasName = StringUtils.hasText(name);
        boolean hasEmail = StringUtils.hasText(email);

        Page<Student> students;
        if (hasName && hasEmail) {
            students = studentRepository.findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(name, email,
                    pageable);
        } else if (hasName) {
            students = studentRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (hasEmail) {
            students = studentRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            students = studentRepository.findAll(pageable);
        }

        return students.map(studentMapper::toDto);
    }
}
