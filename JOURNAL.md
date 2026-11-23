# Gullak Wallet – Development Journal

## Day 1 – Assignment Review and Project Setup

### Objective
Understand the Gullak Wallet assignment and prepare the development environment and project structure.

### Work Completed
- Reviewed the intern assignment document and understood the application requirements.
- Identified the required technologies: Java 21, Quarkus, PostgreSQL, Kafka, OpenLDAP, Docker Compose, Maven, and Robot Framework.
- Created the project directory structure for wallet-api, notification-service, database, LDAP, scripts, and tests.
- Prepared Docker Compose configuration for PostgreSQL, Kafka, and OpenLDAP.
- Started the infrastructure services using Docker Compose.
- Configured PostgreSQL using port 5433 on the host to avoid conflict with the local PostgreSQL service.
- Configured OpenLDAP with the domain `gullak.bank`.
- Created the LDAP organizational units `People` and `Groups` using an LDIF file.
- Successfully added both organizational units to the LDAP directory using `ldapadd`.
- RegisterRequest and RegisterResponse DTO classes were created for the registration API.
- Added fields: fullName, username, password, and email, along with getter and setter methods.
- Created AuthResource for mapping the registration API endpoint.

### Verification
- Verified the Docker containers were running.
- Confirmed the LDAP base DN: `dc=gullak,dc=bank`.
- Confirmed that the `People` and `Groups` organizational units were successfully created.

### Next Steps
- Configure and implement user registration through wallet-api.
- Integrate wallet-api with LDAP for dynamic user creation.
- Implement authentication and role-based authorization.
