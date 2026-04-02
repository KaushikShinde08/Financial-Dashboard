# Equitas Financial Dashboard

An enterprise-grade, full-stack financial management platform engineered with strict Role-Based Access Control (RBAC), deeply integrated security mechanisms, and a pristine light-theme React frontend.

## 🚀 Key Features

- **Strict Role-Based Access Control**:
  - `ADMIN`: Full CRUD management over users and financial records.
  - `ANALYST`: Query-only access to records and aggregate dashboards.
  - `VIEWER`: Strictly walled into high-level dashboard summaries only.
- **Enterprise Security**:
  - Stateless **JWT Authentication** blocking rogue access.
  - Dedicated API **Rate Limiting** via Bucket4j to prevent brute-force attacks (`HTTP 429` protection).
- **Graceful Error Handling**: 
  - Unified `@RestControllerAdvice` intercepting schema validations, returning semantic HTTP codes (`400`, `401`, `403`, `404`) instead of massive stack traces.
- **Data Integrity**: 
  - Implementation of **Soft Deletes**. Users are toggled to `INACTIVE` rather than purged natively from MySQL to ensure relational foreign keys on historical financial ledgers are never orphaned.
- **High-Performance Aggregation**:
  - Backend math offloaded entirely to database-level HQL `GROUP BY` and `COALESCE(SUM())` statements rather than taxing Java stack memory.

## 🛠 Tech Stack
* **Backend**: Java 17, Spring Boot 3.x, Spring Security 6, Spring Data JPA, JWT (jjwt), Bucket4j
* **Database**: MySQL 8.x
* **Frontend**: React (Vite), Axios, Recharts, CSS Variables (Equitas Theme)

## 📦 Setup & Installation

### 1. Database Configuration
Ensure MySQL is running on port `3306`.
```sql
CREATE DATABASE finance_db;
```

### 2. Backend Boot
Open a terminal in the root directory and boot the Spring framework:
```bash
./mvnw clean spring-boot:run
```
*The `application.properties` natively connects to `jdbc:mysql://localhost:3306/finance_db` on `root:root`.*

### 3. Frontend Boot
Open a new terminal inside the `finance-frontend` directory:
```bash
cd finance-frontend
npm install
npm run dev
```
Navigate your browser to the local Vite port (usually `http://localhost:5173`).

## 🗺 API Architecture Summary

| Endpoint Area | Core Purpose | Access Constraints |
| --- | --- | --- |
| `/api/auth/**` | Registration & Authentication | `permitAll()` |
| `/api/users/**` | Managing user states & retrieval | `ADMIN` only |
| `/api/records/**` | Ledger CRUD operations | Write: `ADMIN`, Read: `ADMIN`/`ANALYST` |
| `/api/dashboard/**` | DB-layer metric aggregations | `ADMIN`/`ANALYST`/`VIEWER` |

## ⚖️ Assumptions & Tradeoffs

1. **Tradeoff: Simplified JWT Rotation**: To prioritize backend response speeds and reduce Redis caching dependencies for this hackathon, we assume a relatively short-lived JWT model without a dedicated refresh-token architecture. If an active session expires, the browser logs the user out securely.
2. **Tradeoff: Soft-Deletes Over Cascades**: We consciously trade database storage space in favor of auditing persistence. Deleting users drops them to an inactive state rather than a SQL `CASCADE DELETE`, effectively freezing their historical inputs in time for financial compliance.
3. **Assumption**: We assume that all financial records are logged as absolute positive amounts. The categorical definition of Income vs. Expense acts as the financial mathematical boolean rather than permitting negative digits to ensure human-readability in the Data Tables.
