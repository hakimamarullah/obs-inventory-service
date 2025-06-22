# 📦 Inventory Management API (Dummy Project)

> A modern Spring Boot RESTful application for managing Items, Inventory, and Orders with intelligent stock management and dynamic order processing.

<div align="center">

![Unit Tests](https://github.com/hakimamarullah/obs-inventory-service/actions/workflows/tests.yml/badge.svg?branch=master) ![Build](https://github.com/hakimamarullah/obs-inventory-service/actions/workflows/build.yml/badge.svg?branch=master) ![Build Docker](https://github.com/hakimamarullah/obs-inventory-service/actions/workflows/docker.yml/badge.svg?branch=master)

[🚀 Quick Start](#-quick-start) • [📚 API Docs](#-api-documentation) • [🛠️ Tech Stack](#-tech-stack) • [💾 Database](#-database-setup)

</div>

## ✨ Features

- **Smart Inventory Management** - Real-time stock tracking with top-up and withdrawal operations
- **Dynamic Order Processing** - Automated order number generation and status tracking
- **Multi-Database Support** - H2, PostgreSQL, and Oracle compatibility
- **Interactive API Documentation** - Built-in Swagger UI for testing and exploration
- **Production Ready** - Comprehensive testing, monitoring, and deployment configurations

## 🚀 Quick Start

### Prerequisites

- ☕ Java 21+
- 📦 Maven 3.6+

### Launch Application

```bash
# Clone and run in one command
git clone https://github.com/hakimamarullah/obs-inventory-service && cd obs-inventory-service
./mvnw spring-boot:run
```

🌐 **Application URL:** http://localhost:8080

## 🛠️ Tech Stack

<table>
<tr>
<td><strong>Runtime</strong></td>
<td>Java 21+, Spring Boot 3.x</td>
</tr>
<tr>
<td><strong>Data Layer</strong></td>
<td>Spring Data JPA, Hibernate</td>
</tr>
<tr>
<td><strong>Databases</strong></td>
<td>H2 (dev), PostgreSQL, Oracle</td>
</tr>
<tr>
<td><strong>Documentation</strong></td>
<td>OpenAPI 3.0, Swagger UI</td>
</tr>
</table>

## 📚 API Documentation

### 🔍 Interactive Documentation
- **Swagger UI:** [`localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Spec:** [`localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)


## 💾 Database Setup

### 🔧 H2 Console (Development)
Perfect for testing and development:

```
URL:      http://localhost:8080/h2-console
JDBC:     jdbc:h2:mem:test
Username: sa
Password: (configured in application.properties)
```

### 📊 Database Schema
The application automatically creates these sequences:
- `item_seq` → Item ID generation
- `order_seq` → Order number generation
- `inventory_seq` → Inventory ID generation

### 🌱 Sample Data
Sample items and inventory records are automatically loaded on startup via:
- `schema.sql` → Database schema and sequences
- `import.sql` → Sample data insertion

## 🔧 Development

### Useful SQL Queries

```sql
-- Generate next order number
SELECT NEXT VALUE FOR order_seq;

-- Check inventory levels
SELECT i.name, inv.quantity 
FROM items i 
JOIN inventory inv ON i.id = inv.item_id;

-- View recent orders
SELECT * FROM orders 
ORDER BY created_at DESC 
LIMIT 10;
```

### 🧪 Testing

```bash
# Run all tests
./mvnw test
```

## 📝 Configuration

Key configuration files:
- `application.properties` → Main application settings
- `application-local.properties` → Local development environment
- `application-test.properties` → Testing environment

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
<strong>Built with ❤️ using Spring Boot</strong>
</div>
