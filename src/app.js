'use strict';

const path = require('path');
const express = require('express');
const session = require('express-session');
const config = require('./config');
const { JsonStudentRepository } = require('./repositories/jsonStudentRepository');
const { StudentService } = require('./services/studentService');
const { buildRoutes } = require('./routes');
const { majorClass } = require('./models/majors');

function createApp(options = {}) {
  const storagePath = options.storagePath || config.storagePath;
  const sessionSecret = options.sessionSecret || config.sessionSecret;

  const repository = new JsonStudentRepository(storagePath);
  const service = new StudentService(repository);

  const app = express();

  app.set('view engine', 'ejs');
  app.set('views', path.join(__dirname, '..', 'views'));

  app.use(express.urlencoded({ extended: false }));
  app.use(express.static(path.join(__dirname, '..', 'public')));
  app.use(
    session({
      secret: sessionSecret,
      resave: false,
      saveUninitialized: false,
    })
  );

  // Shared template helpers
  app.locals.majorClass = majorClass;
  app.locals.avatarClass = (index) => `avatar-${index % 5}`;

  app.use(buildRoutes(service));

  // 404
  app.use((req, res) => {
    res.status(404).send('Not Found');
  });

  // Error handler
  // eslint-disable-next-line no-unused-vars
  app.use((err, req, res, next) => {
    console.error(err);
    res.status(500).send('Internal Server Error');
  });

  return { app, repository, service };
}

module.exports = { createApp };
