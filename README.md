# CarbonTrack

CarbonTrack is a Spring Boot REST API for tracking personal activities and estimating their greenhouse-gas emissions in kilograms of CO₂ equivalent (kg CO₂e).

## Current scope

The Milestone 1 backend includes:

- User registration and login
- JWT-based authentication for protected endpoints
- User profile retrieval and updates
- Activity logging for transport, electricity, food, and shopping categories
- Database-backed emission factors and CO₂e calculation
- Activity history for the authenticated user
- Password reset using email OTP verification
- MySQL schema management through Flyway migrations
- Automated tests using the Maven test lifecycle

> **Emission-factor note:** values in `V10__insert_test_emission_factors.sql` are marked as test data. Verify each factor and its source before using the application for real-world or production reporting.

## Technology stack

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT (JJWT 0.12.6)
- MySQL 8.x and MySQL Workbench
- Flyway database migrations
- Maven Wrapper
- Jakarta Bean Validation
- Spring Boot Mail
- JUnit / Spring Boot Test

## Prerequisites

Install and configure:

1. JDK 17
2. MySQL Server and MySQL Workbench
3. Git (optional, for cloning)
4. An internet connection for the first Maven dependency download

Confirm Java is available:

```powershell
java -version
```

The output should report Java 17.

## Project structure

```text
CarbontrackApplication/
├── README.md
└── carbontrack-backend/
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/carbontrack/carbontrackbackend/
        │   │   ├── controller/
        │   │   ├── dto/
        │   │   ├── entity/
        │   │   ├── exception/
        │   │   ├── repository/
        │   │   ├── security/
        │   │   └── service/
        │   └── resources/
        │       ├── application.properties
        │       └── db/migration/
        └── test/
```

## 1. Create the MySQL database

Open MySQL Workbench and run:

```sql
CREATE DATABASE carbontrack;
```

The application uses the database URL below by default:

```text
jdbc:mysql://localhost:3306/carbontrack
```

The application connects as `root` by default unless `DB_USERNAME` is set. Use the username and password configured on your own MySQL installation. Do not commit credentials to Git.

Flyway runs the SQL migrations from:

```text
src/main/resources/db/migration/
```

The migrations create the application tables and load initial **test** emission-factor records. Do not edit migrations that have already been applied to a database; create a new versioned migration for future schema or reference-data changes.

## 2. Configure environment variables in PowerShell

Open PowerShell in the `carbontrack-backend` directory. Set the variables in the same terminal that you will use to run the application.

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/carbontrack"
$env:DB_USERNAME = "root"
$secure = Read-Host "Enter your MySQL password" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password
```

The JWT service expects `JWT_SECRET` to be a **Base64-encoded key with sufficient length for HS256**. Generate a secure key using a trusted cryptographic tool, then set it as an environment variable. For example, if OpenSSL is installed:

```powershell
$env:JWT_SECRET = (openssl rand -base64 32).Trim()
```

Password-reset email requires SMTP configuration. For a Gmail SMTP account, configure:

```powershell
$env:MAIL_USERNAME = "your-sender-address@example.com"
$secureMail = Read-Host "Enter the SMTP/app password" -AsSecureString
$env:MAIL_PASSWORD = [System.Net.NetworkCredential]::new("", $secureMail).Password
```

Use the SMTP app password or credentials approved for your mail provider. Never commit these values, paste them into issue trackers, or include them in screenshots.

These PowerShell variables apply only to the current terminal session. Set them again in a new terminal, or configure them securely in your IDE's run configuration.

## 3. Run the application

From the `carbontrack-backend` directory:

```powershell
.\mvnw.cmd spring-boot:run
```

The API should be available at:

```text
http://localhost:8080
```

Health check:

```http
GET http://localhost:8080/api/health
```

Expected response:

```json
{
  "application": "CarbonTrack",
  "status": "UP"
}
```

## 4. Run automated tests

From the `carbontrack-backend` directory:

```powershell
.\mvnw.cmd clean test
```

A successful run ends with:

```text
BUILD SUCCESS
```

Review the Maven output for the test count and any skipped, failed, or errored tests. Test reports are normally written under:

```text
target/surefire-reports/
```

## 5. API quick start

Base URL:

```text
http://localhost:8080
```

### Register

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "sampleuser",
  "email": "sampleuser@example.com",
  "password": "ChangeThisExample123!"
}
```

