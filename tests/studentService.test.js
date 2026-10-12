'use strict';

const fs = require('fs/promises');
const os = require('os');
const path = require('path');
const { JsonStudentRepository } = require('../src/repositories/jsonStudentRepository');
const { StudentService } = require('../src/services/studentService');
const { DuplicateStudentIdException } = require('../src/errors');

async function makeService() {
  const dir = await fs.mkdtemp(path.join(os.tmpdir(), 'portal-svc-'));
  const repo = new JsonStudentRepository(path.join(dir, 'students.json'));
  await repo.init();
  return new StudentService(repo);
}

describe('StudentService', () => {
  test('getAllStudents sorted ascending by ID', async () => {
    const svc = await makeService();
    await svc.addStudent({ studentId: '24129999', fullName: 'Zed', email: 'z@e.c', major: 'Cyber Security' });
    const all = await svc.getAllStudents();
    const ids = all.map((s) => s.studentId);
    expect(ids).toEqual([...ids].sort((a, b) => a.localeCompare(b)));
  });

  test('addStudent duplicate throws DuplicateStudentIdException', async () => {
    const svc = await makeService();
    await expect(
      svc.addStudent({ studentId: '24127052', fullName: 'Dup', email: 'd@e.c', major: 'Computer Science' })
    ).rejects.toBeInstanceOf(DuplicateStudentIdException);
  });

  test('addStudent null throws', async () => {
    const svc = await makeService();
    await expect(svc.addStudent(null)).rejects.toThrow();
    await expect(svc.addStudent({ fullName: 'No ID' })).rejects.toThrow();
  });

  test('deleteStudent blank returns false', async () => {
    const svc = await makeService();
    expect(await svc.deleteStudent('')).toBe(false);
    expect(await svc.deleteStudent(null)).toBe(false);
    expect(await svc.deleteStudent('99999999')).toBe(false);
  });
});
