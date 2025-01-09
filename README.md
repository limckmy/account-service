# Spring Boot Application - Batch Job & REST API

## Overview
This application consists of two main components:

1. **Batch Job**: Every night at midnight (12 AM), a batch job runs to consume files (`customer.csv`, `account.csv`) and migrates their data into a database.
2. **REST API**: Provides endpoints for managing users and customer accounts.

### Features:
- **User Management**:
  - Register User
  - Generate Token (JWT Authentication)
  - Find User
- **Customer Management**:
  - Search Account by customer ID, account number, or description with pagination
  - Update Account Description

## Prerequisites

Ensure you have the following installed on your system:
- Java 17 or later
- Maven (for building the project)
- A relational database (PostgreSQL) for data storage

## Setup

### 1. Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/yourusername/your-repository.git
cd your-repository
```
### 2. Configure Application Properties
[application.properties](.\src\main\resources\application.properties)

### 3. Install Dependencies
```bash
mvn clean install
```
## Running the Application
### 1. Build the Application
```bash
mvn clean package
```
### 2. Running the Service
```bash
java -jar target/account-service-1.0-SNAPSHOT.jar
```

### 3. Batch Job Execution 
The batch job will automatically run at midnight every day (12 AM) and process the CSV files ([customer.csv](.\src\main\resources\customer.csv) and [account.csv](.\src\main\resources\account.csv)).
Use a [Property](.\src\main\resources\application.properties) to Enable Immediate Execution 
```properties
batch.job.run-immediately=true
```

## API Endpoints

### User Management
#### 1. Register User
```bash
POST /v1/users/register
```

Request Body : 
```json
{
    "username" : "bob",
    "password" : "password123",
    "email" : "bob@limckmy.org"
}
```

#### 2. Generate Token (JWT Authentication)
```bash
POST /v1/users/login
```

Request Body :
```json
{
    "username" : "bob",
    "password" : "password123"
}
```
Response :
```json
{
    "accessToken": "your_jwt_token"
}
```

### Customer Management

#### 1. Search Account
```bash
GET /v1/accounts/search?customerId=1&accountNumber=ACC1001&description=checking&page=0&size=5
Authorization: Bearer your_jwt_token
```
Response :
```json
{
  "content": [
    {
      "customerId": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "555-1234",
      "accounts": [
        {
          "accountId": 1,
          "accountNumber": "ACC1001",
          "accountType": "Savings",
          "accountDescription": "Primary saving account",
          "balance": 5000.00
        }
      ]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 1,
  "empty": false
}
```

#### 2. Update Account Description
```bash
PATCH /v1/accounts/1/description
Authorization: Bearer your_jwt_token
```
Request Body :
```json
{
    "description": "Primary saving account updated"
}
```
Response :
```json
{
    "accountId": 1,
    "accountNumber": "ACC1001",
    "accountType": "Savings",
    "accountDescription": "Primary saving account updated",
    "balance": 5000.00
}
```

---
### References
- Class Diagram
  - [Entity](documentation/class-diagram/entity.png)
  - [MVC](documentation/class-diagram/mvc.png)
- [Activity Diagram](documentation/activity-diagram.png)
- [Postman Collection](Account%20Service.postman_collection.json)
- [OpenAPI](openapi.json)