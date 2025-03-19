Based on your request, here's how you can modify your README file to include details about the GitHub Actions workflow you've implemented:

# Library Management System

A comprehensive Spring Boot application for managing a library's book inventory and borrowing system.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Setup Instructions](#setup-instructions)
- [Database Configuration](#database-configuration)
- [Authentication](#authentication)
- [Docker Setup](#docker-setup)
- [CI/CD Pipeline](#ci-cd-pipeline)
- [Testing](#testing)

## Overview

This Library Management System provides a complete solution for libraries to manage their book inventory, user accounts, and borrowing operations. The application follows the MVC architecture pattern and includes features like user authentication with JWT, role-based access control, and comprehensive exception handling.

## Features

- User authentication and authorization with JWT
- Role-based access control (Admin and User roles)
- Book management (add, update, delete, view)
- Borrowing system (borrow and return books)
- Borrowing history tracking
- API documentation with Swagger UI
- Comprehensive exception handling
- Containerized deployment with Docker
- Automated CI/CD with GitHub Actions

## Technologies Used

- Java 17+
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- MySQL Database
- JWT Authentication
- Maven
- Swagger/OpenAPI
- Docker
- Docker Compose
- GitHub Actions

## Project Structure

```
src/main/java/com/library/management/
├── LibraryManagementApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── PasswordEncoderConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── AdminController.java
│   ├── BookController.java
│   └── UserController.java
├── dto/
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── BookDto.java
│   ├── BorrowDto.java
│   ├── UserDto.java
│   └── ErrorResponse.java
├── entity/
│   ├── User.java
│   ├── Book.java
│   └── Borrow.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── BadRequestException.java
├── repository/
│   ├── UserRepository.java
│   ├── BookRepository.java
│   └── BorrowRepository.java
├── service/
│   ├── AuthService.java
│   ├── BookService.java
│   ├── BorrowService.java
│   ├── UserService.java
│   └── JwtService.java
└── util/
    └── AppConstants.java
```

## API Endpoints

### Authentication Endpoints

| Method | URL                | Description                                | Access      |
|--------|--------------------|--------------------------------------------|-------------|
| POST   | /auth/signup       | Register a new user                        | Public      |
| POST   | /auth/login        | Login and get access tokens                | Public      |
| POST   | /auth/refresh      | Refresh access token                       | Public      |
| POST   | /auth/logout       | Logout                                     | Authenticated |
| GET    | /auth/me           | Get current user details                   | Authenticated |

### Admin Endpoints

| Method | URL                      | Description                          | Access      |
|--------|--------------------------|--------------------------------------|-------------|
| POST   | /admin/books             | Add a new book                       | Admin only  |
| PUT    | /admin/books/{id}        | Update book details                  | Admin only  |
| DELETE | /admin/books/{id}        | Delete a book                        | Admin only  |
| GET    | /admin/books             | View all books                       | Admin only  |
| GET    | /admin/books/{id}        | Get book details                     | Admin only  |
| GET    | /admin/borrowed-books    | View all borrowed books              | Admin only  |

### User Endpoints

| Method | URL                      | Description                          | Access      |
|--------|--------------------------|--------------------------------------|-------------|
| GET    | /books                   | Browse available books               | Authenticated |
| POST   | /books/{id}/borrow       | Borrow a book                        | Authenticated |
| POST   | /books/{id}/return       | Return a book                        | Authenticated |
| GET    | /books/history           | View borrowing history               | Authenticated |

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven
- MySQL
- Docker and Docker Compose (for containerized deployment)

### Installation Steps

1. Clone the repository:
   ```
   https://github.com/anuj-consultadd/Java_SpringBoot_LibraryManagement.git
   cd Java_SpringBoot_LibraryManagement
   ```

2. Configure the database in `application.properties` (see Database Configuration section)

3. Build the project:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

5. Access the application at `http://localhost:8080`

6. Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`

## Database Configuration

Configure your database connection in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/SpringBoot_Library_Management?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
app.jwt.secret=your_jwt_secret_key_should_be_very_long_and_secure_at_least_256_bits
app.jwt.expiration=86400000
app.jwt.refresh-expiration=604800000

# Server Configuration
server.port=8080
```

## Authentication

The application uses JWT (JSON Web Tokens) for authentication:

1. Register a new user using `/auth/signup`
2. Login with username and password at `/auth/login` to receive access and refresh tokens
3. Include the access token in the Authorization header for all protected endpoints:
   ```
   Authorization: Bearer your_access_token
   ```
4. Use the refresh token at `/auth/refresh` to get a new access token when it expires

## Docker Setup

The application is containerized using Docker for easy deployment. The setup includes three containers:
- Spring Boot application
- MySQL database
- phpMyAdmin for database management

### Dockerfile

The Dockerfile for the Spring Boot application:

```dockerfile
FROM openjdk:21
WORKDIR /app

COPY target/management-0.0.1-SNAPSHOT.jar /app/management-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "management-0.0.1-SNAPSHOT.jar"]
```

### Docker Compose

The `docker-compose.yml` file orchestrates the multi-container setup:

```yaml
version: "3.8"
services:
  springboot-app:
    image: springboot-app
    restart: always
    build: .
    ports:
      - 8080:8080
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysqldb:3306/library_management_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: Anuj@1234
    depends_on:
      - mysqldb
    networks:
      - app-network

  phpmyadmin:
    image: phpmyadmin/phpmyadmin
    container_name: phpmyadmin
    ports:
      - "8081:80"
    environment:
      PMA_HOST: mysqldb
      PMA_PORT: 3306
      PMA_USER: root
      PMA_PASSWORD: Anuj@1234
    depends_on:
      - mysqldb
    networks:
      - app-network

  mysqldb:
    image: mysql:8.0
    container_name: mysqldb
    volumes:
      - mysql-data:/var/lib/mysql
    ports:
      - 3307:3306
    environment:
      MYSQL_DATABASE: library_management_db
      MYSQL_ROOT_PASSWORD: Anuj@1234
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  mysql-data:
```

### Running with Docker

1. Build and start the containers:
   ```
   docker-compose up -d
   ```

2. Access the services:
   - Spring Boot application: http://localhost:8080
   - phpMyAdmin: http://localhost:8081 (login with username: root, password: Anuj@1234)
   - MySQL database is accessible on port 3307

3. Stop the containers:
   ```
   docker-compose down
   ```

4. To remove volumes when stopping:
   ```
   docker-compose down -v
   ```

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration and continuous deployment. The workflow is defined in `.github/workflows/build.yml`.

### Workflow Configuration

The CI/CD pipeline performs the following steps:

1. **Trigger**: Runs on push to `main` and `beta` branches, and on pull requests to `main`
2. **Environment**: Sets up Ubuntu with MySQL service for testing
3. **Build Process**:
   - Checks out the code
   - Sets up JDK 17
   - Builds the application with Maven
   - Runs tests with JaCoCo for code coverage
   - Uploads the JaCoCo report as an artifact
   - Builds and pushes the Docker image to Docker Hub

### Docker Hub Integration

The workflow automatically builds and pushes the Docker image to Docker Hub with the following tags:
- `latest`
- `v1`

This allows for easy deployment and version tracking.

### Required Secrets

To use this workflow, you need to set up the following secrets in your GitHub repository:
- `DB_PASSWORD`: Password for the MySQL database
- `DOCKER_USERNAME`: Your Docker Hub username
- `DOCKER_PASSWORD`: Your Docker Hub password or access token

## Testing

The application includes unit tests for the service layer using JUnit and Mockito. To run the tests:

```
mvn test
```

Note: When running tests on Java 23, you may need to add the following VM argument due to compatibility issues with Mockito and ByteBuddy:

```
-Dnet.bytebuddy.experimental=true
```
