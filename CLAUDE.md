# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.2 blog/bulletin board application (Java 21, Gradle) with session-based authentication, board posts with YouTube link embedding, and comments (replies). Uses Mustache for server-side rendering and H2 in-memory database.

## Build & Run Commands

```bash
# Build
gradlew build

# Run dev server with auto-restart
gradlew bootRun --continuous
# Or use: dev.bat

# Run all tests
gradlew test

# Run a single test class
gradlew test --tests "com.example.v4.user.repository.UserRepositoryTest"
```

The app runs on **port 8080** with an H2 in-memory database (no external DB setup needed). Sample data is loaded from `src/main/resources/db/data.sql`.

## Architecture

Layered architecture organized by **domain module** under `com.example.v4`:

- **user/** - Registration, login, session management (controller → service → repository)
- **board/** - Posts with CRUD, YouTube URL embedding via JSoup (controller → service → mapper → repository)
- **reply/** - Comments on board posts (controller → service → repository)
- **global/** - Cross-cutting concerns:
  - `config/` - WebMvcConfig registers interceptors and argument resolvers
  - `annotation/` - `@LoginUser` (injects SessionUser into controllers), `@Loggable`
  - `aop/` - LoggingAspect for controller/service/repository method logging
  - `dto/` - SessionUser record, Dto.User record for internal REST API
  - `exception/` - GlobalExceptionHandler with HTML vs JSON response detection
  - `resetclient/` - RestClients wrapper for internal REST API calls

Each domain module follows: **Controller → Service → Repository** with separate **Request/Response DTOs** and **JPA entities**.

## Key Patterns

- **Authentication**: Session-based. `@LoginUser` annotation + `SessionUserArgumentResolver` injects the logged-in user. `LoginUserInterceptor` exposes session user to all Mustache templates.
- **Authorization**: Owner-only checks for post/comment edit/delete are done in service layer.
- **DTOs**: Java records for request/response objects. Request DTOs use Jakarta Validation (`@NotBlank`, `@Size`, `@Email`). Response DTOs exclude sensitive fields.
- **BoardMapper**: Handles Board entity ↔ DTO conversion, including fetching writer names via internal REST API call to `/api/user/info`.
- **Board editor**: Summernote (jQuery WYSIWYG) for post create/edit forms. Content stored as HTML.
- **YouTube embedding**: `BoardService.replaceLinks()` parses content with JSoup regex, converts YouTube URLs to iframe embeds on save.
- **Exception handling**: `GlobalExceptionHandler` differentiates between HTML and JSON requests, handles RestClient errors and domain exceptions.
- **Templates**: Mustache templates in `src/main/resources/templates/` (header, index, bottom, board/*, user/*).

## Known Issues

- (없음)
