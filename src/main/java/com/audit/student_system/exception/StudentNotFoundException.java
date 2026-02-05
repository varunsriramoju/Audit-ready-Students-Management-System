package com.audit.student_system.exception;

public class StudentNotFoundException extends RuntimeException {

    // No-arg constructor
    public StudentNotFoundException() {
        super();
    }

    // Constructor with message
    public StudentNotFoundException(String message) {
        super(message);
    }

    // Optional: constructor with message and cause
    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}