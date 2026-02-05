# Student Management System (Spring Boot REST API)

An audit-ready Student Management System built with **Spring Boot**, **Spring Security (JWT)** and **Spring Data JPA**.
It provides secure REST APIs to manage student records with validation, pagination/search, Swagger UI, and automatic audit tracking.

## Highlights

- **JWT Authentication + Role-based Authorization**
- **Student CRUD** (Create, Read, Update, Delete)
- **Search + Pagination**
- **Validation** (Jakarta Bean Validation)
- **Global Exception Handling** (consistent error responses)
- **Swagger / OpenAPI** documentation with **Authorize** support
- **Auditing** (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`)

## Tech Stack

- **Java:** 17 (recommended)
- **Spring Boot:** 3.1.5
- **Spring Security:** JWT-based auth
- **Spring Data JPA:** Hibernate ORM
- **Database:** MySQL (configured)
- **Build Tool:** Maven
- **API Docs:** springdoc-openapi

## Project Architecture (Layered)

- **Controller Layer**
  - Handles HTTP requests and responses.
  - Example: `StudentController`, `AuthController`
- **Service Layer**
  - Business logic, validations and transactional behavior.
  - Example: `StudentService`
- **Repository Layer**
  - Spring Data JPA repositories.
  - Example: `StudentRepository`, `UserRepository`
- **Database Layer**
  - Stores entities such as `Student` and `User`.

## Features

### Authentication

- Register user
- Login user and receive JWT token
- Use JWT token in `Authorization: Bearer <token>`

### Student Module

Student details supported:

- `id`
- `name`
- `email`
- `phone`
- `department`
- `year`
- `address`
- `cgpa`

Operations:

- Add new student
- View all students
- Get student by id
- Update student
- Delete student
- Search students by `name` and/or `email`
- Pagination endpoint using `Pageable`

### Auditing

All student records automatically store:

- `createdAt`, `updatedAt`
- `createdBy`, `updatedBy`

`createdBy/updatedBy` is resolved from the authenticated user (Spring Security context). If the request is unauthenticated, it falls back to `SYSTEM`.

## Setup & Run

### 1) Prerequisites

- Java 17 installed
- Maven installed (or use your IDE Maven)
- MySQL running

### 2) Database configuration

This project is configured to use MySQL via `src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:mysql://localhost:3306/audit_student_db`
- `spring.datasource.username=root`
- `spring.datasource.password=...`

Update those values to match your local MySQL.

### 3) Build

```bash
mvn clean install
```

### 4) Run

```bash
mvn spring-boot:run
```

## Swagger / OpenAPI

After running the application, open:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`

Swagger supports JWT authorization via the **Authorize** button.

## API Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

**Login response** contains a JWT token:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "<JWT>",
    "role": "USER"
  }
}
```

Use the token as:

- Header: `Authorization: Bearer <JWT>`

### Students

- `GET /api/students`
- `GET /api/students/{id}`
- `POST /api/students`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`
- `GET /api/students/page` (pagination)
- `GET /api/students/search?name=&email=` (search + pagination)

**Sample request body** for create/update:

```json
{
  "name": "Varun",
  "email": "varun@gmail.com",
  "phone": "9876543210",
  "department": "CSE",
  "year": 3,
  "address": "Bangalore",
  "cgpa": 8.4
}
```

## Validation & Error Handling

- Uses Jakarta Validation annotations such as `@NotBlank`, `@Email`, `@Size`, etc.
- All common errors are handled via a global exception handler and returned in a consistent error response.

## Project Structure (Main Packages)

- `com.audit.student_system.controller`
- `com.audit.student_system.service`
- `com.audit.student_system.repository`
- `com.audit.student_system.entity`
- `com.audit.student_system.dto`
- `com.audit.student_system.security`
- `com.audit.student_system.audit`
- `com.audit.student_system.handler`

## Notes

- If you change entity fields, Hibernate will update the schema because `spring.jpa.hibernate.ddl-auto=update` is enabled.
- A project-level `cspell.json` is included to keep IDE spell-check warnings clean.

## License

This project is for learning and academic purposes.