package com.example.studentmanagement.exception;

/**
 * Exception thrown when attempting to add a student with an ID that already exists.
 */
public class DuplicateStudentIdException extends RuntimeException {

    private final String studentId;

    public DuplicateStudentIdException(String studentId) {
        super("Student with ID '" + studentId + "' already exists.");
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }
}
