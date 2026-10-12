# Student Management System (SSR with Node.js + Express + EJS)

A Server-Side Rendered (SSR) web application built with Node.js, Express 4, and EJS. Refactored in-place from the original Java + Spring Boot + Thymeleaf version. Scope strictly covers **Add**, **List**, and **Delete** student operations. Data is persisted in `data/students.json` with a mutex and atomic file swaps — zero database/ORM.

## Members
|Student ID|Name|
|-|-|
|24127052|Phùng Bảo Khang|
|24127345|Nguyễn Minh Đức|
|24127388|Hy Huê Hưng|

---

## Features
- **Server-Side Rendering (SSR):** EJS templates styled with Bootstrap 5.
- **Pure JSON Storage:** `data/students.json` with `async-mutex` + atomic temp-file rename.
- **Pre-seeded Roster & Retention:** Seeds team roster on first run if file missing. Empty `[]` preserved across restarts.
- **Form Validation & Duplicate Check:**
  - Mandatory: Student ID, Full Name, Email, Major.
  - Student ID: exactly 8 digits (`^[0-9]{8}$`).
  - Major: single source of truth in `src/models/majors.js` (`Computer Science`, `Software Engineering`, `Information System`, `Cyber Security`).
  - Email format validation.
  - Unique Student ID with inline error.
  - PRG pattern with modal re-open + input preservation on errors.
- **Record Deletion:**
  - Web UI via `POST /students/{id}/delete` with confirm + flash.
  - REST via `DELETE /students/{id}` (`204` / `404`).

---

## Project Structure
```
package.json / server.js / app entry (src/app.js)
src/config.js                    # PORT, STORAGE_PATH, SESSION_SECRET
src/models/majors.js             # ALLOWED_MAJORS + isValid + majorClass
src/models/validators.js         # trims + Jakarta-parity messages
src/errors.js                    # DuplicateStudentIdException
src/repositories/jsonStudentRepository.js  # findAll/findById/existsById/save/deleteById + seed + atomic write
src/services/studentService.js   # sorted getAll, add (duplicate guard), delete
src/controllers/homeController.js + studentController.js  # PRG + session flash
src/routes/index.js
views/home.ejs / views/students.ejs
public/css/app.css / public/js/app.js
data/students.json
tests/*.test.js (Jest + Supertest)
```

## Data Flow
```
Browser --GET /--> homeController --> views/home.ejs
Browser --GET /students--> studentController.list --> StudentService.getAll --> JsonStudentRepository.findAll --> data/students.json --> views/students.ejs
Browser --POST /students--> validateStudent --> duplicate check --> save (mutex + atomic rename) --> 302 /students (flash) / 200 re-render + openModal on error
Browser --POST /students/:id/delete--> delete --> 302 + flash
Browser --DELETE /students/:id--> 204 / 404
```

---

## Endpoints
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Landing page with features, team roster, CTA to `/students` |
| `GET` | `/students` | Directory page with full list, search, Add modal |
| `POST` | `/students` | Add student (PRG on success, modal re-open on failure) |
| `POST` | `/students/{id}/delete` | HTML form delete (302 + flash) |
| `DELETE` | `/students/{id}` | REST delete (`204` / `404`) |

---

## How to Run
Requires Node.js 20+.

```bash
cp .env.example .env   # optional; defaults PORT=8080, STORAGE_PATH=data/students.json
npm install
npm start              # http://localhost:8080/
npm run dev            # watch mode
npm test               # Jest + Supertest (25 tests)
```

- Home: http://localhost:8080/
- Directory: http://localhost:8080/students

Config via env: `PORT`, `STORAGE_PATH`, `SESSION_SECRET`.

---

## Notes
- Ported 1:1 from Spring Boot version: same validation messages, flash texts, sorting (ID asc, case-insensitive), seed data, atomic-write semantics.
- No auth/CSRF (classroom demo scope). Add `helmet` + `csurf` + auth before exposing publicly.
- If you kept an old `data/students.json` with extra test rows, delete it once to reseed the clean 3-member roster.
