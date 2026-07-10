# AccountShield

AccountShield is a Spring Boot API for user account management, authentication, refresh token rotation, and email verification.

The application source lives in the `accountshield/` directory.

## Tech Stack

- Java 17
- Spring Boot 4.1
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- springdoc-openapi

## Requirements

- Java 17
- Maven 3.9+ or the bundled `mvnw`
- PostgreSQL 18
- Docker and Docker Compose for containerized runs

## Local Run

```bash
cd accountshield
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080`.

By default the application starts with the `dev` profile:

```yaml
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}
```

You can override it with:

```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

## Docker Run

```bash
cd accountshield
docker compose up --build
```

Services:

- `db` on `localhost:5433`
- `app` on `localhost:8080`

## Environment Variables

### Application

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `DATABASE_URL` | Yes | - | JDBC URL for PostgreSQL |
| `DATABASE_USERNAME` | Yes | - | Database username |
| `DATABASE_PASSWORD` | Yes | - | Database password |
| `JWT_SECRET` | No | `accountshield-development-secret-key-change-me-please-32` | Secret used to sign JWTs |
| `JWT_EXPIRATION` | No | `3600000` | Access token lifetime in ms |
| `JWT_REFRESH_EXPIRATION` | No | `2592000000` | Refresh token lifetime in ms |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:3000` | Allowed frontend origins |
| `CORS_ALLOW_CREDENTIALS` | No | `true` | Whether CORS allows credentials |
| `CORS_ALLOWED_METHODS` | No | `*` | Allowed HTTP methods |
| `CORS_ALLOWED_HEADERS` | No | `*` | Allowed request headers |
| `CORS_MAX_AGE` | No | `3600` | CORS cache age in seconds |
| `ADMIN_NAME` | No | `admin` | Seed admin name |
| `ADMIN_EMAIL` | No | `admin@gmail.com` | Seed admin email |
| `ADMIN_PASSWORD` | No | `admin@12345` | Seed admin password |
| `PORT` | No | `8080` | HTTP port |

### Database

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `POSTGRES_DB` | Yes | - | Database name |
| `POSTGRES_USER` | Yes | - | Database user |
| `POSTGRES_PASSWORD` | Yes | - | Database password |
| `POSTGRES_PORT` | No | `5432` | Internal PostgreSQL port |

## Docker Environment Files

- `accountshield/.env.db.dev` controls the database container
- `accountshield/.env.app.dev` controls the application container

## API Notes

- Base API path: `/api`
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI docs: `/v3/api-docs`
- Auth header: `Authorization: Bearer <token>`

### API Versioning

The API uses media type parameter based content negotiation for versioning. The default version is `1.0`, so clients can call endpoints without specifying a version, or explicitly request one with the `Accept` header:

```http
Accept: application/vnd.devlab.dev+json;v=1.0
```

Supported versions are configured in `WebConfig`:

- `1.0`
- `2.0`
- `3.0`

This keeps endpoint paths stable, for example:

```http
POST /api/auth/login
```

instead of embedding the version in the URL.

### Profiles

The project uses Spring profiles to separate development behavior from production behavior:

- `dev`: email verification codes are returned in the registration response to make local testing simple.
- non-`dev`: verification codes are generated and stored, but not returned in the response. This simulates the production behavior where a mail provider would deliver the code.

Email verification is backed by an in-memory `ConcurrentMapCache` store. This is enough for the current simulation and local development flow; a real production setup can replace it with an external cache or mail provider.

## Useful Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/verify-email`

## Testing

Run the full test suite from the application directory:

```bash
cd accountshield
./mvnw test
```

The test suite uses:

- JUnit 5
- Mockito
- AssertJ
