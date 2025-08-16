# MS-User - Qwiki Project

## 📋 Description

`ms-user` is a backend microservice developed with **Spring Boot** for the **Qwiki** project, an intelligent delivery platform. This microservice handles authentication, authorization, and user role management (Admin, Client, Merchant, Delivery Person), ensuring secure access to other microservices via **JWT**.

## 🚀 Key Features

### Authentication & Authorization
- **JWT-based Authentication** with access and refresh tokens
- **Role-based Access Control** (RBAC) with 4 user roles:
  - **CLIENT** - Default role for regular users
  - **LIVREUR** - Delivery personnel with vehicle documentation
  - **COMMERCANT** - Merchants with business registration
  - **ADMIN** - System administrators
- **Multi-role Support** - Users can have multiple roles simultaneously
- **Account Activation System** - Role upgrade requests require admin approval

### User Management
- Complete CRUD operations for user profiles
- **Role Upgrade System** - Users can request to become merchants or delivery personnel
- **Role Downgrade System** - Admins can revoke special roles
- **Profile Management** - Update personal information, contact details
- **Account Status Management** - Activate/deactivate user accounts

### Business Logic
- **Merchant Store Management** - Create and manage boutiques/shops
- **Working Hours Management** - Set flexible business hours for stores
- **Business Registration** - Handle merchant documentation and verification
- **Delivery Personnel Verification** - Manage delivery person credentials and vehicle papers

### Security Features
- **JWT Token Management** with blacklist for logout
- **Password Encryption** using BCrypt
- **CORS Configuration** for cross-origin requests
- **Role-based Endpoint Protection**
- **Token Validation Service** for inter-microservice communication

## 🛠️ Technologies Used

### Backend Framework
- **Java 21** - Latest LTS version for optimal performance
- **Spring Boot 3.4.3** - Modern enterprise application framework
- **Spring Security 6** - Comprehensive security framework
- **Spring Data JPA** - Database abstraction layer
- **Spring Data REST** - RESTful API generation

### Security & Authentication
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing algorithm
- **Spring OAuth2 Client** - Third-party authentication support

### Database & Persistence
- **MySQL** - Primary database for production
- **JPA/Hibernate** - Object-relational mapping
- **Connection Pooling** - Optimized database connections

### Communication & Integration
- **Spring Cloud OpenFeign** - Inter-microservice communication
- **Apache Kafka** - Event-driven messaging system
- **Eureka Client** - Service discovery and registration

### Development Tools
- **Lombok** - Code generation and boilerplate reduction
- **Maven** - Dependency management and build automation
- **Docker** - Containerization and deployment

## 🏗️ Database Schema

### User Entity
```sql
- id (Primary Key)
- firstName, lastName
- email (Unique identifier)
- phone, address
- password (Encrypted)
- active (Boolean)
- gender, age
- profileImage
- roles (Many-to-Many with Role enum)
```

### Role-Specific Entities
```sql
Livreur:
- cartNationalId
- vehiclePapiers
- user_id (One-to-One)

Commercant:
- cartNationalId
- type
- user_id (One-to-One)

Boutique:
- nom, adresse
- commercant_id (Many-to-One)
- heuresTravail (One-to-Many)
```

## 📡 API Endpoints

### Authentication Endpoints
```http
POST /api/v1/auth/register          # User registration
POST /api/v1/auth/authenticate      # Login with credentials
POST /api/v1/auth/verify-token      # Token validation service
POST /api/v1/auth/logout           # Secure logout with token blacklist
```

### User Management
```http
PUT  /api/v1/auth/user/{userId}     # Update user profile
GET  /api/v1/auth/users/{id}        # Get user by ID
GET  /api/v1/auth/users-by-role     # Filter users by role
POST /api/v1/auth/active/{userId}   # Activate user account
```

### Role Management
```http
POST /api/v1/auth/upgrade-to-livreur/{userId}      # Request delivery role
POST /api/v1/auth/upgrade-to-commercant/{userId}   # Request merchant role
POST /api/v1/auth/downgrade-from-livreur/{userId}  # Remove delivery role
POST /api/v1/auth/downgrade-from-commercant/{userId} # Remove merchant role
```

### Business Management
```http
POST /api/v1/auth/create-boutique/{userId}           # Create merchant store
POST /api/v1/auth/set-working-hours/{userId}/{boutiqueId} # Set store hours
```

## ⚙️ Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/qwiki_users
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=${JWT_SECRET:your-secret-key}
jwt.expiration=86400000  # 24 hours

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

