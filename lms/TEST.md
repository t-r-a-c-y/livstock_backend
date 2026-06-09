# Livestock Management API Swagger Test Guide

Use this guide to test the backend from Swagger UI.

Swagger URL:

```text
http://localhost:8081/swagger-ui.html/index.html
```

Start the application first:

```bash
mvn spring-boot:run
```

## 1. Login As Admin

Open:

```text
POST /api/auth/login
```

Request body:

```json
{
  "email": "admin@livestock.local",
  "password": "Admin@12345"
}
```

Expected result:

- Status `200`
- Response says password was verified and OTP was sent
- The OTP appears in the application terminal when `OTP_LOG_TO_CONSOLE=true`

Do not authorize yet. You only get the token after verifying OTP.

To get the token, open:

```text
POST /api/auth/login/verify
```

Request body:

```json
{
  "email": "admin@livestock.local",
  "code": "123456"
}
```

Use the real terminal OTP.

Important: do not use `POST /api/auth/otp/verify` for login. That endpoint is only for testing standalone OTP delivery and will not return a JWT token.

Expected result:

- Status `200`
- Response contains a `token`
- Role is `ADMIN`

Copy the token.

In Swagger, click **Authorize** and enter:

```text
Bearer YOUR_ADMIN_TOKEN_HERE
```

## 1A. Optional Standalone OTP Test

Open:

```text
POST /api/auth/otp/request
```

Request body:

```json
{
  "email": "admin@livestock.local"
}
```

Expected result:

- Status `200`
- OTP is sent to the configured email address or printed in the terminal

Open:

```text
POST /api/auth/otp/verify
```

Request body:

```json
{
  "email": "admin@livestock.local",
  "code": "123456"
}
```

Expected result:

- Status `200` if the real emailed code is used
- Status `400` if the code is wrong or expired

## 2. Create An Owner

Open:

```text
POST /api/admin/owners
```

Request body:

```json
{
  "user": {
    "fullName": "Jane Owner",
    "email": "jane.owner@example.com",
    "phoneNumber": "+263771234567",
    "password": "Password123!",
    "role": "OWNER"
  },
  "nationalId": "63-123456-A-12",
  "address": "Farm 12, Gweru"
}
```

Expected result:

- Status `201`
- Response contains owner `id`
- User role is `OWNER`

Save the owner `id`.

## 3. View Owners

Open:

```text
GET /api/admin/owners
```

Expected result:

- Status `200`
- The owner created above appears in the page content

## 4. Create A Cow

Open:

```text
POST /api/admin/animals
```

Replace `ownerId` with the owner id from step 2.

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

Expected result:

- Status `201`
- Animal type is `COW`
- Response contains animal `id`

Save the cow `id`.

## 5. Create A Goat

Open:

```text
POST /api/admin/animals
```

```json
{
  "ownerId": 1,
  "tagNumber": "GOAT-001",
  "animalType": "GOAT",
  "breed": "Boer",
  "gender": "MALE",
  "dateOfBirth": "2024-02-10",
  "color": "White",
  "weight": 45.2,
  "animalStatus": "ACTIVE"
}
```

Expected result:

- Status `201`
- Animal type is `GOAT`

Save the goat `id`.

## 6. View All Animals As Admin

Open:

```text
GET /api/admin/animals
```

Expected result:

- Status `200`
- Cow and goat appear in the results

## 7. Add Breeding Record For Cow

Open:

```text
POST /api/admin/breeding-records
```

Replace `cowId` with the cow id.

```json
{
  "cowId": 1,
  "matingDate": "2026-06-09",
  "maleAnimalUsed": "BULL-009",
  "pregnancyStatus": "PENDING",
  "expectedBirthDate": "2027-03-16",
  "actualBirthDate": null,
  "notes": "Natural mating"
}
```

Expected result:

- Status `201`
- Pregnancy status is `PENDING`
- A notification is created for the owner

