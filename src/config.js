'use strict';

require('dotenv').config();

module.exports = {
  port: parseInt(process.env.PORT || '8080', 10),
  storagePath: process.env.STORAGE_PATH || process.env.APP_STORAGE_FILE_PATH || 'data/students.json',
  sessionSecret: process.env.SESSION_SECRET || 'team13-student-portal-secret',
};
