package com.audit.student_system.repository;

import com.audit.student_system.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByNameContainingIgnoreCase(String name);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Student> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<Student> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            String name,
            String email,
            Pageable pageable
    );
}
