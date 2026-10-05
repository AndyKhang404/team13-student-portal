package com.example.studentmanagement.controller;

import com.example.studentmanagement.exception.DuplicateStudentIdException;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void listStudents_returnsStudentsViewWithData() throws Exception {
        Student s1 = new Student("24127052", "Phùng Bảo Khang", "pbkhang@example.com", "Computer Science");
        when(studentService.getAllStudents()).thenReturn(List.of(s1));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("studentForm"))
                .andExpect(model().attribute("students", hasSize(1)));
    }

    @Test
    void addStudent_success_redirectsWithFlashAttribute() throws Exception {
        mockMvc.perform(post("/students")
                        .param("studentId", "24129999")
                        .param("fullName", "Alex Smith")
                        .param("email", "alex@example.com")
                        .param("major", "Cyber Security"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(studentService).addStudent(any(Student.class));
    }

    @Test
    void addStudent_nonNumericStudentId_returnsStudentsViewWithError() throws Exception {
        mockMvc.perform(post("/students")
                        .param("studentId", "STU1234")
                        .param("fullName", "Alex Smith")
                        .param("email", "alex@example.com")
                        .param("major", "Computer Science"))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attribute("openModal", true))
                .andExpect(model().attributeHasFieldErrors("studentForm", "studentId"));
    }

    @Test
    void addStudent_invalidMajor_returnsStudentsViewWithError() throws Exception {
        mockMvc.perform(post("/students")
                        .param("studentId", "24129999")
                        .param("fullName", "Alex Smith")
                        .param("email", "alex@example.com")
                        .param("major", "Astronomy"))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attribute("openModal", true))
                .andExpect(model().attributeHasFieldErrors("studentForm", "major"));
    }

    @Test
    void addStudent_validationError_returnsStudentsViewWithModalOpen() throws Exception {
        mockMvc.perform(post("/students")
                        .param("studentId", "")
                        .param("fullName", "A")
                        .param("email", "not-an-email")
                        .param("major", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attribute("openModal", true))
                .andExpect(model().hasErrors());
    }

    @Test
    void addStudent_duplicateId_returnsStudentsViewWithDuplicateError() throws Exception {
        doThrow(new DuplicateStudentIdException("24127052"))
                .when(studentService).addStudent(any(Student.class));

        mockMvc.perform(post("/students")
                        .param("studentId", "24127052")
                        .param("fullName", "Duplicate Name")
                        .param("email", "dup@example.com")
                        .param("major", "Software Engineering"))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attribute("openModal", true))
                .andExpect(model().attributeHasFieldErrors("studentForm", "studentId"));
    }
}
