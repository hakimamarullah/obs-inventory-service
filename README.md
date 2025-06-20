# Inventory Management API (Dummy Project)

A Spring Boot RESTful application for managing Items, Inventory, and Orders. Includes support for stock updates via Top-Up and Withdrawal, and dynamic order number generation.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA (with H2, PostgreSQL, Oracle support)
- OpenAPI / Swagger (via springdoc-openapi)
- Hibernate
- H2 Database (in-memory, for development)

---

## 🧪 Running the Application

### Prerequisites

- Java 17+
- Maven 3.6+

### Start the app

```bash
./mvnw spring-boot:run
````

The app will start on:
`http://localhost:8080`

---

## 🔍 Swagger UI & OpenAPI

### API Documentation (Swagger UI)

* 📄 Swagger UI:
  👉 [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui/index.html)

* 🧾 OpenAPI JSON:
  👉 [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)

---

## 🗄️ H2 Console (for development)

* URL: [`http://localhost:8080/h2-console`](http://localhost:8080/h2-console)
* JDBC URL: `jdbc:h2:mem:test`
* User: `sa`
* Password: (see `application.properties`)

---

## 📦 Default Sequences

* `item_seq` – for `Item` entity
* `order_seq` – for order number generation
* `inventory_seq` – for `Inventory` entity

---

## 📁 Data Initialization

Sample data is preloaded via:

* `schema.sql` → creates sequences
* `import.sql` → inserts sample items and inventory

You can modify them in `src/main/resources/`.

---

## 🧰 Useful Commands

### Get Next Order Sequence (example query)

```sql
SELECT NEXT VALUE FOR order_seq;
```