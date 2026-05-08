# Gym CRM Application

![Build](https://github.com/juliakhomyn/gym-crm/actions/workflows/ci.yml/badge.svg?branch=develop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=juliakhomyn_gym-crm&metric=coverage)](https://sonarcloud.io/summary/overall?id=juliakhomyn_gym-crm)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=juliakhomyn_gym-crm&metric=alert_status)](https://sonarcloud.io/summary/overall?id=juliakhomyn_gym-crm)

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 17**
- **Maven**
- **Git**

## Setup Instructions
Run the following script to create the database and add a user:

```sql
CREATE DATABASE gym_db;
CREATE USER 'gymuser'@'localhost' IDENTIFIED BY 'gympass';
GRANT ALL PRIVILEGES ON gym_db.* TO 'gymuser'@'localhost';
FLUSH PRIVILEGES;
```
