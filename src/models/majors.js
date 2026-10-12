'use strict';

const ALLOWED_MAJORS = [
  'Computer Science',
  'Software Engineering',
  'Information System',
  'Cyber Security',
];

function normalize(major) {
  return (major || '').trim().toLowerCase();
}

function isValid(major) {
  if (major == null || String(major).trim() === '') return false;
  const n = normalize(String(major));
  return ALLOWED_MAJORS.some((m) => m.toLowerCase() === n);
}

function majorClass(major) {
  switch (major) {
    case 'Computer Science':
      return 'major-cs';
    case 'Software Engineering':
      return 'major-se';
    case 'Information System':
      return 'major-is';
    case 'Cyber Security':
      return 'major-cyber';
    default:
      return 'major-default';
  }
}

module.exports = { ALLOWED_MAJORS, isValid, majorClass };
