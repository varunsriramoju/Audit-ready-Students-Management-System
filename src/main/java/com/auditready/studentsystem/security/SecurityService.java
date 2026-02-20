package com.auditready.studentsystem.security;

import com.auditready.studentsystem.entity.Student;
import com.auditready.studentsystem.entity.User;
import com.auditready.studentsystem.repository.StudentRepository;
import com.auditready.studentsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public boolean isOwnProfile(Long studentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null)
            return false;

        Student student = studentRepository.findById(studentId)
                .orElse(null);

        return student != null && student.getEmail().equals(user.getEmail());
    }
}
