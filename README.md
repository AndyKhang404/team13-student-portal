# Student Management System (SSR with Java + Spring Boot)

A Server-Side Rendered (SSR) web application built with Java, Spring Boot, and Thymeleaf. Student data is persisted strictly using a JSON file (`data/students.json`) with thread-safe file operations and zero database/ORM dependencies.

## Members
|Student ID|Name|
|-|-|
|24127052|Phùng Bảo Khang|
|24127345|Nguyễn Minh Đức|
|24127388|Hy Huê Hưng|

---

## Features
- **Server-Side Rendering (SSR):** Powered by Thymeleaf and styled with Bootstrap 5.
- **Pure JSON Storage:** All student data is stored in [`data/students.json`](file:///home/pbkhang404/Projects/S1/web/block3/data/students.json) with `ReentrantReadWriteLock` for concurrency safety and atomic file swapping.
- **Pre-seeded Roster:** Automatically initializes and seeds the roster with the team members on first run if the file does not exist.
- **Form Validation & Duplicate Check:**
  - Mandatory fields: Student ID, Full Name, Email, Major.
  - Email format validation.
  - Unique Student ID verification with inline error feedback.
  - Post-Redirect-Get (PRG) pattern with automatic modal re-opening and form input preservation upon validation errors.

---

## Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Landing/Home page with project features, development team roster, and CTA to `/students` |
| `GET` | `/students` | Student Directory page rendering the full list of students and the "Add Student" modal |
| `POST` | `/students` | Form submission endpoint to add a new student |

---

## How to Run

### 1. Run the Application
Execute using the included Maven Wrapper:
```bash
./mvnw spring-boot:run
```
The server will start at: `http://localhost:8080/`

- Access Home page: [http://localhost:8080/](http://localhost:8080/)
- Access Students Directory: [http://localhost:8080/students](http://localhost:8080/students)

### 2. Run Automated Tests
Execute the full test suite (Repository, Service, and Web MVC MockMvc tests):
```bash
./mvnw test
```

### 3. Package to Executable JAR
```bash
./mvnw clean package
java -jar target/student-management-0.0.1-SNAPSHOT.jar
```
