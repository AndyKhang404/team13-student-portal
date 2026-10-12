'use strict';

const fs = require('fs/promises');
const os = require('os');
const path = require('path');
const request = require('supertest');
const { createApp } = require('../src/app');

async function makeApp() {
  const dir = await fs.mkdtemp(path.join(os.tmpdir(), 'portal-web-'));
  const storagePath = path.join(dir, 'students.json');
  const { app, repository } = createApp({ storagePath, sessionSecret: 'test-secret' });
  await repository.init();
  return app;
}

describe('web routes (parity with StudentControllerTest)', () => {
  test('GET / returns home', async () => {
    const app = await makeApp();
    const res = await request(app).get('/');
    expect(res.status).toBe(200);
    expect(res.text).toMatch(/Student Management/);
  });

  test('GET /students returns list view', async () => {
    const app = await makeApp();
    const res = await request(app).get('/students');
    expect(res.status).toBe(200);
    expect(res.text).toMatch(/24127052/);
  });

  test('POST /students success redirects with flash', async () => {
    const app = await makeApp();
    const agent = request.agent(app);
    const res = await agent
      .post('/students')
      .type('form')
      .send({ studentId: '24129999', fullName: 'Alex Smith', email: 'alex@example.com', major: 'Cyber Security' });
    expect(res.status).toBe(302);
    expect(res.headers.location).toBe('/students');
    const follow = await agent.get('/students');
    expect(follow.text).toMatch(/successfully added/);
    expect(follow.text).toMatch(/24129999/);
  });

  test('POST /students non-numeric ID re-renders with openModal + error', async () => {
    const app = await makeApp();
    const res = await request(app).post('/students').type('form').send({
      studentId: 'STU1234',
      fullName: 'Alex Smith',
      email: 'alex@example.com',
      major: 'Computer Science',
    });
    expect(res.status).toBe(200);
    expect(res.text).toMatch(/8 digits/);
    expect(res.text).toMatch(/addStudentModal/);
    expect(res.text).toMatch(/bootstrap\.Modal/);
  });

  test('POST /students invalid major re-renders with error', async () => {
    const app = await makeApp();
    const res = await request(app).post('/students').type('form').send({
      studentId: '24129999',
      fullName: 'Alex Smith',
      email: 'alex@example.com',
      major: 'Astronomy',
    });
    expect(res.status).toBe(200);
    expect(res.text).toMatch(/valid major/);
  });

  test('POST /students duplicate re-renders with duplicate error', async () => {
    const app = await makeApp();
    const res = await request(app).post('/students').type('form').send({
      studentId: '24127052',
      fullName: 'Duplicate Name',
      email: 'dup@example.com',
      major: 'Software Engineering',
    });
    expect(res.status).toBe(200);
    expect(res.text).toMatch(/already exists/);
  });

  test('POST /students/:id/delete success redirects with flash', async () => {
    const app = await makeApp();
    const agent = request.agent(app);
    const res = await agent.post('/students/24127052/delete');
    expect(res.status).toBe(302);
    expect(res.headers.location).toBe('/students');
    const follow = await agent.get('/students');
    expect(follow.text).toMatch(/successfully deleted/);
  });

  test('POST /students/:id/delete missing redirects with error flash', async () => {
    const app = await makeApp();
    const agent = request.agent(app);
    await agent.post('/students/99999999/delete').expect(302);
    const follow = await agent.get('/students');
    expect(follow.text).toMatch(/could not be found/);
  });

  test('DELETE /students/:id returns 204 / 404', async () => {
    const app = await makeApp();
    await request(app).delete('/students/24127052').expect(204);
    await request(app).delete('/students/99999999').expect(404);
  });
});
