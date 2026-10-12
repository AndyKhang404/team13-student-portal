'use strict';

const { ALLOWED_MAJORS } = require('../models/majors');
const { validateStudent } = require('../models/validators');
const { DuplicateStudentIdException } = require('../errors');

function takeFlash(req) {
  const flash = {
    successMessage: req.session.successMessage || null,
    errorMessage: req.session.errorMessage || null,
  };
  delete req.session.successMessage;
  delete req.session.errorMessage;
  return flash;
}

function listStudents(service) {
  return async (req, res, next) => {
    try {
      const students = await service.getAllStudents();
      const flash = takeFlash(req);
      res.render('students', {
        students,
        studentForm: { studentId: '', fullName: '', email: '', major: '' },
        errors: {},
        hasErrors: false,
        openModal: false,
        allowedMajors: ALLOWED_MAJORS,
        ...flash,
      });
    } catch (err) {
      next(err);
    }
  };
}

function addStudent(service) {
  return async (req, res, next) => {
    try {
      const { value, errors, valid } = validateStudent(req.body || {});
      if (!valid) {
        const students = await service.getAllStudents();
        return res.status(200).render('students', {
          students,
          studentForm: value,
          errors,
          hasErrors: true,
          openModal: true,
          allowedMajors: ALLOWED_MAJORS,
          successMessage: null,
          errorMessage: null,
        });
      }
      try {
        await service.addStudent(value);
        req.session.successMessage = `Student ${value.fullName} (${value.studentId}) was successfully added!`;
        return res.redirect('/students');
      } catch (err) {
        if (err instanceof DuplicateStudentIdException) {
          const students = await service.getAllStudents();
          return res.status(200).render('students', {
            students,
            studentForm: value,
            errors: { studentId: err.message },
            hasErrors: true,
            openModal: true,
            allowedMajors: ALLOWED_MAJORS,
            successMessage: null,
            errorMessage: null,
          });
        }
        throw err;
      }
    } catch (err) {
      next(err);
    }
  };
}

function deleteStudentForm(service) {
  return async (req, res, next) => {
    try {
      const { studentId } = req.params;
      const existing = await service.getStudentById(studentId);
      const deleted = await service.deleteStudent(studentId);
      if (deleted) {
        const name = existing ? existing.fullName : studentId;
        req.session.successMessage = `Student ${name} (${studentId}) was successfully deleted.`;
      } else {
        req.session.errorMessage = `Student with ID '${studentId}' could not be found.`;
      }
      return res.redirect('/students');
    } catch (err) {
      next(err);
    }
  };
}

function deleteStudentRest(service) {
  return async (req, res, next) => {
    try {
      const deleted = await service.deleteStudent(req.params.studentId);
      if (deleted) return res.status(204).end();
      return res.status(404).end();
    } catch (err) {
      next(err);
    }
  };
}

module.exports = { listStudents, addStudent, deleteStudentForm, deleteStudentRest };
