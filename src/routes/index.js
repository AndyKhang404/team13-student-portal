'use strict';

const express = require('express');
const homeController = require('../controllers/homeController');
const studentController = require('../controllers/studentController');

function buildRoutes(service) {
  const router = express.Router();

  router.get('/', homeController.getHome);

  router.get('/students', studentController.listStudents(service));
  router.post('/students', studentController.addStudent(service));
  router.post('/students/:studentId/delete', studentController.deleteStudentForm(service));
  router.delete('/students/:studentId', studentController.deleteStudentRest(service));

  return router;
}

module.exports = { buildRoutes };
