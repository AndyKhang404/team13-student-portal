package com.example.studentmanagement.repository;

import com.example.studentmanagement.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe JSON file-backed implementation of {@link StudentRepository}.
 * Uses ReentrantReadWriteLock and atomic file swaps to ensure data integrity
 * without needing an external database or ORM.
 */
@Repository
public class JsonStudentRepository implements StudentRepository {

    private static final Logger log = LoggerFactory.getLogger(JsonStudentRepository.class);

    private final Path storagePath;
    private final ObjectMapper objectMapper;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    @org.springframework.beans.factory.annotation.Autowired
    public JsonStudentRepository(
            @Value("${app.storage.file-path:data/students.json}") String filePathString) {
        this(filePathString, new ObjectMapper());
    }

    public JsonStudentRepository(String filePathString, ObjectMapper objectMapper) {
        this.storagePath = Paths.get(filePathString);
        this.objectMapper = (objectMapper != null ? objectMapper.copy() : new ObjectMapper())
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostConstruct
    public void init() {
        rwLock.writeLock().lock();
        try {
            if (storagePath.getParent() != null && !Files.exists(storagePath.getParent())) {
                Files.createDirectories(storagePath.getParent());
                log.info("Created storage directory: {}", storagePath.getParent());
            }

            if (!Files.exists(storagePath) || Files.size(storagePath) == 0) {
                log.info("Storage file not found or empty. Seeding initial student data from team roster...");
                List<Student> initialSeed = List.of(
                        new Student("24127052", "Phùng Bảo Khang", "pbkhang@example.com", "Computer Science"),
                        new Student("24127345", "Nguyễn Minh Đức", "nmduc@example.com", "Information System"),
                        new Student("24127388", "Hy Huê Hưng", "hhhung@example.com", "Software Engineering")
                );
                writeStudentsInternal(initialSeed);
                log.info("Successfully seeded {} students to {}", initialSeed.size(), storagePath);
            }
        } catch (IOException e) {
            log.error("Failed to initialize student JSON storage at {}", storagePath, e);
            throw new RuntimeException("Could not initialize student JSON storage", e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public List<Student> findAll() {
        rwLock.readLock().lock();
        try {
            return readStudentsInternal();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Optional<Student> findById(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return Optional.empty();
        }
        rwLock.readLock().lock();
        try {
            return readStudentsInternal().stream()
                    .filter(s -> s.getStudentId() != null && s.getStudentId().equalsIgnoreCase(studentId.trim()))
                    .findFirst();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public boolean existsById(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        rwLock.readLock().lock();
        try {
            return readStudentsInternal().stream()
                    .anyMatch(s -> s.getStudentId() != null && s.getStudentId().equalsIgnoreCase(studentId.trim()));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Student save(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student must not be null");
        }
        rwLock.writeLock().lock();
        try {
            List<Student> currentStudents = new ArrayList<>(readStudentsInternal());
            currentStudents.add(student);
            writeStudentsInternal(currentStudents);
            log.info("Persisted new student: {} to {}", student.getStudentId(), storagePath);
            return student;
        } catch (IOException e) {
            log.error("Failed to write student data to {}", storagePath, e);
            throw new RuntimeException("Could not save student to JSON storage", e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean deleteById(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        rwLock.writeLock().lock();
        try {
            List<Student> currentStudents = new ArrayList<>(readStudentsInternal());
            boolean removed = currentStudents.removeIf(s -> s.getStudentId() != null && s.getStudentId().equalsIgnoreCase(studentId.trim()));
            if (removed) {
                writeStudentsInternal(currentStudents);
                log.info("Deleted student: {} from {}", studentId, storagePath);
            }
            return removed;
        } catch (IOException e) {
            log.error("Failed to update student storage after deletion at {}", storagePath, e);
            throw new RuntimeException("Could not delete student from JSON storage", e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private List<Student> readStudentsInternal() {
        if (!Files.exists(storagePath)) {
            return Collections.emptyList();
        }
        try {
            File file = storagePath.toFile();
            if (file.length() == 0) {
                return Collections.emptyList();
            }
            List<Student> students = objectMapper.readValue(file, new TypeReference<List<Student>>() {});
            return students != null ? students : Collections.emptyList();
        } catch (IOException e) {
            log.error("Failed to read students from {}", storagePath, e);
            throw new RuntimeException("Could not read students from JSON storage", e);
        }
    }

    private void writeStudentsInternal(List<Student> students) throws IOException {
        Path tempFile = Files.createTempFile(
                storagePath.getParent() != null ? storagePath.getParent() : Paths.get("."),
                "students-temp-",
                ".json"
        );
        try {
            objectMapper.writeValue(tempFile.toFile(), students);
            try {
                Files.move(tempFile, storagePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempFile, storagePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            if (Files.exists(tempFile)) {
                try {
                    Files.delete(tempFile);
                } catch (IOException ignored) {
                }
            }
        }
    }
}
