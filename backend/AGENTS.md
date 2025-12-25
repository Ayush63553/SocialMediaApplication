## Role
You are a senior full stack engineer building a Angular Typescript and Spring Boot RESTful web application.

## Task
Create a complete Spring Boot REST API project for managing **Local Community** with full CRUD functionality.

## Context
- Use **Java 21** and **Spring Boot 3.x**.
- Use **MySQL** as the local database.
- Follow the standard layered architecture:  
  `Controller → Service → Repository`.
- Use **`com.infy`** as the root package.
- The **Local Community** should have the following fields:  
  `[field1], [field2], [field3], ...`
- Use **Lombok and ModelMapper** to reduce boilerplate code.
- Add **basic validation** using annotations like `@NotNull`, `@Size`, etc.
- Include **example data** in `data.sql`.
- Configure database connection in `application.properties`.

## Reasoning
- Follow RESTful API design principles.
- Use DTOs for API responses; convert with ModelMapper.
- Handle global exceptions with @RestControllerAdvice and service-level errors using @Aspect.
- Ensure proper use of Spring annotations:
  - `@RestController`, `@Service`, `@Repository`
  - `@RequestMapping`, `@PathVariable`, `@RequestBody`, `@Valid`

## Output Format
- ✅ Entity class with JPA annotations  
- ✅ DTO classes for request and response with validation annotations and ModelMapper conversion
- ✅ Repository interface extending `JpaRepository`  
- ✅ Service interface and implementation  
- ✅ REST controller with CRUD endpoints  
- ✅ Global exception handler  
- ✅ Aspect class for service-layer exception handling
- ✅ `data.sql` with sample data  
- ✅ `application.properties` configured for MySQL

## Stop Condition
Stop after generating all components and ensuring the application builds and runs successfully with sample data. Also ensure the application is built successfully while resolving the errors and continously mapping the requirements
