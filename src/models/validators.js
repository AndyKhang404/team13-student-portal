'use strict';

const { isValid: isValidMajor } = require('./majors');

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function trimOrEmpty(v) {
  return v == null ? '' : String(v).trim();
}

// Mirrors Jakarta validation messages in Student.java exactly.
function validateStudent(input) {
  const value = {
    studentId: trimOrEmpty(input.studentId),
    fullName: trimOrEmpty(input.fullName),
    email: trimOrEmpty(input.email),
    major: trimOrEmpty(input.major),
  };
  const errors = {};

  if (!value.studentId) {
    errors.studentId = 'Student ID is required';
  } else if (!/^[0-9]{8}$/.test(value.studentId)) {
    errors.studentId = 'Student ID must be exactly 8 digits (e.g. 24127052)';
  }

  if (!value.fullName) {
    errors.fullName = 'Full name is required';
  } else if (value.fullName.length < 2 || value.fullName.length > 100) {
    errors.fullName = 'Full name must be between 2 and 100 characters';
  }

  if (!value.email) {
    errors.email = 'Email is required';
  } else if (!EMAIL_RE.test(value.email)) {
    errors.email = 'Please provide a valid email address';
  }

  if (!value.major) {
    errors.major = 'Major is required';
  } else if (!isValidMajor(value.major)) {
    errors.major = 'Please select a valid major from the list';
  }

  return { value, errors, valid: Object.keys(errors).length === 0 };
}

module.exports = { validateStudent };