Use a unique email and username for each registration test. This is an example only; do not use a real password in documentation.

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "sampleuser@example.com",
  "password": "ChangeThisExample123!"
}
```

Copy the JWT from the successful response. The response structure is defined by `AuthResponseDTO`.

### Use a protected endpoint

In Postman, select **Authorization → Bearer Token** and paste the JWT. Do not add the word `Bearer` yourself if Postman adds it automatically.

```http
GET /api/test/protected
Authorization: Bearer <JWT>
```

A request without a valid token should be rejected.

### Get and update the current user's profile

```http
GET /api/users/me
Authorization: Bearer <JWT>
```

```http
PUT /api/users/me
Authorization: Bearer <JWT>
Content-Type: application/json
```

Use the fields defined in `UpdateProfileRequestDTO` when updating a profile.

### Create an activity

```http
POST /api/activities
Authorization: Bearer <JWT>
Content-Type: application/json
```

```json
{
  "category": "TRANSPORT",
  "activityType": "CAR_PETROL",
  "quantity": 12,
  "unit": "km",
  "logDate": "2026-10-09"
}
```

The backend looks up the matching emission factor by activity type, unit, and effective date, then calculates:

```text
CO₂e (kg) = quantity × kg CO₂e per unit
```

For example, if the configured test factor is `0.18 kg CO₂e/km`, then `12 km × 0.18 = 2.16 kg CO₂e`. This example uses test data and is not a verified scientific result.

### List the current user's activities

```http
GET /api/activities/me
Authorization: Bearer <JWT>
```

### Password reset

The password-reset endpoints are:

- `POST /api/auth/forgot-password`
- `POST /api/auth/verify-otp`
- `POST /api/auth/reset-password`

Use the request DTOs in `com.carbontrack.carbontrackbackend.dto` for the required JSON fields. The flow requires the configured SMTP service to deliver the OTP.

## 6. Verify activity persistence in MySQL Workbench

After successfully creating an activity, run:

```sql
USE carbontrack;

SELECT
    id,
    user_id,
    category,
    activity_type,
    quantity,
    unit,
    co2e_kg,
    log_date
FROM activity_logs
ORDER BY id DESC
LIMIT 10;
```

Inspect the available emission factors:

```sql
SELECT
    id,
    activity_type,
    unit,
    kg_co2e_per_unit,
    source,
    effective_date
FROM emission_factors
ORDER BY activity_type, effective_date DESC;
```

A missing matching emission factor should not be replaced with a guessed value. Add or update factors only from a documented, appropriate source. For an already-migrated database, use a new Flyway migration rather than changing an applied migration.

## 7. Milestone 1 final verification checklist

- [ ] `GET /api/health` returns `UP`.
- [ ] Flyway migrations complete and the expected tables exist in MySQL.
- [ ] Registration and login work.
- [ ] Protected endpoints reject missing/invalid JWTs and accept a valid token.
- [ ] `GET /api/users/me` and `PUT /api/users/me` work with a JWT.
- [ ] `POST /api/activities` calculates CO₂e and saves the activity.
- [ ] `GET /api/activities/me` returns the authenticated user's activity history.
- [ ] Invalid categories, activity types, quantities, units, dates, and missing factors are rejected as expected.
- [ ] Password-reset OTP flow works with the configured SMTP account.
- [ ] `.\mvnw.cmd clean test` finishes with `BUILD SUCCESS`; save the test summary.
- [ ] No real credentials or secrets are present in the current source files or tracked repository.
- [ ] Initial emission factors have documented sources before any production use.
- [ ] README and Postman/database screenshots are ready for the mentor.

## Security notes

- Never commit database passwords, JWT signing keys, SMTP credentials, OTPs, or reset tokens.
- If a real secret was committed previously, removing it from the latest file is not enough; rotate the secret and review repository history.
- Keep local configuration and generated build output out of version control.
- Use verified emission-factor sources before presenting estimates as real-world measurements.

## Current limitations

This repository is the **backend**; it does not by itself provide a React frontend. The database currently uses MySQL as configured for this project. Confirm any institutional requirements with the mentor before changing the selected database or framework version.
