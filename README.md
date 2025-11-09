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

## 🌐 Deploy to Render.com

1. **Fork or use this repository**
2. **Create new Web Service on Render**
3. **Connect GitHub repository**: `nanmax/hris-jasamedika`
4. **Configure deployment**:
   - **Build Command**: `./mvnw clean package -DskipTests -Pproduction`
   - **Start Command**: `java -Dserver.port=$PORT $JAVA_OPTS -jar target/*.jar`
   - **Environment**: Docker
   - **Java Version**: 17 (from system.properties)

### Environment Variables (Optional)

```
VAADIN_PRODUCTION_MODE=true
JAVA_OPTS=-Xmx300m -Xss512k -XX:CICompilerCount=2
DATABASE_URL=your_database_url_here
```

## 📱 Install as Mobile App

After deployment:

1. **Android Chrome**: Open URL → Menu (⋮) → "Add to Home screen"
2. **iOS Safari**: Open URL → Share → "Add to Home Screen" 
3. **Desktop Chrome**: Address bar install icon

## 🔧 Configuration

- **Database**: Configured for H2 (dev) and PostgreSQL (production)
- **JWT Secret**: Auto-generated for security
- **PWA**: Pre-configured with icons and manifest
- **Mobile CSS**: Responsive design included

## 📦 Project Structure

```
src/
├── main/
│   ├── java/com/nanmax/hris/
│   │   ├── config/          # Security & Web configuration
│   │   ├── controller/      # REST API controllers
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business logic
│   │   ├── ui/              # Vaadin UI components
│   │   └── security/        # JWT & Authentication
│   ├── resources/
│   │   ├── db/migration/    # Database migrations
│   │   └── static/          # CSS, icons, offline page
│   └── frontend/            # Vaadin frontend
└── test/                    # Unit tests
```

## 🔐 Default Login

```
Email: admin@jasamedika.com
Password: admin123
```

## 📱 PWA Features

- **Offline Support**: Works without internet
- **App Icons**: Custom HRIS icons
- **Splash Screen**: Professional loading screen
- **Mobile Navigation**: Hamburger menu
- **Touch Optimized**: Mobile-first design

## 🚀 Deployment Status

- ✅ **Production Ready**: Optimized build configuration
- ✅ **Cloud Ready**: Environment variables support
- ✅ **Mobile Ready**: PWA configuration complete
- ✅ **Security Ready**: JWT authentication implemented
- ✅ **Database Ready**: Migration scripts included

## 📞 Support

For technical support or questions about this HRIS system, please create an issue in this repository.

---

**Built with ❤️ for Jasamedika**