## 🐳 Docker Deployment

### Build and Run
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t qwiki/ms-user:latest .

# Run with Docker Compose
docker-compose up -d
```

### Docker Compose Services
- **MySQL Database** - User data persistence
- **phpMyAdmin** - Database administration interface
- **Redis** - Token caching and session management

## 🔐 Security Implementation

### JWT Token Structure
```json
{
  "sub": "user@example.com",
  "roles": ["CLIENT", "COMMERCANT"],
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Role-based Access Control
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('COMMERCANT')")
@PreAuthorize("hasAnyRole('ADMIN', 'COMMERCANT')")
```

### Password Security
- **BCrypt Hashing** - Industry-standard password encryption
- **Salt Generation** - Unique salt for each password
- **Password Strength Validation** - Configurable password policies

## 📊 Event-Driven Architecture

### Kafka Integration
```java
// User role changes trigger events
kafkaPublisher.sendMessage("user-role-changed", userEvent);

// Business registration events
kafkaPublisher.sendMessage("merchant-registered", merchantEvent);

// Delivery personnel verification
kafkaPublisher.sendMessage("livreur-verified", livreurEvent);
```

### Event Types
- **UserRegistered** - New user account creation
- **RoleUpgradeRequested** - Role change requests
- **MerchantVerified** - Business verification completion
- **LivreurActivated** - Delivery personnel approval

## 🧪 Testing

### Postman Collection
A comprehensive Postman collection is included (`postman.json`) with:
- Authentication workflows
- Role management scenarios
- Business registration flows
- Error handling test cases

### Test Data
```bash
# Default admin account
Email: admin@gmail.com
Password: admin
Roles: [ADMIN]
```

## 📈 Performance & Scalability

### Optimization Features
- **Connection Pooling** - Efficient database connections
- **JWT Stateless Design** - Horizontal scalability
- **Lazy Loading** - Optimized entity relationships
- **Caching Strategy** - Redis for token management

### Monitoring
- **Health Checks** - Spring Actuator endpoints
- **Metrics Collection** - Application performance monitoring
- **Logging Framework** - Structured application logs

## 🚀 Deployment Architecture

### Microservices Integration
```
API Gateway → JWT Validation → MS-User
           ↘ Role Authorization → Other Services
```

### Service Discovery
- **Eureka Registration** - Automatic service discovery
- **Load Balancing** - Client-side load balancing
- **Health Monitoring** - Service availability tracking

## 🛡️ Security Best Practices

### Implementation Highlights
- **Token Blacklisting** - Secure logout implementation
- **Role Separation** - Principle of least privilege
- **Input Validation** - Comprehensive request validation
- **CORS Configuration** - Cross-origin security
- **SQL Injection Prevention** - Parameterized queries

## 📝 Business Logic

### User Lifecycle
1. **Registration** - Create account with CLIENT role
2. **Profile Setup** - Complete user information
3. **Role Request** - Apply for merchant/delivery roles
4. **Verification Process** - Admin approval workflow
5. **Business Setup** - Create stores and set working hours
6. **Activation** - Full platform access

### Role Management Rules
- Users cannot be both COMMERCANT and LIVREUR simultaneously
- Role upgrades require account deactivation pending approval
- Admins can upgrade/downgrade any user roles
- Business registration requires valid documentation

## 🔧 Development Setup

### Prerequisites
```bash
# Required software
- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Docker & Docker Compose
- Kafka (optional for events)
```

### Local Development
```bash
# Clone repository
git clone https://github.com/your-repo/ms-user.git

# Configure database
# Update application.properties with your MySQL credentials

# Run application
mvn spring-boot:run

# Access APIs
http://localhost:8080/api/v1/auth
```

## 🐛 Troubleshooting

### Common Issues
1. **Database Connection** - Verify MySQL credentials and port
2. **JWT Token Errors** - Check token expiration and format
3. **Role Access Denied** - Verify user roles and permissions
4. **Kafka Connection** - Ensure Kafka is running for events

## 🔄 Future Enhancements

### Planned Features
- **OAuth2 Integration** - Google/Facebook login
- **Multi-tenant Support** - Organization-based isolation
- **Advanced Role Permissions** - Granular access control
- **Audit Logging** - Complete user action tracking
- **Rate Limiting** - API request throttling
- **2FA Authentication** - Enhanced security options

## 📄 License

This project is part of the Qwiki platform and is proprietary software developed for educational and commercial purposes.
