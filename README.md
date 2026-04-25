# Gym CRM Application

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 17**
- **Maven**
- **Git**

## Setup Instructions
Run the following script to create the database and add a user:

```sql
CREATE DATABASE "gym_db";
CREATE USER user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE "gym_db" TO user;
```
