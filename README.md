# 💸 ExpenseCore — Scalable Backend System

> A production-grade, enterprise-level RESTful backend system built with **Spring Boot**, featuring JWT authentication, dynamic RBAC, Redis-based token blacklisting, and clean layered architecture.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Key Features](#-key-features)
- [API Endpoints](#-api-endpoints)
- [Security Design](#-security-design)
- [Database Schema](#-database-schema)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [Running the Application](#-running-the-application)

---

## 🧩 Overview

**ExpenseCore** is a scalable backend system for expense management, designed with enterprise-level patterns and production-ready practices. It provides secure, multi-user expense tracking with fine-grained access control, structured observability, and a fully standardized API layer.

---

## 🏗 System Architecture

```
Client
  │
  ▼ HTTP Request
┌─────────────────────────────────────┐
│            API Gateway              │
│  ┌──────────────┐  ┌─────────────┐  │
│  │Auth Controller│  │User/Expense │  │
│  └──────────────┘  │/Category    │  │
│                    │Controllers  │  │
│                    └─────────────┘  │
└─────────────────────────────────────┘
         │                  │
         ▼                  ▼
┌──────────────┐   ┌──────────────────┐
│ MySQL DB     │   │   Redis Cache     │
│ • Users      │   │   JWT Blacklist   │
│ • Roles &    │   └──────────────────┘
│   Permissions│
│ • Expenses   │
│ • Categories │
└──────────────┘
         │
┌──────────────────────────┐
│  Logging (SLF4J + MDC)   │
└──────────────────────────┘
┌──────────────────────────┐
│  Validation Layer        │
│  (Jakarta Validation)    │
└──────────────────────────┘
```

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT |
| Database | MySQL |
| Caching | Redis |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Logging | SLF4J + MDC |
| Build Tool | Maven |

---

## ✨ Key Features

### 🔐 Authentication & Security
- **JWT-based stateless authentication** — no server-side session storage
- **Custom `UserDetails`** with dynamic authority loading
- **Spring Security `@PreAuthorize`** for method-level authorization
- **SHA-256 token hashing** before storage in Redis

### 🛡 Role-Based Access Control (RBAC)
- Roles and permissions are **stored in the database**, not hardcoded as enums
- **Many-to-Many** role-permission mapping
- Dynamic authority resolution at runtime
- Role hierarchy: `ROLE_ADMIN` → `ROLE_USER`
- Granular permissions: `PERMISSION_READ`, `PERMISSION_WRITE`

### 🔴 Redis Token Blacklisting
- Logout **immediately invalidates** the JWT
- Tokens stored in Redis with **TTL matching token expiry**
- Auto-expiry ensures **zero memory leaks**
- Blacklist check on every authenticated request

### 📦 API Design & Standardization
- **`ApiResponse<T>` wrapper** for consistent success/error responses
- **Global exception handling** via `@RestControllerAdvice`
- Input validation with Bean Validation (Jakarta)
- 15+ RESTful API endpoints across all modules

### 📊 Logging & Observability
- **MDC (Mapped Diagnostic Context)** with `correlationId` / `traceId` per request
- Request/response logging with **execution time tracking**
- Structured logs for production-level debugging

### 🔍 Pagination & Filtering
- Spring Data JPA `Pageable` for large dataset handling
- Custom `PageResponse<T>` wrapper
- **Advanced filters** for expenses:
  - By category
  - By date range
  - By name
- Sorting field validation to prevent runtime errors

### 🔒 Data Security & Integrity
- **User-level data isolation** — users access only their own data
- Ownership validation at the service layer
- No entity exposure — **DTO-based communication throughout**

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/register` | Register a new user | Public |
| `POST` | `/api/auth/login` | Login and receive JWT | Public |
| `POST` | `/api/auth/logout` | Invalidate JWT token | Authenticated |

### Users
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/users/me` | Get current user profile | `PERMISSION_READ` |
| `PUT` | `/api/users/me` | Update current user profile | `PERMISSION_WRITE` |
| `GET` | `/api/users` | List all users | `ROLE_ADMIN` |
| `DELETE` | `/api/users/{id}` | Delete user | `ROLE_ADMIN` |

### Expenses
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/expenses` | Create an expense | `PERMISSION_WRITE` |
| `GET` | `/api/expenses` | Get all expenses (paginated + filtered) | `PERMISSION_READ` |
| `GET` | `/api/expenses/{id}` | Get expense by ID | `PERMISSION_READ` |
| `PUT` | `/api/expenses/{id}` | Update expense | `PERMISSION_WRITE` |
| `DELETE` | `/api/expenses/{id}` | Delete expense | `PERMISSION_WRITE` |

### Categories
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/categories` | Create category | `ROLE_ADMIN` |
| `GET` | `/api/categories` | Get all categories | `PERMISSION_READ` |
| `PUT` | `/api/categories/{id}` | Update category | `ROLE_ADMIN` |
| `DELETE` | `/api/categories/{id}` | Delete category | `ROLE_ADMIN` |

---

## 🔐 Security Design

### JWT Authentication Flow

```
Client ──[Login Request]──► JWT Generation ──► JWT Token
                                                   │
                                                   ▼
                                          Secure Endpoint
                                                   │
                                                   ▼
                                            JWT Filter
                                         ┌─────────────┐
                                         │Validate Token│
                                         │Check Redis   │
                                         │  Cache       │
                                         └─────────────┘
```

### Redis JWT Blacklist Flow

```
Logout Request
     │
     ▼
Hash Token (SHA-256)
     │
     ▼
Store in Redis as:
  Key:   blacklist:<tokenHash>
  Value: "true"
  TTL:   remaining token lifetime
     │
     ▼
On Next Request → JWT Filter checks Redis → Token Denied ✗
```

### RBAC Authorization Flow

```
User Request
     │
     ▼
Spring Security → Fetch User Roles from DB
     │
     ▼
Role-Permission Mapping Table
  role_id → permission_id
     │
     ├──► PERMISSION_READ  ──► Service Layer Check ──► Access Granted/Denied
     └──► PERMISSION_WRITE ──► Service Layer Check ──► Access Granted/Denied
```

---

## 🗄 Database Schema

```
users
  id | username | email | password | created_at

roles
  id | name (ROLE_USER, ROLE_ADMIN)

permissions
  id | name (PERMISSION_READ, PERMISSION_WRITE)

user_roles
  user_id | role_id

role_permissions
  role_id | permission_id

expenses
  id | title | amount | date | category_id | user_id | created_at

categories
  id | name | description
```

---

## 📁 Project Structure

```
expensecore/
├── src/main/java/com/expensecore/
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── ExpenseController.java
│   │   └── CategoryController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── ExpenseService.java
│   │   └── CategoryService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ExpenseRepository.java
│   │   └── CategoryRepository.java
│   ├── security/
│   │   ├── JwtFilter.java
│   │   ├── JwtUtil.java
│   │   ├── CustomUserDetailsService.java
│   │   └── SecurityConfig.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Permission.java
│   │   ├── Expense.java
│   │   └── Category.java
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── CustomExceptions.java
│   └── config/
│       └── RedisConfig.java
└── src/main/resources/
    └── application.yml
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+
- Redis 7+

### Clone the Repository

```bash
git clone https://github.com/MadathaGanesh/ExpenseCore-Scalable-Backend-System-with-JWT-RBAC-
cd https://github.com/MadathaGanesh/ExpenseCore-Scalable-Backend-System-with-JWT-RBAC-
```

### Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE expensecore;
```

### Configure Environment Variables

Create an `application.yml` or set environment variables (see below).

### Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

---

## ⚙ Environment Variables

```yaml
# application.yml

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/expensecore
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

  redis:
    host: localhost
    port: 6379

jwt:
  secret: YOUR_JWT_SECRET_KEY
  expiration: 86400000   # 24 hours in ms

logging:
  level:
    com.expensecore: DEBUG
```

---

## ▶ Running the Application

```bash
# Start MySQL
# Start Redis
redis-server

# Run the app
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## 📌 Notable Design Decisions

- **No hardcoded roles/permissions** — everything is DB-driven for true runtime flexibility
- **SHA-256 hashing** on JWT before Redis storage — prevents token leakage even if Redis is compromised
- **DTO-only API surface** — entities never leave the service layer
- **MDC correlation IDs** — every request is fully traceable end-to-end in logs
- **Ownership validation** in service layer — not just at the controller, ensuring security even with direct service calls

---

## 👤 Author

**Madatha Ganesh**  
Java Backend Developer  
[GitHub](https://github.com/MadathaGanesh) · [LinkedIn](https://www.linkedin.com/in/madathaganesh/)

---

> *ExpenseCore is built with enterprise-grade practices to demonstrate real-world backend engineering — not a beginner tutorial project.*
