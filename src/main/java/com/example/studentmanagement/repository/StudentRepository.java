package com.example.studentmanagement.repository;

import com.example.studentmanagement.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing student persistence.
 */
public interface StudentRepository {

    /**
     * Retrieve all students.
     *
     * @return List of all students
     */
    List<Student> findAll();

    /**
     * Find a student by studentId.
     *
     * @param studentId The unique identifier of the student
     * @return Optional containing the student if found
     */
    Optional<Student> findById(String studentId);

    /**
     * Check if a student with the given studentId exists.
     *
     * @param studentId The unique identifier of the student
     * @return true if exists, false otherwise
     */
    boolean existsById(String studentId);

    /**
     * Save a student to storage.
     *
     * @param student The student to save
     * @return The saved student
     */
    Student save(Student student);

    /**
     * Delete a student by studentId.
     *
     * @param studentId The unique identifier of the student
     * @return true if deleted, false if not found
     */
    boolean deleteById(String studentId);
}
