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


## Day 2 – LDAP Registration Implementation & Verification (24 Nov 2026)

### Work Completed
- Created LDAP organizational units: `ou=People` and `ou=Groups`.
- Created LDAP groups: `customer` and `admin`.
- Tested the user registration API.
- Verified that the registered user `anand01` exists in LDAP under `ou=People`.
- Verified the user's name, username and email attributes.
- Confirmed that `anand01` is a member of the `customer` group using `ldapsearch`.

### Issues Faced
- LDAP registration initially failed because `ou=People` and `ou=Groups` were missing.
- LDAP import initially failed due to credential issues during binding.
- Corrected the LDAP import command and successfully created the required LDAP entries.

### Verification
- LDAP user search returned `result: 0 Success`.
- LDAP customer group search returned `result: 0 Success`.
- User entry and customer group membership were both confirmed.

### Next Step
- Plan login scenarios and request/response structure.
- Implement LDAP-based login authentication and validation.


# Day 3 – Daily Work Journal

### Work Completed
1. Implemented the login functionality using LDAP authentication.
2. Integrated manually generated JWT tokens using HMAC-SHA256 (HS256).
3. Added JWT claims such as issuer (`iss`), subject (`sub`), issued-at (`iat`), and expiration (`exp`).
4. Configured the JWT secret using an environment variable.
5. Implemented a custom JWT token validator for signature and claim verification.
6. Created a protected `/secure/test` endpoint to test JWT authentication.
7. Tested the login and protected endpoint using Postman.

### Issue Faced
While testing the protected endpoint through Postman, the `/secure/test` endpoint continued returning `401 Unauthorized` even when a JWT token was provided. Requests without a token were also rejected. Although the custom JWT filter was triggered, authentication with a valid token could not be verified.

### Pending Work
- Troubleshoot and resolve the JWT validation and authentication issue.
- Verify successful access to protected endpoints using a valid token.
- Test missing, invalid, expired, and valid JWT tokens.
