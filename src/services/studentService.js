'use strict';

const { DuplicateStudentIdException } = require('../errors');

function compareIds(a, b) {
  if (a == null && b == null) return 0;
  if (a == null) return 1;
  if (b == null) return -1;
  const al = String(a).toLowerCase();
  const bl = String(b).toLowerCase();
  if (al < bl) return -1;
  if (al > bl) return 1;
  return 0;
}

class StudentService {
  constructor(repository) {
    this.repository = repository;
  }

  async getAllStudents() {
    const all = await this.repository.findAll();
    return [...all].sort((x, y) => compareIds(x.studentId, y.studentId));
  }

  async getStudentById(studentId) {
    return this.repository.findById(studentId);
  }

  async addStudent(student) {
    if (!student || student.studentId == null) {
      throw new Error('Student and Student ID cannot be null');
    }
    const trimmedId = String(student.studentId).trim();
    if (await this.repository.existsById(trimmedId)) {
      throw new DuplicateStudentIdException(trimmedId);
    }
    const clean = {
      studentId: trimmedId,
      fullName: student.fullName != null ? String(student.fullName).trim() : student.fullName,
      email: student.email != null ? String(student.email).trim() : student.email,
      major: student.major != null ? String(student.major).trim() : student.major,
    };
    return this.repository.save(clean);
  }

  async deleteStudent(studentId) {
    if (studentId == null || String(studentId).trim() === '') return false;
    return this.repository.deleteById(String(studentId).trim());
  }
}

module.exports = { StudentService };
