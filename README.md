
# 🥷 Ninja Mission Manager

In the Hidden Villages, where shinobi once relied on scrolls, messengers, and verbal reports to coordinate their missions, a new era has arrived. 🥷✨

**Ninja Mission Manager** is a RESTful API forged with **Java** and **Spring Boot**, created to bring order, efficiency, and security to the way villages manage their ninjas and operations. No more lost scrolls or forgotten assignments—the system provides a **modern and centralized mission hub** worthy of any Hokage.

With this platform, every village gains the power to:

- 📑 **Record Missions** – From D-rank chores to S-rank covert ops, missions can be created, tracked, and assigned.
- 👤 **Manage Shinobi** – Organize ninjas, monitor their ranks, and keep tabs on who’s out in the field.
- 🛡️ **Protect Secrets** – Defend sensitive intel using **Spring Security** and **JWT**, ensuring only trusted shinobi gain access.
- 📈 **Trace Every Step** – Maintain transparency and accountability for all ninja-related activities.

By blending the spirit of the shinobi world with modern software craftsmanship, **Ninja Mission Manager** is more than just a system—it’s the **digital Hokage’s office**. 🏯🔥

## 📑 Table of Contents

- [✨ Features](#-features)
- [⚙️ Prerequisites](#️-prerequisites)
- [🚀 Installation](#-installation)
- [🔧 Configuration](#-configuration)
- [▶️ Usage](#️-usage)
    - [Examples of Endpoints](#examples-of-endpoints)
- [📦 Dependencies](#-dependencies)
- [🔒 Security](#-security)
- [📚 API Documentation](#-api-documentation)
- [🧪 Testing](#-testing)
- [👥 Contributors](#-contributors)

## ✨ Features

Ninja Mission Manager provides a powerful set of features designed to modernize the shinobi world while keeping everything secure and efficient:

- 🥷 **RESTful API with Spring Boot** – Core system for managing ninja missions, ninjas, and villages.
- 🔒 **Authentication & Authorization with JWT** – Secure access to resources, ensuring only trusted shinobi enter the system.
- 🧑‍🤝‍🧑 **Entities** – Manage essential village data: **Ninja**, **Mission**, and **Village**.
- 🎯 **Custom Filters** – Search and filter missions, ninjas, and villages with precision.
- ✅ **Validation & DTOs** – Ensure data consistency and reliability across all operations.
- 📑 **Mission Lifecycle (CRUD)** – Full create, read, update, and delete operations for missions.
- 📧 **Email Notifications** – Automatic emails for ninja registration and mission assignments.
- 🧪 **Testing Coverage (70%+)** – Reliable and well-tested system to reduce risks.
- ⚠️ **Centralized Exception Handling** – Consistent and customized error responses.  

### ⚙️ Prerequisites

Before you begin, ensure you have the following software installed on your system:

-   **Java 17 or higher**: The project is built on Java 17, so make sure you have a compatible JDK installed.
-   **Maven**: Used for dependency management and building the project.
-   **MySQL**: The database used by the application. You'll need to have it installed and running.
-   **An IDE (e.g., IntelliJ IDEA, VS Code)**: While not strictly a prerequisite for running, an IDE is highly recommended for development.

### 🚀 Installation

Follow these steps to set up and run the **Ninja Mission Manager** API on your local machine:

1.  **Clone the repository**

    Start by cloning the project from GitHub to your local environment using the following command:

    ```bash
    git clone [https://github.com/may-leth/ninja-mission-manager.git](https://github.com/may-leth/ninja-mission-manager.git)
    cd ninja-mission-manager
    ```

2.  **Configure the database**

    -   Ensure you have a **MySQL** server running.
    -   Create a new database for the project (e.g., `ninja_db`).
    -   Open the `src/main/resources/application.properties` file and update the database connection settings with your credentials:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/ninjamissionmanager
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    ```

3.  **Build and run the application**

    Use Maven to build the project and run it as a Spring Boot application:

    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```
    **With Docker**
    If you have Docker installed, you can build and run the application using Docker Compose. Make sure your database configuration is correct in `application.properties` before building the image.

    ```bash
    docker compose up -d
    ```

    The API will be available at `http://localhost:8080` once the application has started.


### 🔧 Configuration

The application's core settings are managed in the `src/main/resources/application.properties` file. You can customize the behavior of the application by modifying the following properties:

-   **Database Configuration**

    These properties control the connection to your **MySQL** database.

    ```properties
    # MySQL Database Settings
    spring.datasource.url=jdbc:mysql://localhost:3306/ninjamissionmanager?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    spring.jpa.hibernate.ddl-auto=update
    ```

-   **JWT Security Configuration**

    This is the secret key used to sign and verify **JWTs**. **It is crucial to change this value from the default for production use.**

    ```properties
    # JWT Secret Key
    jwt.secret.key=a_super_secret_key_that_you_must_change
    ```

-   **Email Service Configuration**

    These properties are required for sending email notifications.

    ```properties
    # Email Service Settings (e.g., Gmail)
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your-email@gmail.com
    spring.mail.password=your-email-app-password
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    ```
### ▶️ Usage

The **Ninja Mission Manager** API is a RESTful service that can be consumed by any HTTP client. You can use tools like **cURL**, **Postman**, or an IDE's built-in REST client to interact with the endpoints.

The API is secured using **JWT**, so you must first authenticate to get a token that you will include in the `Authorization` header of subsequent requests.

#### Examples of Endpoints

| Method | Endpoint                  | Description | Access            |
| :--- |:--------------------------| :--- |:------------------|
| **`POST`** | `/login`                  | Sends user credentials to receive a **JWT** token. | Authenticated     |
| **`POST`** | `/missions`               | Creates a new mission record. | Authenticated     |
| **`GET`** | `/ninjas/{id}`            | Retrieves details for a specific ninja by their ID. | Role-Based Access |
| **`GET`** | `/villages`               | Retrieves a list of all villages. | Public            |
| **`GET`** | `/villages/{id}`          | Retrieves details for a specific village by its ID. | Public            |
| **`GET`** | `/missions?rank=S` | Retrieves a list of missions, filtered by the specified rank. | Authenticated     |

### 📦 Dependencies

The project leverages the following key dependencies managed by Maven:

-   **`spring-boot-starter-web`**: Builds web applications with RESTful services using Spring MVC.
-   **`spring-boot-starter-data-jpa`**: Provides powerful Spring Data features for JPA, simplifying database operations.
-   **`spring-boot-starter-security`**: Secures the application with robust authentication and authorization features.
-   **`spring-boot-starter-mail`**: Enables the sending of email notifications.
-   **`mysql-connector-j`**: The JDBC driver for connecting to the **MySQL** database.
-   **`jjwt-api`**, **`jjwt-impl`**, **`jjwt-jackson`**: A suite of libraries for handling **JSON Web Tokens (JWT)**.
-   **`lombok`**: Reduces boilerplate code (e.g., getters, setters, constructors).
-   **`springdoc-openapi-starter-webmvc-ui`**: Automatically generates API documentation in OpenAPI 3 format and provides a Swagger UI.
-   **`spring-boot-starter-test`**: Includes essential libraries like **JUnit**, **Mockito**, and **AssertJ** for writing unit and integration tests.

### 📚 API Documentation

This project uses **SpringDoc OpenAPI** to automatically generate interactive API documentation with **Swagger UI**. Once the application is running, you can access the full documentation, including all available endpoints, request/response models, and security schemes.

To view the documentation, simply navigate to the following URL in your web browser:
```
http://localhost:8080/swagger-ui.html
```
From there, you can explore the API, view request/response examples, and even test the endpoints directly from the browser.

### 🧪 Testing

The **Ninja Mission Manager** project is committed to code quality and stability, with a focus on comprehensive testing. The current test suite provides a coverage of over **70%**, ensuring that core functionalities are reliable and free of regressions.

The project includes:

-   **Unit Tests**: Focused on testing individual components in isolation.
-   **Integration Tests**: Validating that different parts of the application work together correctly (e.g., controllers, services, and repositories).

To run all the tests, use the following Maven command:

```bash
./mvnw test
```
### 👥 Contributors

This project is the result of a passion for both technology and the shinobi world. I believe that, just like in a village, every contribution strengthens the community.

A special thanks to the following ninja, whose skills were woven into the code:

-   [May](https://github.com/may-leth) - The **Ninja of the Hidden Code**, initial creator and steadfast maintainer of the mission system.

If you would like to join our squad, feel free to:

-   Open an issue to report a jutsu (bug) or suggest a new mission (feature).
-   Submit a pull request with your scrolls (improvements).