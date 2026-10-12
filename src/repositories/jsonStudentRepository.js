'use strict';

const fs = require('fs/promises');
const path = require('path');
const crypto = require('crypto');
const { Mutex } = require('async-mutex');

const SEED_STUDENTS = [
  { studentId: '24127052', fullName: 'Phùng Bảo Khang', email: 'pbkhang@example.com', major: 'Computer Science' },
  { studentId: '24127345', fullName: 'Nguyễn Minh Đức', email: 'nmduc@example.com', major: 'Information System' },
  { studentId: '24127388', fullName: 'Hy Huê Hưng', email: 'hhhung@example.com', major: 'Software Engineering' },
];

function normId(id) {
  return (id || '').trim().toLowerCase();
}

class JsonStudentRepository {
  constructor(storagePath) {
    this.storagePath = storagePath || 'data/students.json';
    this.mutex = new Mutex();
  }

  async init() {
    await this.mutex.runExclusive(async () => {
      const dir = path.dirname(this.storagePath);
      if (dir && dir !== '.') {
        await fs.mkdir(dir, { recursive: true });
      }
      try {
        await fs.access(this.storagePath);
      } catch {
        await this._writeInternal(SEED_STUDENTS);
      }
    });
  }

  async findAll() {
    return this._readInternal();
  }

  async findById(studentId) {
    if (studentId == null || String(studentId).trim() === '') return null;
    const n = normId(String(studentId));
    const all = await this._readInternal();
    return all.find((s) => s.studentId && normId(s.studentId) === n) || null;
  }

  async existsById(studentId) {
    if (studentId == null || String(studentId).trim() === '') return false;
    return (await this.findById(studentId)) !== null;
  }

  async save(student) {
    if (!student) throw new Error('Student must not be null');
    return this.mutex.runExclusive(async () => {
      const current = await this._readInternal();
      current.push({ ...student });
      await this._writeInternal(current);
      return student;
    });
  }

  async deleteById(studentId) {
    if (studentId == null || String(studentId).trim() === '') return false;
    const n = normId(String(studentId));
    return this.mutex.runExclusive(async () => {
      const current = await this._readInternal();
      const filtered = current.filter((s) => !(s.studentId && normId(s.studentId) === n));
      if (filtered.length === current.length) return false;
      await this._writeInternal(filtered);
      return true;
    });
  }

  async _readInternal() {
    try {
      const raw = await fs.readFile(this.storagePath, 'utf8');
      if (!raw.trim()) return [];
      const parsed = JSON.parse(raw);
      return Array.isArray(parsed) ? parsed : [];
    } catch (err) {
      if (err && err.code === 'ENOENT') return [];
      throw err;
    }
  }

  async _writeInternal(students) {
    const dir = path.dirname(this.storagePath);
    if (dir && dir !== '.') {
      await fs.mkdir(dir, { recursive: true });
    }
    const tmp = path.join(dir === '.' ? '.' : dir, `students-temp-${crypto.randomUUID()}.json`);
    try {
      await fs.writeFile(tmp, JSON.stringify(students, null, 2) + '\n', 'utf8');
      await fs.rename(tmp, this.storagePath);
    } finally {
      try {
        await fs.unlink(tmp);
      } catch {
        // temp already moved — ignore
      }
    }
  }
}

module.exports = { JsonStudentRepository, SEED_STUDENTS };
