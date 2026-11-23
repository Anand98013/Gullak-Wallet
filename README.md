# Gullak Wallet

Gullak Wallet is a backend wallet application developed as part of an internship assignment. The project focuses on building REST APIs for user registration, authentication, wallet management, money transfers, and notifications.

The application uses a microservice-based architecture with separate services for wallet operations and notification processing.

## Project Objectives

- Implement user registration and authentication.
- Manage user wallets and account balances.
- Support secure money transfers between accounts.
- Maintain transactional consistency during transfers.
- Integrate LDAP for authentication and user access management.
- Use Kafka for asynchronous transfer event processing.
- Store account, transfer, and notification data in PostgreSQL.
- Implement role-based access control for customers and administrators.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend development |
| Quarkus 3.15 | REST API framework |
| Maven 3.9 | Build and dependency management |
| PostgreSQL 17 | Relational database |
| Apache Kafka 4.x | Event messaging |
| OpenLDAP 1.5.0 | Identity and access management |
| Docker Compose v2 | Container orchestration |
| JUnit 5 | Unit and integration testing |
| Robot Framework 7.x | API testing and automation |

## Architecture

The application consists of two independent services:

### 1. Wallet API
Responsible for:
- User registration and login
- Wallet and account operations
- Balance validation
- Money transfers
- Database transactions
- Publishing transfer events to Kafka

### 2. Notification Service
Responsible for:
- Consuming transfer events from Kafka
- Processing transfer notifications
- Creating notifications for sender and receiver
- Storing notification records in PostgreSQL

## Transfer Workflow

1. Receive a transfer request.
2. Validate request data and account IDs.
3. Check the sender's available balance.
4. Execute debit and credit operations within a database transaction.
5. Save the transfer record.
6. Commit the transaction.
7. Publish a transfer event to Kafka.
8. Notification Service consumes the event and creates notifications for both parties.

## Project Structure

```text
gullak-wallet/
├── wallet-api/
├── notification-service/
├── db/
│   └── schema.sql
├── ldap/
│   └── users.ldif
├── scripts/
├── tests/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── JOURNAL.md
```

## Security

- LDAP-based identity and access management.
- Role-based authorization for customers and administrators.
- Customers can access their own accounts.
- Administrators can access accounts according to their assigned permissions.
- Credentials and sensitive configuration are kept out of version control.
- Wallet balance updates are handled by the Wallet API.

## Project Status

This project is currently under development. APIs, service integrations, database operations, authentication, and testing are being implemented incrementally.

## Author

**Anand Kumar**

Backend Development Intern

