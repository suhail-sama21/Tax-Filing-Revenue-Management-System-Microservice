# Taxpayer Service API Documentation

This document provides detailed information on all API endpoints for the Taxpayer Service, including Postman examples, expected outputs, and exceptional cases.

## Base URL
`http://localhost:8080/api/taxpayers` (assuming default port; adjust as per configuration)

## Common Headers
- `Content-Type: application/json`

## API Endpoints

### 1. Create Taxpayer Profile
- **Method**: POST
- **URL**: `/profile?userId={userId}&type={type}`
- **Description**: Creates a new taxpayer profile for the given user ID.

#### Postman Request
- **Method**: POST
- **URL**: `http://localhost:8080/api/taxpayers/profile?userId=1&type=Citizen`
- **Headers**:
  - Content-Type: application/json
- **Body**: None (parameters in query)

#### Expected Success Response
- **Status Code**: 201 Created
- **Body**:
```json
{
  "userId": 1,
  "type": "Citizen",
  "taxpayerIdNumber": "1234567890",
  "taxpayerId": 1
}
```

#### Exceptional Cases
- **Invalid userId**: 400 Bad Request - "Validation failed"
- **Database error**: 500 Internal Server Error - "Database error: Connection failed"

### 2. Get Full Taxpayer Profile
- **Method**: GET
- **URL**: `/user/{userId}/full-profile`
- **Description**: Retrieves the full profile of a taxpayer, including user details.

#### Postman Request
- **Method**: GET
- **URL**: `http://localhost:8080/api/taxpayers/user/1/full-profile`
- **Headers**:
  - Content-Type: application/json
- **Body**: None

#### Expected Success Response
- **Status Code**: 200 OK
- **Body**:
```json
{
  "taxpayerId": 1,
  "taxpayerIdNumber": "1234567890",
  "type": "Citizen",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

#### Exceptional Cases
- **Taxpayer not found**: 404 Not Found - "Taxpayer not found for user ID: 1"
- **User Service unavailable**: 503 Service Unavailable - "Failed to update user profile in User Service for user ID: 1"

### 3. Update Taxpayer Profile
- **Method**: PUT
- **URL**: `/user/{userId}/profile`
- **Description**: Updates the taxpayer's profile by forwarding to the User Service.

#### Postman Request
- **Method**: PUT
- **URL**: `http://localhost:8080/api/taxpayers/user/1/profile`
- **Headers**:
  - Content-Type: application/json
- **Body**:
```json
{
  "name": "Updated Name",
  "email": "updated@example.com"
}
```

#### Expected Success Response
- **Status Code**: 200 OK
- **Body**: Same as Get Full Taxpayer Profile response

#### Exceptional Cases
- **Taxpayer not found**: 404 Not Found - "Taxpayer not found for user ID: 1"
- **User Service error**: 503 Service Unavailable - "Failed to update user profile in User Service for user ID: 1"
- **Validation error**: 400 Bad Request - "Validation failed"

### 4. Upload Document
- **Method**: POST
- **URL**: `/user/{userId}/documents/upload`
- **Description**: Uploads a document for the taxpayer.

#### Postman Request
- **Method**: POST
- **URL**: `http://localhost:8080/api/taxpayers/user/1/documents/upload`
- **Headers**:
  - Content-Type: application/json
- **Body**:
```json
{
  "docType": "ID Proof",
  "fileUri": "http://example.com/document.pdf"
}
```

#### Expected Success Response
- **Status Code**: 201 Created
- **Body**:
```json
{
  "id": 1,
  "docType": "ID Proof",
  "fileUri": "http://example.com/document.pdf",
  "verificationStatus": "Pending",
  "uploadedDate": "2026-04-08T10:00:00"
}
```

#### Exceptional Cases
- **Taxpayer not found**: 404 Not Found - "Taxpayer not found for user ID: 1"
- **File upload error**: 400 Bad Request - "File upload failed"
- **Validation error**: 400 Bad Request - "Validation failed"

### 5. Get Documents
- **Method**: GET
- **URL**: `/user/{userId}/documents`
- **Description**: Retrieves all documents for the taxpayer.

#### Postman Request
- **Method**: GET
- **URL**: `http://localhost:8080/api/taxpayers/user/1/documents`
- **Headers**:
  - Content-Type: application/json
- **Body**: None

#### Expected Success Response
- **Status Code**: 200 OK
- **Body**:
```json
[
  {
    "id": 1,
    "docType": "ID Proof",
    "fileUri": "http://example.com/document.pdf",
    "verificationStatus": "Pending",
    "uploadedDate": "2026-04-08T10:00:00"
  }
]
```

#### Exceptional Cases
- **Taxpayer not found**: 404 Not Found - "Taxpayer not found for user ID: 1"

### 6. Delete Document
- **Method**: DELETE
- **URL**: `/user/{userId}/documents/{documentId}`
- **Description**: Deletes a document if it is in 'Rejected' status.

#### Postman Request
- **Method**: DELETE
- **URL**: `http://localhost:8080/api/taxpayers/user/1/documents/1`
- **Headers**:
  - Content-Type: application/json
- **Body**: None

#### Expected Success Response
- **Status Code**: 204 No Content
- **Body**: None

#### Exceptional Cases
- **Document not found**: 404 Not Found - "Document not found with ID: 1"
- **Deletion not allowed**: 400 Bad Request - "Only rejected documents can be deleted."

### 7. Update Document Verification Status
- **Method**: PATCH
- **URL**: `/user/{userId}/documents/{documentId}/verify`
- **Description**: Updates the verification status of a document.

#### Postman Request
- **Method**: PATCH
- **URL**: `http://localhost:8080/api/taxpayers/user/1/documents/1/verify`
- **Headers**:
  - Content-Type: application/json
- **Body**:
```json
{
  "status": "Approved"
}
```

#### Expected Success Response
- **Status Code**: 200 OK
- **Body**:
```json
{
  "id": 1,
  "docType": "ID Proof",
  "fileUri": "http://example.com/document.pdf",
  "verificationStatus": "Approved",
  "uploadedDate": "2026-04-08T10:00:00"
}
```

#### Exceptional Cases
- **Taxpayer not found**: 404 Not Found - "Taxpayer not found for user ID: 1"
- **Document not found**: 404 Not Found - "Document not found with ID: 1"
- **Document ownership**: 403 Forbidden - "Document does not belong to this taxpayer"
- **Invalid status**: 400 Bad Request - "Invalid document status"

## General Exceptional Cases
- **Database errors**: 500 Internal Server Error - "Database error: [message]"
- **Unexpected errors**: 500 Internal Server Error - "An unexpected error occurred: [message]"