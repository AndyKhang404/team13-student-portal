'use strict';

const config = require('./src/config');
const { createApp } = require('./src/app');

const { app, repository } = createApp();

repository
  .init()
  .then(() => {
    app.listen(config.port, () => {
      console.log(`Student Portal listening at http://localhost:${config.port}/`);
    });
  })
  .catch((err) => {
    console.error('Failed to initialize storage', err);
    process.exit(1);
  });
