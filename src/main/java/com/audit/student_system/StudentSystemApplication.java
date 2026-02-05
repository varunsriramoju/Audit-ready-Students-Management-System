package com.audit.student_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentSystemApplication.class, args);
    }
}