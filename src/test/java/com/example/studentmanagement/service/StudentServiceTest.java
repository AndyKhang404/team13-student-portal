package com.example.studentmanagement.service;

import com.example.studentmanagement.exception.DuplicateStudentIdException;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository);
    }

    @Test
    void getAllStudents_returnsStudentsSortedByStudentId() {
        Student s1 = new Student("24127388", "Student B", "b@example.com", "Major B");
        Student s2 = new Student("24127052", "Student A", "a@example.com", "Major A");

        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Student> result = studentService.getAllStudents();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStudentId()).isEqualTo("24127052");
        assertThat(result.get(1).getStudentId()).isEqualTo("24127388");
    }

    @Test
    void addStudent_successWhenIdIsUnique() {
        Student newStudent = new Student("24129999", "New Student", "new@example.com", "Computer Science");

        when(studentRepository.existsById("24129999")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student saved = studentService.addStudent(newStudent);

        assertThat(saved.getStudentId()).isEqualTo("24129999");
        verify(studentRepository).save(newStudent);
    }

    @Test
    void addStudent_throwsExceptionWhenDuplicateIdExists() {
        Student duplicateStudent = new Student("24127052", "Duplicate", "dup@example.com", "CS");

        when(studentRepository.existsById("24127052")).thenReturn(true);

        assertThatThrownBy(() -> studentService.addStudent(duplicateStudent))
                .isInstanceOf(DuplicateStudentIdException.class)
                .hasMessageContaining("24127052");

        verify(studentRepository, never()).save(any());
    }
}
