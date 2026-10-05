package com.example.studentmanagement.repository;

import com.example.studentmanagement.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonStudentRepositoryTest {

    @TempDir
    Path tempDir;

    private Path jsonFile;
    private JsonStudentRepository repository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jsonFile = tempDir.resolve("test-students.json");
        objectMapper = new ObjectMapper();
        repository = new JsonStudentRepository(jsonFile.toString(), objectMapper);
    }

    @Test
    void init_seedsInitialStudentsWhenFileDoesNotExist() {
        assertThat(Files.exists(jsonFile)).isFalse();

        repository.init();

        assertThat(Files.exists(jsonFile)).isTrue();
        List<Student> students = repository.findAll();
        assertThat(students).hasSize(3);
        assertThat(students).extracting(Student::getStudentId)
                .containsExactlyInAnyOrder("24127052", "24127345", "24127388");
    }

    @Test
    void save_addsNewStudentAndPersistsToFile() {
        repository.init();

        Student newStudent = new Student("99999999", "Test User", "test@example.com", "Cyber Security");
        Student saved = repository.save(newStudent);

        assertThat(saved).isEqualTo(newStudent);

        List<Student> students = repository.findAll();
        assertThat(students).hasSize(4);
        assertThat(repository.existsById("99999999")).isTrue();

        Optional<Student> retrieved = repository.findById("99999999");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getFullName()).isEqualTo("Test User");
        assertThat(retrieved.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void existsById_returnsFalseForNonExistentStudent() {
        repository.init();
        assertThat(repository.existsById("non-existent")).isFalse();
        assertThat(repository.existsById("")).isFalse();
        assertThat(repository.existsById(null)).isFalse();
    }

    @Test
    void save_nullStudentThrowsException() {
        repository.init();
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteById_existingStudent_removesStudentAndReturnsTrue() {
        repository.init();
        assertThat(repository.existsById("24127052")).isTrue();

        boolean deleted = repository.deleteById("24127052");

        assertThat(deleted).isTrue();
        assertThat(repository.existsById("24127052")).isFalse();
        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void deleteById_nonExistentStudent_returnsFalse() {
        repository.init();

        boolean deleted = repository.deleteById("00000000");

        assertThat(deleted).isFalse();
        assertThat(repository.findAll()).hasSize(3);
    }
}
