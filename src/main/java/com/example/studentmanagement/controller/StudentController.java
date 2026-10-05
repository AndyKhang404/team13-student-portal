package com.example.studentmanagement.controller;

import com.example.studentmanagement.exception.DuplicateStudentIdException;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing students via SSR (/students).
 * Supports retrieving students (GET) and adding students (POST).
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @ModelAttribute("allowedMajors")
    public java.util.List<String> allowedMajors() {
        return Student.ALLOWED_MAJORS;
    }

    /**
     * Display all students and provide an empty form backing object.
     */
    @GetMapping
    public String listStudents(Model model) {
        if (!model.containsAttribute("studentForm")) {
            model.addAttribute("studentForm", new Student());
        }
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    /**
     * Handle form submission to add a new student.
     * Uses PRG (Post-Redirect-Get) on success; re-displays modal with errors on failure.
     */
    @PostMapping
    public String addStudent(
            @Valid @ModelAttribute("studentForm") Student studentForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("openModal", true);
            return "students";
        }

        try {
            studentService.addStudent(studentForm);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Student " + studentForm.getFullName() + " (" + studentForm.getStudentId() + ") was successfully added!"
            );
            return "redirect:/students";
        } catch (DuplicateStudentIdException ex) {
            bindingResult.rejectValue("studentId", "duplicate", ex.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("openModal", true);
            return "students";
        }
    }

    /**
     * Delete student via HTML form submission: POST /{studentId}/delete.
     * Uses PRG (Post-Redirect-Get) with flash message.
     */
    @PostMapping("/{studentId}/delete")
    public String deleteStudentForm(
            @PathVariable("studentId") String studentId,
            RedirectAttributes redirectAttributes) {
        Optional<Student> studentOpt = studentService.getStudentById(studentId);
        boolean deleted = studentService.deleteStudent(studentId);
        if (deleted) {
            String name = studentOpt.map(Student::getFullName).orElse(studentId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Student " + name + " (" + studentId + ") was successfully deleted."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student with ID '" + studentId + "' could not be found."
            );
        }
        return "redirect:/students";
    }

    /**
     * REST endpoint: DELETE /{studentId}.
     * Returns 204 No Content on successful deletion, 404 Not Found if missing.
     */
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudentRest(@PathVariable("studentId") String studentId) {
        boolean deleted = studentService.deleteStudent(studentId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