Negative test:

- Try using a goat id as `cowId`
- Expected result: status `400`

## 8. Update Breeding Record

Open:

```text
PUT /api/admin/breeding-records/{id}
```

Request body:

```json
{
  "cowId": 1,
  "matingDate": "2026-06-09",
  "maleAnimalUsed": "BULL-009",
  "pregnancyStatus": "PREGNANT",
  "expectedBirthDate": "2027-03-16",
  "actualBirthDate": null,
  "notes": "Pregnancy confirmed"
}
```

Expected result:

- Status `200`
- Pregnancy status changes to `PREGNANT`
- Owner receives another notification

## 9. Add Health Record

Open:

```text
POST /api/admin/health-records
```

Replace `animalId` with the cow or goat id.

```json
{
  "animalId": 1,
  "diagnosis": "Mild infection",
  "treatment": "Antibiotic treatment",
  "medication": "Oxytetracycline",
  "veterinarianName": "Dr Moyo",
  "visitDate": "2026-06-09",
  "nextVisitDate": "2026-06-23",
  "notes": "Monitor appetite"
}
```

Expected result:

- Status `201`
- Health record is saved
- Owner receives a health notification

## 10. Add Vaccination Record

Open:

```text
POST /api/admin/vaccination-records
```

```json
{
  "animalId": 1,
  "vaccineName": "FMD Vaccine",
  "vaccinationDate": "2026-06-09",
  "nextDueDate": "2026-06-16",
  "administeredBy": "Dr Moyo",
  "notes": "Annual vaccination"
}
```

Expected result:

- Status `201`
- Vaccination record is saved
- Owner receives a notification

## 11. Login As Owner

Open:

```text
POST /api/auth/login
```

Request body:

```json
{
  "email": "jane.owner@example.com",
  "password": "Password123!"
}
```

Expected result:

- Status `200`
- Response says password was verified and OTP was sent

Open:

```text
POST /api/auth/login/verify
```

Request body:

```json
{
  "email": "jane.owner@example.com",
  "code": "123456"
}
```

Expected result:

- Status `200`
- Role is `OWNER`
- Response contains owner JWT token

Click **Authorize** in Swagger and replace the admin token with:

```text
Bearer YOUR_OWNER_TOKEN_HERE
```

## 12. View Owner Profile

Open:

```text
GET /api/owner/profile
```

Expected result:

- Status `200`
- Profile belongs to the logged-in owner

## 13. View Owner Animals

Open:

```text
GET /api/owner/animals
```

Expected result:

- Status `200`
- Only this owner's cows and goats are returned

## 14. View Owner Records

Test these endpoints:

```text
GET /api/owner/breeding-records
GET /api/owner/health-records
GET /api/owner/vaccination-records
```

Expected result:

- Status `200`
- Only records for the logged-in owner's animals are returned

## 15. Confirm Owner Cannot Use Admin APIs

While still authorized as owner, try:

```text
POST /api/admin/breeding-records
GET /api/admin/animals
DELETE /api/admin/animals/{id}
```

Expected result:

- Status `403`
- Owner is blocked from admin endpoints

## 16. Owner Sends Message To Admin

Open:

```text
POST /api/owner/messages
```

```json
{
  "animalId": 1,
  "subject": "Cow health question",
  "messageBody": "Please review the latest health record for this cow."
}
```

Expected result:

- Status `201`
- Message status is `SENT`
- Admin receives a notification

Save the message `id`.

## 17. Admin Views And Replies To Message

Authorize again as admin.

Open:

```text
GET /api/admin/messages
```

Expected result:

- Status `200`
- Owner message is visible

Open:

```text
POST /api/admin/messages/reply
```

```json
{
  "messageId": 1,
  "messageBody": "Thank you. The animal should be checked again on the next visit date."
}
```

Expected result:

- Status `200`
- Reply message is created
- Owner receives a notification

## 18. Owner Views Replies

