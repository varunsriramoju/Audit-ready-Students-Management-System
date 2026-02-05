package com.audit.student_system.service;

import com.audit.student_system.entity.Student;
import com.audit.student_system.exception.StudentNotFoundException;
import com.audit.student_system.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(@NonNull StudentRepository studentRepository) {
        this.studentRepository = Objects.requireNonNull(studentRepository, "StudentRepository must not be null");
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(@NonNull Long id) {
        Objects.requireNonNull(id, "Student ID must not be null");
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
    }

    @Transactional
    public Student createStudent(@NonNull Student student) {
        Objects.requireNonNull(student, "Student must not be null");
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(@NonNull Long id, @NonNull Student updatedStudent) {
        Objects.requireNonNull(id, "Student ID must not be null");
        Objects.requireNonNull(updatedStudent, "Updated student must not be null");
        
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        
        // Update only the fields that should be updated
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setPhone(updatedStudent.getPhone());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setYear(updatedStudent.getYear());
        existingStudent.setAddress(updatedStudent.getAddress());
        existingStudent.setCgpa(updatedStudent.getCgpa());
        
        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(@NonNull Long id) {
        Objects.requireNonNull(id, "Student ID must not be null");
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Student> getAllStudents(@NonNull Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return studentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Student> searchStudents(String name, String email, @NonNull Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");

        boolean hasName = StringUtils.hasText(name);
        boolean hasEmail = StringUtils.hasText(email);

        if (!hasName && !hasEmail) {
            return studentRepository.findAll(pageable);
        }

        if (hasName && hasEmail) {
            return studentRepository.findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
                    name,
                    email,
                    pageable
            );
        }

        if (hasName) {
            return studentRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return studentRepository.findByEmailContainingIgnoreCase(email, pageable);
    }
}