# E-Bank Microservices Architecture (Spring Boot, Spring Cloud & Spring AI)

Distributed microservices application built with **Spring Boot 3.4.2**, **Spring Cloud 2024.0.0**, **Spring AI 1.0.0**, **Model Context Protocol (MCP)**, and **Java 17**.

---

## Architecture Overview

| Service | Module | Port | Description |
| :--- | :--- | :---: | :--- |
| **Discovery Service** | `discovery-service` | `8761` | Netflix Eureka Server for service registration and discovery |
| **Customer Service** | `customer-service` | `8056` | Manages customer entities using Spring Data JPA & in-memory H2 database, and exposes tools via `customer-mcp-server` |
| **E-Bank Service** | `ebank-service` | `8053` | Manages bank accounts, communicates with `customer-service` via **OpenFeign** with **Resilience4j** fault tolerance, and exposes tools via `ebank-mcp-server` |
| **Gateway Service** | `gateway-service` | `8888` | Reactive Spring Cloud Gateway with dynamic Eureka route discovery |
| **E-Bank Bot** | `ebank-bot` | `8058` | Spring AI chatbot agent connected via MCP to `customer-service` and `ebank-service`, exposed over REST, Telegram, and Discord |

---

## Tech Stack

- **Java 17** & **Spring Boot 3.4.2**
- **Spring Cloud** (Netflix Eureka, OpenFeign, Spring Cloud Gateway, Circuit Breaker)
- **Spring AI** & **Model Context Protocol (MCP)** (`spring-ai-starter-mcp-client`, `spring-ai-starter-model-openai`)
- **Resilience4j** (`@CircuitBreaker` with graceful fallback responses)
- **Telegram Bots Longpolling Starter** (`8.3.0`) & **JDA (Java Discord API)** (`5.3.0`)
- **Spring Data JPA** & **H2 Database**
- **SpringDoc OpenAPI (Swagger UI)**
- **Lombok** & **Maven**

---

## Configuration & Environment Variables

Sensitive keys and tokens are externalized via placeholders in `ebank-bot/src/main/resources/application.properties`:

- `OPENAI_API_KEY` (default placeholder: `YOUR_OPENAI_API_KEY`, model: `gpt-4o-mini`)
- `TELEGRAM_BOT_TOKEN` (default placeholder: `YOUR_TELEGRAM_BOT_TOKEN`)
- `DISCORD_BOT_TOKEN` (default placeholder: `YOUR_DISCORD_BOT_TOKEN`)

---

## Startup Order

1. **`DiscoveryServiceApplication`** (`8761`) — Wait until the Eureka dashboard is accessible at `http://localhost:8761`
2. **`CustomerServiceApplication`** (`8056`) — Seeds initial customer records (`Mohamed`, `Iman`, `Yassin`) and exposes MCP tools (`customer-mcp-server`)
3. **`EbankServiceApplication`** (`8053`) — Seeds bank accounts linked to customer IDs via `CustomerRestClient` and exposes MCP tools (`ebank-mcp-server`)
4. **`GatewayServiceApplication`** (`8888`) — Enables dynamic routing to registered services
5. **`EbankBotApplication`** (`8058`) — Registers with Eureka (`EBANK-BOT`), connects to MCP servers (`8056` and `8053`), and starts REST, Telegram, and Discord interfaces

---

## Key Endpoints & Interfaces

### Eureka Dashboard
- `http://localhost:8761`

### API Gateway Routes
- **Get all customers:** `GET http://localhost:8888/customer-service/customers`
- **Get customer by ID:** `GET http://localhost:8888/customer-service/customers/{id}`
- **Get all bank accounts (with customer details):** `GET http://localhost:8888/ebank-service/accounts`
- **Get bank account by ID:** `GET http://localhost:8888/ebank-service/accounts/{id}`

### E-Bank Bot & Swagger UI Documentation
- **E-Bank Bot Swagger UI:** `http://localhost:8058/swagger-ui/index.html`
- **Customer Service Swagger UI:** `http://localhost:8056/swagger-ui/index.html`
- **E-Bank Service Swagger UI:** `http://localhost:8053/swagger-ui/index.html`
- **Messaging Channels:** REST endpoint (`EBankChatbotController`), Telegram bot, and Discord bot (`Message Content Intent` enabled in Discord Developer Portal)

---

## Fault Tolerance (Resilience4j)

`CustomerRestClient` in `ebank-service` is protected with `@CircuitBreaker(name = "customerService", fallbackMethod = "getDefaultCustomer")`. If `customer-service` becomes unavailable, `ebank-service` returns account data gracefully with `"Not Available"` customer placeholders instead of failing, and automatically recovers once `customer-service` is back online.
