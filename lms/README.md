# Livestock Management API

Spring Boot backend API for managing livestock owners, cows, goats, breeding records, health records, vaccinations, messages, notifications, and report exports.

## Stack

- Java 21
- Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security
- JWT authentication
- PostgreSQL, MySQL, or H2 for local development
- Lombok, Bean Validation
- OpenAPI/Swagger
- CSV and PDF report export

## Run Locally

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8081`.

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

H2 console:

```text
http://localhost:8081/h2-console
```

Default local admin:

```json
{
  "email": "admin@livestock.local",
  "password": "Admin@12345"
}
```

Override configuration with environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/livestock
DB_USERNAME=postgres
DB_PASSWORD=password
DB_DRIVER=org.postgresql.Driver
JWT_SECRET=replace-with-a-long-production-secret
DEFAULT_ADMIN_EMAIL=admin@example.com
DEFAULT_ADMIN_PASSWORD=ChangeMe123!
```

## Authentication

Login:

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@livestock.local",
  "password": "Admin@12345"
}
```

Use the returned JWT:

```http
Authorization: Bearer <token>
```

## Main Admin Endpoints

- `POST /api/admin/owners`
- `GET /api/admin/owners`
- `PUT /api/admin/owners/{id}`
- `DELETE /api/admin/owners/{id}`
- `POST /api/admin/animals`
- `GET /api/admin/animals?includeInactive=false`
- `PUT /api/admin/animals/{id}`
- `DELETE /api/admin/animals/{id}`
- `POST /api/admin/breeding-records`
- `PUT /api/admin/breeding-records/{id}`
- `GET /api/admin/breeding-records`
- `POST /api/admin/health-records`
- `POST /api/admin/vaccination-records`
- `GET /api/admin/messages`
- `POST /api/admin/messages/reply`
- `POST /api/admin/reports/export`
- `GET /api/admin/report-logs`

## Main Owner Endpoints

- `GET /api/owner/profile`
- `GET /api/owner/animals`
- `GET /api/owner/breeding-records`
- `GET /api/owner/health-records`
- `GET /api/owner/vaccination-records`
- `POST /api/owner/messages`
- `GET /api/owner/messages`
- `GET /api/owner/notifications`
- `PATCH /api/owner/notifications/{id}/read`
- `POST /api/owner/reports/export`

## Example Requests

Create owner:

```json
{
  "user": {
    "fullName": "Jane Owner",
    "email": "jane@example.com",
    "phoneNumber": "+263771234567",
    "password": "Password123!",
    "role": "OWNER"
  },
  "nationalId": "63-123456-A-12",
  "address": "Farm 12, Gweru"
}
```

Create animal:

```json
{
  "ownerId": 1,
  "tagNumber": "COW-001",
  "animalType": "COW",
  "breed": "Brahman",
  "gender": "FEMALE",
  "dateOfBirth": "2023-08-15",
  "color": "Brown",
  "weight": 310.5,
  "animalStatus": "ACTIVE"
}
```

Add breeding record:

```json
{
  "cowId": 1,
  "matingDate": "2026-06-07",
  "maleAnimalUsed": "BULL-009",
  "pregnancyStatus": "PENDING",
  "expectedBirthDate": "2027-03-14",
  "notes": "Natural mating"
}
```

Export report:

```json
{
  "reportType": "ALL_LIVESTOCK",
  "exportFormat": "CSV",
  "fromDate": "2026-01-01",
  "toDate": "2026-12-31",
  "animalType": "COW",
  "status": "ACTIVE",
  "includeInactive": false
}
```

## Security Rules Implemented

- Passwords are BCrypt-hashed.
- JWT protects all non-login endpoints.
- Admin routes require `ADMIN`.
- Owner routes require `OWNER`.
- Owner APIs only return records linked to the current owner.
- Owners cannot create or update breeding, health, vaccination, animal, or ownership records.
- Soft delete sets `active=false`; normal owner reads only return active records.
- Audit fields are populated using JPA auditing.

## Notes

This project uses H2 by default for quick frontend integration. Set the database environment variables above to run against PostgreSQL or MySQL in production.
