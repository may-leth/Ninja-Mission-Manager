# Ninja-Mission-Manager

This project is a RESTful service developed with Spring Boot to manage ninja missions. It allows performing CRUD operations (Create, Read, Update, Delete) on the missions, assigning them a name, rank, designated ninja, and completion status.

The main goal of this project, aside from its functionality, is to demonstrate the implementation of robust Integration Tests that adhere to Clean Code principles in a Spring Boot application.
> 💡 This project was built for a class demonstration

## Features
- Layered architecture (Controller, Service, Repository)
- Separation of concerns using DTOs and Mappers
- Validation with @Valid
- Descriptive integration tests using @Nested and @DisplayName
- SQL scripts for test data (@Sql)

##  Project Structure

```
src/
├── main/
│    ├── controllers/                # REST API Endpoints
│    │   └── MissionController.java
│    │── dtos/                       # Data Transfer Objects
│    ├── models/                     # Domain Model
│    │   └── Mission.java
│    │   └── Rank.java               # Enum for mission ranks                       
│    │
│    ├── repositories/               # Data Access Layer 
│    │   └── MissionRepository.java
│    │
│    └── services/                   # Business Logic Layer
│         └── MissionService.java
│
├── test/
│   └──java/
│       └── ninjamissionmanager/  
│       └── MissionControllerIntegrationTest.java # Integration tests
│
└── resources/
    └── test-data.sql     # SQL script for test data setup (used by integration tests)

```
## Getting Started
### Requirements
- JDK 17+
- Maven

### Clone the repository
```bash
git clone https://github.com/may-leth/Ninja-Mission-Manager.git
cd ninja-mission-manager
```
## API Endpoints

The API exposes the following endpoints for mission management:

| HTTP Method | Endpoint          | Description                                    | Request Body          | Response Status | Response Body / Notes                                                                   |
| :---------- | :---------------- | :--------------------------------------------- | :-------------------- | :-------------- | :-------------------------------------------------------------------------------------- |
| `GET`       | `/missions`       | Retrieves all missions.                        | *None* | `200 OK`        | `List<MissionResponse>`                                                                 |
| `GET`       | `/missions/{id}`  | Retrieves a mission by its ID.                 | *None* | `200 OK`        | `MissionResponse` if found. `404 Not Found` if mission does not exist.                  |
| `POST`      | `/missions`       | Creates a new mission.                         | `MissionRequest` (JSON) | `201 Created`   | `MissionResponse` of the created mission. `400 Bad Request` for invalid input.          |
| `PUT`       | `/missions/{id}`  | Updates an existing mission by its ID.         | `MissionRequest` (JSON) | `200 OK`        | `MissionResponse` of the updated mission. `400 Bad Request` for invalid input, `404 Not Found` if mission does not exist. |
| `DELETE`    | `/missions/{id}`  | Deletes a mission by its ID.                   | *None* | `200 OK`        | `MissionResponse` of the deleted mission (for confirmation). `404 Not Found` if mission does not exist. |

