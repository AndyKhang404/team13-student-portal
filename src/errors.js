'use strict';

class DuplicateStudentIdException extends Error {
  constructor(studentId) {
    super(`Student with ID '${studentId}' already exists.`);
    this.name = 'DuplicateStudentIdException';
    this.studentId = studentId;
  }
}

module.exports = { DuplicateStudentIdException };
