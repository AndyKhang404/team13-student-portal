'use strict';

const fs = require('fs/promises');
const os = require('os');
const path = require('path');
const { JsonStudentRepository } = require('../src/repositories/jsonStudentRepository');

async function tempPath() {
  const dir = await fs.mkdtemp(path.join(os.tmpdir(), 'portal-test-'));
  return path.join(dir, 'students.json');
}

describe('JsonStudentRepository', () => {
  test('seeds 3 roster students on first init', async () => {
    const p = await tempPath();
    const repo = new JsonStudentRepository(p);
    await repo.init();
    const all = await repo.findAll();
    expect(all).toHaveLength(3);
    expect(all.map((s) => s.studentId).sort()).toEqual(['24127052', '24127345', '24127388']);
  });

  test('empty array file is preserved (no reseed)', async () => {
    const p = await tempPath();
    const repo = new JsonStudentRepository(p);
    await repo.init();
    await fs.writeFile(p, '[]', 'utf8');
    expect(await repo.findAll()).toEqual([]);
  });

  test('save + existsById (case-insensitive) + deleteById', async () => {
    const p = await tempPath();
    const repo = new JsonStudentRepository(p);
    await repo.init();
    await repo.save({ studentId: '24129999', fullName: 'Alex', email: 'a@b.c', major: 'Cyber Security' });
    expect(await repo.existsById('24129999')).toBe(true);
    expect(await repo.findById(' 24129999 ')).toMatchObject({ studentId: '24129999' });
    expect(await repo.deleteById('24129999')).toBe(true);
    expect(await repo.deleteById('24129999')).toBe(false);
  });

  test('findById blank returns null', async () => {
    const p = await tempPath();
    const repo = new JsonStudentRepository(p);
    await repo.init();
    expect(await repo.findById('')).toBeNull();
    expect(await repo.findById(null)).toBeNull();
  });
});
