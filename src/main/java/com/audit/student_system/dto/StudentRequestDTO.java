package com.audit.student_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudentRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phone;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 10, message = "Year must not be greater than 10")
    private Integer year;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @DecimalMin(value = "0.0", inclusive = true, message = "CGPA must be at least 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "CGPA must not be greater than 10.0")
    private Double cgpa;

    // getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }
}