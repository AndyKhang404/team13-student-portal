package com.example.studentmanagement.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

/**
 * Domain model representing a Student.
 */
public class Student {

    public static final List<String> ALLOWED_MAJORS = Major.allDisplayNames();

    @NotBlank(message = "Student ID is required")
    @Pattern(regexp = "^[0-9]{8}$", message = "Student ID must be exactly 8 digits (e.g. 24127052)")
    private String studentId;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Major is required")
    @Pattern(regexp = "^(Computer Science|Software Engineering|Information System|Cyber Security)$",
            message = "Please select a valid major from the list")
    private String major;

    public Student() {
    }

    public Student(String studentId, String fullName, String email, String major) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.major = major;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId != null ? studentId.trim() : null;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major != null ? major.trim() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
}
