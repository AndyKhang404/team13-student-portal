'use strict';

const { validateStudent } = require('../src/models/validators');
const { ALLOWED_MAJORS, isValid } = require('../src/models/majors');

describe('majors', () => {
  test('has 4 official majors', () => {
    expect(ALLOWED_MAJORS).toEqual([
      'Computer Science',
      'Software Engineering',
      'Information System',
      'Cyber Security',
    ]);
  });

  test('isValid is case-insensitive and trims', () => {
    expect(isValid('computer science')).toBe(true);
    expect(isValid('  Cyber Security  ')).toBe(true);
    expect(isValid('Astronomy')).toBe(false);
    expect(isValid('')).toBe(false);
    expect(isValid(null)).toBe(false);
  });
});

describe('validateStudent (mirrors Student.java messages)', () => {
  const base = {
    studentId: '24129999',
    fullName: 'Alex Smith',
    email: 'alex@example.com',
    major: 'Cyber Security',
  };

  test('valid input passes', () => {
    const { valid, errors } = validateStudent(base);
    expect(valid).toBe(true);
    expect(errors).toEqual({});
  });

  test('non-numeric ID fails', () => {
    const { valid, errors } = validateStudent({ ...base, studentId: 'STU1234' });
    expect(valid).toBe(false);
    expect(errors.studentId).toMatch(/8 digits/);
  });

  test('non-8-digit ID fails', () => {
    const { valid, errors } = validateStudent({ ...base, studentId: '1234567' });
    expect(valid).toBe(false);
    expect(errors.studentId).toBeDefined();
  });

  test('invalid major fails', () => {
    const { valid, errors } = validateStudent({ ...base, major: 'Astronomy' });
    expect(valid).toBe(false);
    expect(errors.major).toBeDefined();
  });

  test('blank fields report all errors', () => {
    const { valid, errors } = validateStudent({ studentId: '', fullName: 'A', email: 'not-an-email', major: '' });
    expect(valid).toBe(false);
    expect(errors.studentId).toBeDefined();
    expect(errors.fullName).toBeDefined();
    expect(errors.email).toBeDefined();
    expect(errors.major).toBeDefined();
  });

  test('trims values', () => {
    const { value } = validateStudent({ ...base, studentId: '  24129999  ' });
    expect(value.studentId).toBe('24129999');
  });
});