Authorize again as owner.

Open:

```text
GET /api/owner/messages
```

Expected result:

- Status `200`
- Owner can see their sent messages and admin replies

## 19. Owner Views Notifications

Open:

```text
GET /api/owner/notifications
```

Expected result:

- Status `200`
- Notifications from breeding, health, vaccination, and admin reply are visible

Save a notification `id`.

Open:

```text
PATCH /api/owner/notifications/{id}/read
```

Expected result:

- Status `200`
- `readStatus` becomes `true`

## 20. Export Admin CSV Report

Authorize as admin.

Open:

```text
POST /api/admin/reports/export
```

```json
{
  "reportType": "ALL_LIVESTOCK",
  "exportFormat": "CSV",
  "fromDate": "2026-01-01",
  "toDate": "2026-12-31",
  "ownerId": null,
  "animalType": null,
  "status": null,
  "includeInactive": false
}
```

Expected result:

- Status `200`
- CSV file downloads
- File opens in Excel

## 21. Export Admin PDF Report

Open:

```text
POST /api/admin/reports/export
```

```json
{
  "reportType": "BREEDING_RECORDS",
  "exportFormat": "PDF",
  "fromDate": "2026-01-01",
  "toDate": "2026-12-31",
  "ownerId": null,
  "animalType": null,
  "status": null,
  "includeInactive": false
}
```

Expected result:

- Status `200`
- PDF file downloads
- PDF includes title, generated date, generated by, table data, and page text

## 22. View Report Logs

Open:

```text
GET /api/admin/report-logs
```

Expected result:

- Status `200`
- Generated CSV and PDF reports appear in the logs

## 23. Export Owner Report

Authorize as owner.

Open:

```text
POST /api/owner/reports/export
```

```json
{
  "reportType": "ALL_LIVESTOCK",
  "exportFormat": "CSV",
  "fromDate": "2026-01-01",
  "toDate": "2026-12-31",
  "ownerId": null,
  "animalType": null,
  "status": null,
  "includeInactive": false
}
```

Expected result:

- Status `200`
- Only the logged-in owner's livestock is exported

Negative test:

```json
{
  "reportType": "ALL_LIVESTOCK",
  "exportFormat": "CSV",
  "includeInactive": true
}
```

Expected result:

- Status `403`
- Owners cannot export inactive records

## 24. Soft Delete Test

Authorize as admin.

Open:

```text
PATCH /api/admin/animals/{id}/inactive
```

Expected result:

- Status `204`
- Animal is not permanently deleted

Then test:

```text
GET /api/admin/animals?includeInactive=false
```

Expected result:

- Deleted animal does not appear

Then test:

```text
GET /api/admin/animals?includeInactive=true
```

Expected result:

- Deleted animal appears with `active=false`

Authorize as owner and test:

```text
GET /api/owner/animals
```

Expected result:

- Inactive animal does not appear

## 25. Validation Tests

Try creating an owner with:

```json
{
  "user": {
    "fullName": "",
    "email": "not-an-email",
    "phoneNumber": "",
    "password": "123",
    "role": "OWNER"
  },
  "nationalId": "",
  "address": ""
}
```

Expected result:

- Status `400`
- Validation errors are returned

Try logging in with a wrong password:

```json
{
  "email": "jane.owner@example.com",
  "password": "wrong-password"
}
```

Expected result:

- Status `401` or `403`
- Token is not returned

## Suggested Full Test Order

1. Login as admin
2. Create owner
3. Create cow
4. Create goat
5. Add breeding record
6. Add health record
7. Add vaccination record
8. Login as owner
9. View owner profile
10. View owner animals and records
11. Confirm owner cannot access admin APIs
12. Send owner message
13. Login as admin
14. Reply to message
15. Login as owner
16. View reply and notifications
17. Export owner reports
18. Login as admin
19. Export admin reports
20. View report logs
21. Test soft delete
22. Test validation errors
