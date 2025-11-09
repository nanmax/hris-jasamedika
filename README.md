# HRIS Jasamedika# HRIS Jasamedika



Human Resource Information System built with Spring Boot and Vaadin with PWA support.Human Resource Information System untuk PT Jasamedika.



## 🚀 Features## Features

- PWA (Progressive Web App) Support

- **Employee Management**: Complete CRUD operations for employee data- Mobile-First Responsive Design

- **Attendance System**: Check-in/check-out with timestamp tracking- Employee Management

- **Master Data Management**: Departments, positions, education levels, etc.- Attendance System

- **JWT Authentication**: Secure login system- Dashboard & Reports

- **PWA Support**: Install as mobile app

- **Mobile Responsive**: Optimized for mobile devices## Tech Stack

- **Hamburger Menu**: Mobile-friendly navigation- Java 17

- Spring Boot 3.5.7

## 🛠 Tech Stack- Vaadin 24.9.4

- H2 Database

- **Backend**: Spring Boot 3.5.7, Java 17- Maven

- **Frontend**: Vaadin 24.9.4

- **Database**: H2 (development), PostgreSQL ready for production## Deploy

- **Security**: JWT AuthenticationApplication is deployed on Render.com as a PWA-enabled web application.

- **Build Tool**: Maven

- **PWA**: Progressive Web App ready## Local Development

```bash

## 📱 Mobile Features./mvnw spring-boot:run

```

- Responsive design for all screen sizes

- PWA installable on mobile devicesAccess at: http://localhost:8080
- Touch-friendly interface
- Offline support
- Native app-like experience

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+

### Local Development

```bash
# Clone repository
git clone https://github.com/nanmax/hris-jasamedika.git
cd hris-jasamedika

# Run application
./mvnw spring-boot:run

# Access application
http://localhost:8080
```

### Production Build

```bash
./mvnw clean package -Pproduction
```
