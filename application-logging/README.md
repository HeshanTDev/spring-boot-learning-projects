# application-logging

A small, learning-oriented CRUD REST API built with Spring Boot. It exposes two resources, **Product** and **User**, and follows a conventional layered architecture.

This guide explains how to get the project running from a clean clone.

---

## Tech stack

| Technology      | Version / notes                          |
| --------------- | ---------------------------------------- |
| Java            | 17                                       |
| Spring Boot     | 4.1.1                                    |
| Spring Web MVC  | via `spring-boot-starter-webmvc`         |
| Spring Data JPA | via `spring-boot-starter-data-jpa`       |
| MySQL           | 8.x (driver: `com.mysql.cj.jdbc.Driver`) |
| Lombok          | reduces boilerplate                      |
| Bean Validation | `jakarta.validation`                     |

---

## Prerequisites

- **JDK 17 or newer** (`java -version`)
- **MySQL 8** running locally
- No Maven install required — the Maven Wrapper (`mvnw` / `mvnw.cmd`) is included

---

## 1. Create the database

The application connects to a schema named `application_logging` by default (change the URL in the next step if you prefer another name). Tables are created automatically by Hibernate (`ddl-auto=update`), so you only need the empty database:

```sql
CREATE DATABASE application_logging;
```

---

## 2. Configure the database connection

`src/main/resources/application.properties` ships with **placeholders** — no real credentials are committed. Either edit that file directly, or (recommended) leave it untouched and put your secrets in a git-ignored override file:

**Option A — git-ignored override (recommended)**

Create `src/main/resources/application-local.properties`. It is already listed in `.gitignore`, so it can never be committed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/application_logging
spring.datasource.username=your_username
spring.datasource.password=your_password
```

`application.properties` imports this file automatically:

```properties
spring.config.import=optional:classpath:application-local.properties
```

Because the import is marked `optional:`, the application still starts when the file is absent.

**Option B — edit `application.properties` directly**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/application_logging
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Adjust the host, port, and database name in the URL to match your MySQL setup. The database name in the URL must match the schema you created in step 1.

> **Never commit real credentials.** Keep your password out of version control, or override it at launch (see below).

### Overriding credentials without editing the file

```bash
java -jar target/application-logging-0.0.1-SNAPSHOT.jar \
  --spring.datasource.username=root \
  --spring.datasource.password=secret
```

On Windows PowerShell:

```powershell
java -jar target/application-logging-0.0.1-SNAPSHOT.jar --spring.datasource.username=root --spring.datasource.password=secret
```

---

## 3. Build

```bash
# Linux / macOS
./mvnw clean package

# Windows
mvnw.cmd clean package
```

The tests run against a real MySQL instance, so if the database is unavailable use `-DskipTests`:

```bash
./mvnw clean package -DskipTests
```

---

## 4. Run

```bash
# via the Spring Boot plugin
./mvnw spring-boot:run

# or run the packaged jar
java -jar target/application-logging-0.0.1-SNAPSHOT.jar
```

The API is then available at **http://localhost:8080**.

---

## API endpoints

Base URL: `http://localhost:8080`

### Products — `/api/v1/products`

| Method | Path                    | Description        | Success status |
| ------ | ----------------------- | ------------------ | -------------- |
| POST   | `/api/v1/products`      | Create a product   | `201 Created`  |
| GET    | `/api/v1/products`      | List all products  | `200 OK`       |
| GET    | `/api/v1/products/{id}` | Get one product    | `200 OK`       |
| PUT    | `/api/v1/products/{id}` | Update a product   | `200 OK`       |
| DELETE | `/api/v1/products/{id}` | Delete a product   | `204 No Content` |

**Product request body**

```json
{
  "name": "Laptop",
  "price": 1200.00
}
```

**Product response body**

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 1200.00
}
```

### Users — `/api/v1/users`

| Method | Path                 | Description     | Success status |
| ------ | -------------------- | --------------- | -------------- |
| POST   | `/api/v1/users`      | Create a user   | `201 Created`  |
| GET    | `/api/v1/users`      | List all users  | `200 OK`       |
| GET    | `/api/v1/users/{id}` | Get one user    | `200 OK`       |
| PUT    | `/api/v1/users/{id}` | Update a user   | `200 OK`       |
| DELETE | `/api/v1/users/{id}` | Delete a user   | `204 No Content` |

**User request body**

```json
{
  "name": "Heshan",
  "email": "heshan@example.com"
}
```

**User response body**

```json
{
  "id": 1,
  "name": "Heshan",
  "email": "heshan@example.com"
}
```

---

## Validation rules

| DTO                | Field   | Rule                                     |
| ------------------ | ------- | ---------------------------------------- |
| `ProductRequestDto`| `name`  | `@NotBlank`                              |
| `ProductRequestDto`| `price` | `@NotNull`, `@Positive`                  |
| `UserRequestDto`   | `name`  | `@NotBlank`                              |
| `UserRequestDto`   | `email` | `@NotBlank`, `@Email`                    |

---

## Error responses

Missing resources return **404 Not Found**:

```json
{
  "message": "Product not found with id: 42"
}
```

Validation failures return **400 Bad Request** with one entry per invalid field:

```json
{
  "name": "Product name must not be blank",
  "price": "Product price must be greater than zero"
}
```

Malformed JSON bodies also return **400 Bad Request**:

```json
{
  "message": "Malformed request body"
}
```

---

## Testing with Postman

Import both collections from the `postman/` directory:

- `postman/ProductCollection.json`
- `postman/UserCollection.json`

Each collection defines a `baseUrl` variable (default `http://localhost:8080`). Run the requests in order — the **Create** request saves the new `id` into a `productId` / `userId` variable that the read, update, and delete requests reuse. Every request ships with assertions against the expected status codes, including the `404` and `400` cases.

---

## Quick manual smoke test

```bash
# Create
curl -i -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":1200.00}'

# List
curl http://localhost:8080/api/v1/products

# Unknown id -> 404
curl -i http://localhost:8080/api/v1/products/999999

# Invalid body -> 400
curl -i -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"","price":-5}'
```

---

## Project structure

```text
src/main/java/com/heshant/bcd/applicationlogging/
├── controller/
│   ├── ProductController.java
│   └── UserController.java
├── dto/
│   ├── ProductRequestDto.java
│   ├── ProductResponseDto.java
│   ├── UserRequestDto.java
│   └── UserResponseDto.java
├── entity/
│   ├── Product.java
│   └── User.java
├── exception/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── ProductRepository.java
│   └── UserRepository.java
└── service/
    ├── ProductService.java
    ├── UserService.java
    └── impl/
        ├── ProductServiceImpl.java
        └── UserServiceImpl.java
```

Layer responsibilities:

- **controller** — HTTP handling only; never touches repositories
- **service** — business logic and entity ↔ DTO mapping
- **repository** — persistence only
- **dto** — API input/output; entities are never exposed
- **entity** — database mapping
- **exception** — centralised error handling via `@RestControllerAdvice`

---

## Logging

Logging uses SLF4J through Lombok's `@Slf4j`. Service classes log create/read/update/delete activity and warn on missing resources, for example:

```
INFO  ProductServiceImpl : Creating product: Laptop
INFO  ProductServiceImpl : Product created with id: 1
WARN  ProductServiceImpl : Product not found with id: 42
```

---

## Notes

- No authentication, authorisation, or Spring Security is included — this is a learning CRUD application.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for development but should not be used in production.
