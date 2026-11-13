# E-Bank Microservices Architecture (Spring Boot & Spring Cloud)

Distributed microservices application built with **Spring Boot 3.4.2**, **Spring Cloud 2024.0.0**, and **Java 17**.

---

## Architecture Overview

| Service | Module | Port | Description |
| :--- | :--- | :---: | :--- |
| **Discovery Service** | `discovery-service` | `8761` | Netflix Eureka Server for service registration and discovery |
| **Customer Service** | `customer-service` | `8056` | Manages customer entities using Spring Data JPA & in-memory H2 database |
| **E-Bank Service** | `ebank-service` | `8053` | Manages bank accounts, communicates with `customer-service` via **OpenFeign**, and implements **Resilience4j** fault tolerance |
| **Gateway Service** | `gateway-service` | `8888` | Reactive Spring Cloud Gateway with dynamic Eureka route discovery |

---

## Tech Stack

- **Java 17** & **Spring Boot 3.4.2**
- **Spring Cloud** (Netflix Eureka, OpenFeign, Spring Cloud Gateway, Circuit Breaker)
- **Resilience4j** (`@CircuitBreaker` with graceful fallback responses)
- **Spring Data JPA** & **H2 Database**
- **SpringDoc OpenAPI (Swagger UI)**
- **Lombok** & **Maven**

---

## Startup Order

1. **`DiscoveryServiceApplication`** (`8761`) — Wait until the Eureka dashboard is accessible at `http://localhost:8761`
2. **`CustomerServiceApplication`** (`8056`) — Seeds initial customer records (`Mohamed`, `Iman`, `Yassin`)
3. **`EbankServiceApplication`** (`8053`) — Seeds bank accounts linked to customer IDs via `CustomerRestClient`
4. **`GatewayServiceApplication`** (`8888`) — Enables dynamic routing to registered services

---

## Key Endpoints

### Eureka Dashboard
- `http://localhost:8761`

### API Gateway Routes
- **Get all customers:** `GET http://localhost:8888/customer-service/customers`
- **Get customer by ID:** `GET http://localhost:8888/customer-service/customers/{id}`
- **Get all bank accounts (with customer details):** `GET http://localhost:8888/ebank-service/accounts`
- **Get bank account by ID:** `GET http://localhost:8888/ebank-service/accounts/{id}`

### Swagger UI Documentation
- **Customer Service:** `http://localhost:8056/swagger-ui/index.html`
- **E-Bank Service:** `http://localhost:8053/swagger-ui/index.html`

---

## Fault Tolerance (Resilience4j)

`CustomerRestClient` in `ebank-service` is protected with `@CircuitBreaker(name = "customerService", fallbackMethod = "getDefaultCustomer")`. If `customer-service` becomes unavailable, `ebank-service` returns account data gracefully with `"Not Available"` customer placeholders instead of failing, and automatically recovers once `customer-service` is back online.
