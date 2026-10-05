package com.example.studentmanagement.service;

import com.example.studentmanagement.exception.DuplicateStudentIdException;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing student business logic, validations, and ordering.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieve all students sorted ascending by Student ID.
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll().stream()
                .sorted(Comparator.comparing(Student::getStudentId, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a student by ID.
     */
    public Optional<Student> getStudentById(String studentId) {
        return studentRepository.findById(studentId);
    }

    /**
     * Add a new student, verifying uniqueness of studentId.
     *
     * @throws DuplicateStudentIdException if student with same ID exists
     */
    public Student addStudent(Student student) {
        if (student == null || student.getStudentId() == null) {
            throw new IllegalArgumentException("Student and Student ID cannot be null");
        }

        String trimmedId = student.getStudentId().trim();
        if (studentRepository.existsById(trimmedId)) {
            throw new DuplicateStudentIdException(trimmedId);
        }

        student.setStudentId(trimmedId);
        if (student.getFullName() != null) {
            student.setFullName(student.getFullName().trim());
        }
        if (student.getEmail() != null) {
            student.setEmail(student.getEmail().trim());
        }
        if (student.getMajor() != null) {
            student.setMajor(student.getMajor().trim());
        }

        return studentRepository.save(student);
    }

    /**
     * Delete a student by studentId.
     *
     * @param studentId The unique identifier of the student
     * @return true if deleted, false if not found
     */
    public boolean deleteStudent(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        return studentRepository.deleteById(studentId.trim());
    }
}
